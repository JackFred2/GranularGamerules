package red.jackf.granulargamerules.impl.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import red.jackf.granulargamerules.impl.GranularGamerules;

public class GGConfig {
    public static ConfigClassHandler<GGConfig> INSTANCE = ConfigClassHandler.createBuilder(GGConfig.class)
            .id(GranularGamerules.id("main"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setJson5(true)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("granulargamerules.json5"))
                    .build())
            .build();

    @AutoGen(category = "main")
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry(comment = "(Dedicated Servers Only) Whether to enable fake gamerules for various server.properties fields.")
    public boolean enableFakeServerRules = true;
}
