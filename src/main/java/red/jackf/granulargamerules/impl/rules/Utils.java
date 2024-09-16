package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.GranularGamerules;

import java.util.function.Function;

public class Utils {
    public static <V extends GameRules.Value<V>, V2> RuleGenerator<V> createGenerator(GameRules.Key<V> parent, V2 defaultValue, Function<V2, GameRules.Type<V>> factory) {
        GameRules.Category category = parent.getCategory();
        String prefix = parent.getId() + "/";

        return name -> {
            GameRules.Key<V> rule = GameRuleRegistry.register(prefix + name, category, factory.apply(defaultValue));
            GranularGamerules.add(rule, parent);

            return rule;
        };
    }

    public interface RuleGenerator<V extends GameRules.Value<V>> {
        GameRules.Key<V> create(String ruleName);
    }
}
