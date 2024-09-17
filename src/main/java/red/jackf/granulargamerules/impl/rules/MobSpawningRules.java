package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.GGDeferredChecker;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MobSpawningRules {
    private static final Utils.RuleGenerator<GameRules.BooleanValue> GENERATOR = Utils.createGenerator(GameRules.RULE_DOMOBSPAWNING, true, GameRuleFactory::createBooleanRule);

    public static final GameRules.Key<GameRules.BooleanValue> MONSTER = create("monster");
    public static final GameRules.Key<GameRules.BooleanValue> CREATURE = create("creature");
    public static final GameRules.Key<GameRules.BooleanValue> AMBIENT = create("ambient");
    public static final GameRules.Key<GameRules.BooleanValue> AXOLOTLS = create("axolotls");
    public static final GameRules.Key<GameRules.BooleanValue> UNDERGROUND_WATER_CREATURE = create("undergroundWaterCreature");
    public static final GameRules.Key<GameRules.BooleanValue> WATER_CREATURE = create("waterCreature");
    public static final GameRules.Key<GameRules.BooleanValue> WATER_AMBIENT = create("waterAmbient");

    public static final GameRules.Key<GameRules.BooleanValue> FORCE_ENABLE_TRIAL_SPAWNERS
            = Utils.createChild(GameRules.RULE_DOMOBSPAWNING, "forceEnableTrialSpawners", GameRuleFactory.createBooleanRule(false));

    private static final List<MobCategory> SPAWNING_CATEGORIES = Stream.of(MobCategory.values())
            .filter(mobCategory -> mobCategory != MobCategory.MISC)
            .toList();

    /**
     * Check if any overrides are present for spawning, in case doMobSpawning is false
     */
    public static boolean anySpawningOverrides(ServerLevel level) {
        return SPAWNING_CATEGORIES.stream().anyMatch(category -> isSpawningAllowed(level, category));
    }

    /**
     * Check if a certain mob category is allowed to be spawned
     */
    public static boolean isSpawningAllowed(ServerLevel level, MobCategory category) {
        var rules = level.getGameRules();

        return (switch (category) {
            case MONSTER:
                yield GGDeferredChecker.getBoolean(rules, MONSTER);
            case CREATURE:
                yield GGDeferredChecker.getBoolean(rules, CREATURE);
            case AMBIENT:
                yield GGDeferredChecker.getBoolean(rules, AMBIENT);
            case AXOLOTLS:
                yield GGDeferredChecker.getBoolean(rules, AXOLOTLS);
            case UNDERGROUND_WATER_CREATURE:
                yield GGDeferredChecker.getBoolean(rules, UNDERGROUND_WATER_CREATURE);
            case WATER_CREATURE:
                yield GGDeferredChecker.getBoolean(rules, WATER_CREATURE);
            case WATER_AMBIENT:
                yield GGDeferredChecker.getBoolean(rules, WATER_AMBIENT);
            default: yield  Optional.<Boolean>empty();
        }).orElse(rules.getBoolean(GameRules.RULE_DOMOBSPAWNING));
    }

    private static GameRules.Key<GameRules.BooleanValue> create(String name) {
        return GENERATOR.create(name);
    }

    public static void setup() {

    }
}
