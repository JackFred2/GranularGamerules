package red.jackf.granulargamerules.mixins.dedicated;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.dedicated.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Properties;

@Mixin(Settings.class)
public interface SettingsAccessor<T extends Settings<T>> {

    @Invoker
    Properties invokeCloneProperties();

    @Invoker
    T invokeReload(RegistryAccess registryAccess, Properties properties);
}
