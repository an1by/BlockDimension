package net.aniby.blockdimension.mixin;

import net.aniby.blockdimension.BlockDimension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraftforge.common.util.ITeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow
    @Nullable
    protected abstract PortalInfo findDimensionEntryPoint(ServerLevel pDestination);

//    @Inject(at = @At("HEAD"), method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", cancellable = true)
//    private void teleportTo(ServerLevel pLevel, double pX, double pY, double pZ, Set<RelativeMovement> pRelativeMovements, float pYRot, float pXRot, CallbackInfoReturnable<Boolean> cir) {
//        if (BlockEnd.preventUnacceptableMove(pLevel, cir)) {
//            cir.setReturnValue(false);
//        }
//    }

    @Inject(at = @At("HEAD"), method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", cancellable = true)
    public void teleportTo(ServerLevel newLevel, double pX, double pY, double pZ, float pYaw, float pPitch, CallbackInfo ci) {
        BlockDimension.preventUnacceptableMove(newLevel, ci);
    }

    @Inject(at = @At("HEAD"), method = "changeDimension", cancellable = true, remap = false)
    public void changeDimension(ServerLevel level, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir) {
        PortalInfo teleportTarget = this.findDimensionEntryPoint(level);
        if (teleportTarget != null) {
            if (BlockDimension.preventUnacceptableMove(level, cir)) {
                cir.setReturnValue(null);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "startRiding", cancellable = true)
    public void startRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (entity.getType() == EntityType.PLAYER) {
            BlockDimension.preventUnacceptableMove(entity.level(), cir);
        }
    }
}
