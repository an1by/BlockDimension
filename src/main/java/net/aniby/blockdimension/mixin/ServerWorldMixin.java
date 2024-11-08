package net.aniby.blockdimension.mixin;

import net.aniby.blockdimension.BlockDimension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {
    @Inject(at = @At("HEAD"), method = "addDuringPortalTeleport", cancellable = true)
    public void onPlayerChangeDimension(ServerPlayer player, CallbackInfo ci) {
        BlockDimension.preventUnacceptableMove(player.serverLevel(), ci);
    }

    @Inject(at = @At("HEAD"), method = "addDuringCommandTeleport", cancellable = true)
    public void onPlayerTeleport(ServerPlayer player, CallbackInfo ci) {
        BlockDimension.preventUnacceptableMove(player.serverLevel(), ci);
    }
}
