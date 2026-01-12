# Archived

GG has been archived so I can stay focused on a smaller amount of mods for now. Given modern versions now have data-driven rules, and the ugliness of GG's client code it would make more sense to be a rewrite in the future.

# 🌍 Granular Gamerules

![A meme explaining the mod - various vanilla gamerules such as mobGriefing are split into multiple.](https://cdn.modrinth.com/data/YFUweSka/images/949475b58bad6455b583f4d45051ff42acc6e0cb.png)

Grants you more control over the vanilla game rule system by:

- Breaking up some of Minecraft's most overloaded game rules, such as `mobGriefing`, `doMobSpawning` or `universalAnger`, into deferrable sub-rules.
- Adding smaller utility rules such as turning off thunder or removing underground players from the sleep vote.
- On dedicated servers, allowing some common properties in `server.properties` to be changed via fake game rules, such as `pvp` or `enableCommandBlock`.

These are all done in a way that doesn't break datapacks, mods or server plugins that change game rules, while letting you 'pin' features on or off.

There are 37 new sub-rules, 3 miscellaneous rules and 8 `server.properties` rules; for a full description of each see [the Wiki](https://docs.jackf.red/granular-gamerules).

<details>
<summary>Full list of rules</summary>

- mobGriefing
  - creaturesEatPlants
  - creepersDestroyBlocks
  - endermenMoveBlocks
  - enderDragonDestroysBlocks
  - evokersWololo
  - ghastsDestroyBlocks
  - itemsTakenByAllays
  - itemsTakenByPiglins
  - itemsTakenByOthers
  - mobsCrushTurtleEggs
  - mobsTrampleFarmland
  - ravagersDestroyPlants
  - silverfishEnterStone
  - silverfishWakeFriends
  - snowGolemsLeaveTrails
  - withersDestroyBlocks
  - zombiesBreakDoors
- doMobSpawning
  - creature
  - monster
  - ambient
  - waterCreature
  - undergroundWaterCreature
  - waterAmbient
  - axolotl
- randomTickSpeed
  - extraCopperTicks
  - extraCropTicks
  - extraFarmlandTicks
  - extraLeafTicks
  - extraSaplingTicks
  - extraSpreadingTerrainTicks
- universalAnger
  - bees
  - endermen
  - ironGolems
  - llamas
  - piglins
  - wolves
  - zombifiedPiglins
- `server.properties` psuedo-gamerules
  - pvp
  - allowFlight
  - enableCommandBlock
  - maxPlayers
  - spawnProtection
  - simulationDistance
  - viewDistance
  - entityBroadcastRangePercentage
- doWeatherCycle/enableThunder
- lavaSourceConversion/onlyInNetherBiomes
- playersSleepingPercentage/countUnderground
- playersTrampleFarmland
- waterSourceConversion/onlyInWetBiomes

</details>

## 🛌 New Miscellaneous Rules

**Granular Gamerules** adds a couple new rules you might find yourself wanting:

- Removing underground players from the sleep vote - no longer prevented from sleeping due to miners.
- Disabling thunder from the weather cycle.
- Stopping players from trampling farmland.
- Filtering water & lava source conversions by biome

These are disabled by default, and details are available on the wiki.

## 🖥️ Dedicated Server Rules

You may have setup a dedicated server and realised you forgot to change some settings in `server.properties`. **Granular Gamerules** lets you change several of these in-game via psuedo-gamerules:

- `pvp`
- `allowFlight`
- `enableCommandBlock`
- `maxPlayers`
- `spawnProtection`
- `viewDistance`
- `simulationDistance`
- `entityBroadcastRangePercentage`

These are backed by the actual `server.properties` file, and will update the file on change.

## 👩‍👧‍👦 Sub-Rules (Datapack & Server Plugin backwards compatibility)

Granular Gamrules lets you change parts of gamerules when you need them. Other parts will follow the parent gamerule until they are changed.

For example, if you wanted to specifically stop creepers blowing up blocks, you'd use `/gamerule mobGriefing/creepersDestroyBlocks false`. Other mobGriefing sub-rules such as Zombies breaking doors, will follow the parent `/gamerule mobGriefing` until changed. 

# 🛠️ Install & Requirements

Can work server-side only, in the case of dedicated servers.

**Granular Gamerules** requires [Fabric API](https://modrinth.com/mod/fabric-api) and [YACL](https://modrinth.com/mod/yacl), and should be good to add or remove to an existing world.

<details>

<summary>Dev Stuff</summary>

## Command Implementation

'Overlays' defer nodes and fake gamerules by re-registering the 'gamerule' node. Should be fine, seems to just add.

</details>
