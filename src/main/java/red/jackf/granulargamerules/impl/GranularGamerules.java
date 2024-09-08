package red.jackf.granulargamerules.impl;

import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GranularGamerules implements ModInitializer {
    public static final String ID = "granulargamerules";
    public static final Component MOD_TITLE = Component.literal("Granular Gamerules");
    public static final Logger LOGGER = LoggerFactory.getLogger("granulargamerules");

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    // child to parent
    private static final Map<GameRules.Key<?>, GameRules.Key<?>> PARENT_GAMERULES = new HashMap<>();

    @Override
    public void onInitialize() {
        MobGriefingRules.setup();

        GGCommand.setup();
    }

    public static <T extends GameRules.Value<T>> void add(GameRules.Key<T> child, GameRules.Key<T> parent) {
        PARENT_GAMERULES.put(child, parent);
    }

    public static <T extends GameRules.Value<T>> Optional<GameRules.Key<T>> getParentRule(GameRules.Key<T> child) {
        //noinspection unchecked
        return Optional.ofNullable((GameRules.Key<T>) PARENT_GAMERULES.get(child));
    }
}