@file:Suppress("UnstableApiUsage", "RedundantNullableReturnType")

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import me.modmuss50.mpp.ReleaseType
import net.fabricmc.loom.task.RemapJarTask
import org.ajoberstar.grgit.Grgit
import org.eclipse.jgit.api.errors.NoHeadException
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import red.jackf.GenerateChangelogTask
import red.jackf.UpdateDependenciesTask

plugins {
	id("maven-publish")
	id("fabric-loom") version "1.7-SNAPSHOT"
	id("org.jetbrains.kotlin.jvm") version "2.0.0"
	id("com.github.breadmoirai.github-release") version "2.4.1"
	id("org.ajoberstar.grgit") version "5.2.1"
	id("me.modmuss50.mod-publish-plugin") version "0.3.3"
}

val grgit: Grgit? = project.grgit

var canPublish = grgit != null && System.getenv("RELEASE") != null

fun getVersionSuffix(): String {
	return grgit?.branch?.current()?.name ?: "nogit+${properties["minecraft_version"]}"
}

group = properties["maven_group"]!!

if (System.getenv().containsKey("NEW_TAG")) {
	version = System.getenv("NEW_TAG").substring(1)
} else {
	val versionStr = "${properties["mod_version"]}+${properties["minecraft_version"]!!}"
	canPublish = false
	version = if (grgit != null) {
		try {
			"$versionStr+dev-${grgit.log()[0].abbreviatedId}"
		} catch (ex: NoHeadException) {
			"$versionStr+dev-nogithead"
		}
	} else {
		"$versionStr+dev-nogit"
	}
}

repositories {
	// Parchment Mappings
	maven {
		name = "ParchmentMC"
		url = uri("https://maven.parchmentmc.org")
		content {
			includeGroup("org.parchmentmc.data")
		}
	}

	// Mod Menu, EMI
	maven {
		name = "TerraformersMC"
		url = uri("https://maven.terraformersmc.com/releases/")
		content {
			includeGroup("com.terraformersmc")
			includeGroup("dev.emi")
		}
	}

	// YACL
	maven {
		name = "Xander Maven"
		url = uri("https://maven.isxander.dev/releases")
		content {
			includeGroup("dev.isxander")
			includeGroupAndSubgroups("org.quiltmc")
		}
	}

	// YACL Snapshots
	maven {
		name = "Xander Snapshot Maven"
		url = uri("https://maven.isxander.dev/snapshots")
		content {
			includeGroup("dev.isxander")
			includeGroupAndSubgroups("org.quiltmc")
		}
	}

	// JackFredLib
	maven {
		name = "JackFredMaven"
		url = uri("https://maven.jackf.red/releases")
		content {
			includeGroupAndSubgroups("red.jackf")
		}
	}
}

java {
	withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

	mods {
		create("granulargamerules") {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
		}
	}

	log4jConfigs.from(file("log4j2.xml"))

	runs.configureEach {
		programArgs.addAll("--username JackFred".split(" "))
		vmArgs.addAll("-Dfabric-tag-conventions-v2.missingTagTranslationWarning=VERBOSE".split(" "))
	}

	accessWidenerPath.set(file("src/main/resources/granulargamerules.accesswidener"))
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${properties["parchment_version"]}@zip")
	})
	modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")
	include(implementation(annotationProcessor("io.github.llamalad7:mixinextras-fabric:${properties["mixinextras_version"]}")!!)!!)

	include(modApi("red.jackf.jackfredlib:jackfredlib:${properties["jackfredlib_version"]}")!!)

	modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric-api_version"]}")

	// Config
	modImplementation("dev.isxander:yet-another-config-lib:${properties["yacl_version"]}") {
		exclude(group = "com.terraformersmc", module = "modmenu")
	}

	// COMPATIBILITY
	modCompileOnly("com.terraformersmc:modmenu:${properties["modmenu_version"]}")
	modLocalRuntime("com.terraformersmc:modmenu:${properties["modmenu_version"]}")

	//modCompileOnly("dev.emi:emi-fabric:${properties["emi_version"]}:api")
	modLocalRuntime("dev.emi:emi-fabric:${properties["emi_version"]}")
}

tasks.withType<ProcessResources>().configureEach {
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand(inputs.properties)
	}
}

tasks.named<Jar>("sourcesJar") {
	dependsOn(tasks.classes)
	archiveClassifier.set("sources")
	from(sourceSets.main.get().allSource)
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${properties["archivesBaseName"]}"}
	}
}

tasks.withType<JavaCompile> {
	options.release.set(21)
}

