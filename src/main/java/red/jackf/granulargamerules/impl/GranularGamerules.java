package red.jackf.granulargamerules.impl;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.jackf.granulargamerules.impl.rules.MobGriefing;

public class GranularGamerules implements ModInitializer {
	public static final String ID = "granulargamerules";
	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}
	public static final Component MOD_TITLE = Component.literal("Granular Gamerules");

    public static final Logger LOGGER = LoggerFactory.getLogger("granulargamerules");

	private static final Multimap<GameRules.Key<?>, GameRules.Key<?>> CHILD_GAMERULES = MultimapBuilder.hashKeys().arrayListValues().build();

	public static Multimap<GameRules.Key<?>, GameRules.Key<?>> getChildGamerules() {
		return CHILD_GAMERULES;
	}

	public static <T extends GameRules.Value<T>> GameRules.Key<T> register(GameRules.Key<?> parent, String id, GameRules.Type<T> type) {
        String fullId = parent.getId() + "/" + id;
        GameRules.Key<T> registered = GameRuleRegistry.register(fullId, parent.getCategory(), type);
        CHILD_GAMERULES.put(parent, registered);
        return registered;
    }

	@Override
	public void onInitialize() {
		MobGriefing.doRegister();
	}
}