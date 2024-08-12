package red.jackf.granulargamerules.impl;

import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GranularGamerules implements ModInitializer {
    public static final String ID = "granulargamerules";
    public static final Component MOD_TITLE = Component.literal("Granular Gamerules");
    public static final Logger LOGGER = LoggerFactory.getLogger("granulargamerules");

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    @Override
    public void onInitialize() {

    }
}