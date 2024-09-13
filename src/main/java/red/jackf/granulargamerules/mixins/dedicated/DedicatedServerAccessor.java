package red.jackf.granulargamerules.mixins.dedicated;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(DedicatedServer.class)
public interface DedicatedServerAccessor {

    @Accessor
    DedicatedServerSettings getSettings();
}
