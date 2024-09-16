package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;
import red.jackf.granulargamerules.impl.GGDeferredChecker;

import java.util.Optional;

public class UniversalAngerRules {
    private static final Utils.RuleGenerator<GameRules.BooleanValue> GENERATOR = Utils.createGenerator(GameRules.RULE_UNIVERSAL_ANGER, false, GameRuleFactory::createBooleanRule);

    public static final GameRules.Key<GameRules.BooleanValue> BEES_UNIVERSAL_ANGER = create("bees");
    public static final GameRules.Key<GameRules.BooleanValue> ENDERMEN_UNIVERSAL_ANGER = create("endermen");
    public static final GameRules.Key<GameRules.BooleanValue> IRON_GOLEM_UNIVERSAL_ANGER = create("ironGolems");
    public static final GameRules.Key<GameRules.BooleanValue> LLAMA_UNIVERSAL_ANGER = create("llamas");
    public static final GameRules.Key<GameRules.BooleanValue> PIGLINS_UNIVERSAL_ANGER = create("piglins");
    public static final GameRules.Key<GameRules.BooleanValue> WOLVES_UNIVERSAL_ANGER = create("wolves");
    public static final GameRules.Key<GameRules.BooleanValue> ZOMBIFIED_PIGLINS_UNIVERSAL_ANGER = create("zombifiedPiglins");

    public static Optional<Boolean> testOverride(GameRules instance, Object mob) {
        @Nullable
        GameRules.Key<GameRules.BooleanValue> override = switch (mob) {
            case Bee ignored: yield BEES_UNIVERSAL_ANGER;
            case EnderMan ignored: yield ENDERMEN_UNIVERSAL_ANGER;
            case IronGolem ignored: yield IRON_GOLEM_UNIVERSAL_ANGER;
            case Llama ignored: yield LLAMA_UNIVERSAL_ANGER;
            case Piglin ignored: yield PIGLINS_UNIVERSAL_ANGER;
            case Wolf ignored: yield WOLVES_UNIVERSAL_ANGER;
            case ZombifiedPiglin ignored: yield ZOMBIFIED_PIGLINS_UNIVERSAL_ANGER;
            default: yield null;
        };

        if (override != null) {
            return GGDeferredChecker.getBoolean(instance, override);
        } else {
            return Optional.empty();
        }
    }

    private static GameRules.Key<GameRules.BooleanValue> create(String name) {
        return GENERATOR.create(name);
    }

    public static void setup() {
        // no-op
    }
}
