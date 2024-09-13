package red.jackf.granulargamerules.client.impl;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import red.jackf.granulargamerules.impl.config.GGConfig;

@SuppressWarnings("unused")
public class GGModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> GGConfig.INSTANCE.generateGui().generateScreen(screen);
    }
}
