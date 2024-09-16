package red.jackf.granulargamerules.mixins.miscrules.sleepcountsbelowsurface;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.granulargamerules.impl.mixinutil.GGServerPlayerSurfaceTracker;
import red.jackf.granulargamerules.impl.rules.MiscRules;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements GGServerPlayerSurfaceTracker {
    @Unique
    private boolean lastWasUnderSurface;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void checkUnderSurfaceOnInit(MinecraftServer server, ServerLevel level, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo ci) {
        this.lastWasUnderSurface = MiscRules.isBelowSurface((ServerPlayer) (Object) this);
    }

    @Override
    public boolean gg$updateIsUnderSurface() {
        boolean old = lastWasUnderSurface;
        this.lastWasUnderSurface = MiscRules.isBelowSurface((ServerPlayer) (Object) this);
        return old != this.lastWasUnderSurface;
    }
}