tasks.withType<KotlinCompile> {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_21)
	}
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"]!!)
		}
	}

	repositories {
		// if not in CI we publish to maven local
		if (!System.getenv().containsKey("CI")) repositories.mavenLocal()

		if (canPublish) {
			maven {
				name = "JackFredMaven"
				url = uri("https://maven.jackf.red/releases/")
				content {
					includeGroupByRegex("red.jackf.*")
				}
				credentials {
					username = properties["jfmaven.user"]?.toString() ?: System.getenv("JACKFRED_MAVEN_USER")
					password = properties["jfmaven.key"]?.toString() ?: System.getenv("JACKFRED_MAVEN_PASS")
				}
			}
		}
	}
}

if (canPublish) {
	// Changelog Generation
	val lastTag = if (System.getenv("PREVIOUS_TAG") == "NONE") null else System.getenv("PREVIOUS_TAG")
	val newTag = "v$version"

	var generateChangelogTask: TaskProvider<GenerateChangelogTask>? = null

	// Changelog Generation
	if (lastTag != null) {
		val changelogHeader = if (properties.containsKey("changelogHeaderAddon")) {
			val addonProp: String = properties["changelogHeaderAddon"]!!.toString()

			if (addonProp.isNotBlank()) {
				addonProp
			} else {
				null
			}
		} else {
			null
		}

		val changelogFileText = rootProject.file("changelogs/${properties["mod_version"]}.md")
			.takeIf { it.exists() }
			?.readText()

		generateChangelogTask = tasks.register<GenerateChangelogTask>("generateChangelog") {
			this.lastTag.set(lastTag)
			this.newTag.set(newTag)
			githubUrl.set(properties["github_url"]!!.toString())
			prefixFilters.set(properties["changelog_filter"]!!.toString().split(","))

			val bundledText = """
                |Bundled:
                |  - JackFredLib: ${properties["jackfredlib_version"]}
                |  - Mixin Extras: ${properties["mixinextras_version"]}
                |  """.trimMargin()

			// Add a bundled block for each module version
			prologue.set(listOfNotNull(changelogHeader, changelogFileText, bundledText).joinToString(separator = "\n\n"))
		}
	}

	val changelogTextProvider = if (generateChangelogTask != null) {
		provider {
			generateChangelogTask!!.get().changelogFile.get().asFile.readText()
		}
	} else {
		provider {
			"No Changelog Generated"
		}
	}

	// GitHub Release
	tasks.named<GithubReleaseTask>("githubRelease") {
		generateChangelogTask?.let { dependsOn(it) }

		authorization = System.getenv("GITHUB_TOKEN")?.let { "Bearer $it" }
		owner = properties["github_owner"]!!.toString()
		repo = properties["github_repo"]!!.toString()
		tagName = newTag
		releaseName = "${properties["mod_name"]} $newTag"
		targetCommitish = grgit!!.branch.current().name
		releaseAssets.from(
			tasks["remapJar"].outputs.files,
			tasks["remapSourcesJar"].outputs.files,
		)
		subprojects.forEach {
			releaseAssets.from(
				it.tasks["remapJar"].outputs.files,
				it.tasks["remapSourcesJar"].outputs.files,
			)
		}

		body = changelogTextProvider
	}

	// Mod Platforms
	if (listOf("CURSEFORGE_TOKEN", "MODRINTH_TOKEN").any { System.getenv().containsKey(it) }) {
		publishMods {
			changelog.set(changelogTextProvider)
			type.set(when(properties["release_type"]) {
				"release" -> ReleaseType.STABLE
				"beta" -> ReleaseType.BETA
				else -> ReleaseType.ALPHA
			})
			modLoaders.add("fabric")
			modLoaders.add("quilt")
			file.set(tasks.named<RemapJarTask>("remapJar").get().archiveFile)

			if (System.getenv().containsKey("CURSEFORGE_TOKEN") || dryRun.get()) {
				curseforge {
					projectId.set(properties["cf_project_id"]!!.toString())
					accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
					properties["game_versions_curse"]!!.toString().split(",").forEach {
						minecraftVersions.add(it)
					}
					displayName.set("${properties["prefix"]!!} ${properties["mod_name"]!!} ${version.get()}")
					listOf("fabric-api", "yacl").forEach {
						requires {
							slug.set(it)
						}
					}
					listOf("modmenu").forEach {
						optional {
							slug.set(it)
						}
					}
				}
			}

			if (System.getenv().containsKey("MODRINTH_TOKEN") || dryRun.get()) {
				modrinth {
					accessToken.set(System.getenv("MODRINTH_TOKEN"))
					projectId.set(properties["mr_project_id"]!!.toString())
					properties["game_versions_mr"]!!.toString().split(",").forEach {
						minecraftVersions.add(it)
					}
					displayName.set("${properties["mod_name"]!!} ${version.get()}")
					listOf("fabric-api", "yacl").forEach {
						requires {
							slug.set(it)
						}
					}
					listOf("modmenu").forEach {
						optional {
							slug.set(it)
						}
					}
				}
			}
		}
	}
}

tasks.register<UpdateDependenciesTask>("updateModDependencies") {
	mcVersion.set(properties["minecraft_version"]!!.toString())
	loader.set("fabric")
}