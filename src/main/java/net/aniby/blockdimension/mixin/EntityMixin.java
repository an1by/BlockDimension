package net.aniby.blockdimension.mixin;

import net.aniby.blockdimension.BlockDimension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.portal.PortalInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    @Nullable
    protected abstract PortalInfo findDimensionEntryPoint(ServerLevel pDestination);

    @Shadow
    public abstract boolean hasPassenger(Predicate<Entity> passenger);

    @Shadow
    @Nullable
    private Entity vehicle;

    @Shadow
    public abstract EntityType<?> getType();

    @Unique
    public boolean blockDimension$isTherePlayer() {
        return this.getType() == EntityType.PLAYER
                || this.hasPassenger(p -> p.getType() == EntityType.PLAYER)
                || (this.vehicle != null && this.vehicle.getType() == EntityType.PLAYER);
    }

    @Inject(at = @At("HEAD"), method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", cancellable = true)
    public void moveToWorld(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
        if (blockDimension$isTherePlayer()) {
            PortalInfo teleportTarget = this.findDimensionEntryPoint(destination);
            if (teleportTarget != null) {
                if (BlockDimension.preventUnacceptableMove(destination, cir)) {
                    cir.setReturnValue(null);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", cancellable = true)
    public void teleport(ServerLevel level, double pX, double pY, double pZ, Set<RelativeMovement> relativeMovements, float pYRot, float pXRot, CallbackInfoReturnable<Boolean> cir) {
        if (blockDimension$isTherePlayer()) {
            if (BlockDimension.preventUnacceptableMove(level, cir)) {
                cir.setReturnValue(null);
            }
        }
    }
}
