package red.jackf.granulargamerules.impl;

import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.jackf.granulargamerules.impl.config.GGConfig;
import red.jackf.granulargamerules.impl.rules.MiscRules;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;
import red.jackf.granulargamerules.impl.rules.MobSpawningRules;
import red.jackf.granulargamerules.impl.rules.UniversalAngerRules;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GranularGamerules implements ModInitializer {
    public static final String ID = "granulargamerules";
    public static final Component MOD_TITLE = Component.literal("Granular Gamerules");
    public static final Logger LOGGER = LoggerFactory.getLogger("granulargamerules");

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    // child to parent
    private static final Map<GameRules.Key<?>, GameRules.Key<?>> DEFERRABLE_GAMERULES = new HashMap<>();
    private static final Map<GameRules.Key<?>, GameRules.Key<?>> PARENT_GAMERULES = new HashMap<>();

    @Override
    public void onInitialize() {
        GGConfig.INSTANCE.load();

        MobGriefingRules.setup();
        MobSpawningRules.setup();
        UniversalAngerRules.setup();
        MiscRules.setup();

        GGCommand.setup();
    }

    public static void setVisualParent(GameRules.Key<?> child, GameRules.Key<?> parent) {
        PARENT_GAMERULES.put(child, parent);
    }

    public static <T extends GameRules.Value<T>> Optional<GameRules.Key<T>> getVisualParent(GameRules.Key<T> child) {
        //noinspection unchecked
        return Optional.ofNullable((GameRules.Key<T>) PARENT_GAMERULES.get(child));
    }

    public static <T extends GameRules.Value<T>> Set<GameRules.Key<T>> getVisualChildren(GameRules.Key<T> parent) {
        //noinspection unchecked
        return PARENT_GAMERULES.entrySet().stream()
                .filter(entry -> entry.getValue() == parent)
                .map(entry -> (GameRules.Key<T>) entry.getKey())
                .collect(Collectors.toSet());
    }

    public static <T extends GameRules.Value<T>> void setDeferrable(GameRules.Key<T> child, GameRules.Key<T> parent) {
        DEFERRABLE_GAMERULES.put(child, parent);
        setVisualParent(child, parent);
    }

    public static boolean isDeferrable(GameRules.Key<?> key) {
        return DEFERRABLE_GAMERULES.containsKey(key);
    }

    public static <T extends GameRules.Value<T>> Optional<GameRules.Key<T>> getDeferredParent(GameRules.Key<T> child) {
        //noinspection unchecked
        return Optional.ofNullable((GameRules.Key<T>) DEFERRABLE_GAMERULES.get(child));
    }
}