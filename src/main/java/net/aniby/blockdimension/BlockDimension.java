package net.aniby.blockdimension;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

@Mod(BlockDimension.MODID)
public class BlockDimension {
    public static final String MODID = "blockdimension";

    public BlockDimension() {
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        new DimensionCommand(event.getDispatcher());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (inClosedWorld(event.getLevel())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (inClosedWorld(player.level())) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            for (ServerLevel level : server.getAllLevels()) {
                if (level.dimension() == Level.OVERWORLD) {
                    player.teleportTo(
                            level,
                            level.getLevelData().getXSpawn(),
                            level.getLevelData().getYSpawn(),
                            level.getLevelData().getZSpawn(),
                            new HashSet<>(),
                            player.yRotO, player.xRotO
                    );
                    break;
                }
            }
        }
    }

    private static boolean inClosedWorld(Level level) {
        return Config.LOCKED_DIMENSIONS.get()
                .contains(level.dimension().location().toString());
    }

    public static <T extends CallbackInfo> boolean preventUnacceptableMove(Level level, T ci) {
        if (inClosedWorld(level)) {
            ci.cancel();
            return true;
        }
        return false;
    }
}
