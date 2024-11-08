package net.aniby.blockdimension;

import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    public static <T extends CallbackInfo> boolean preventUnacceptableMove(Level level, T ci) {
        boolean inClosedWorld = Config.LOCKED_DIMENSIONS.get()
                .contains(level.dimension().location().toString());
        if (inClosedWorld) {
            ci.cancel();
            return true;
        }
        return false;
    }
}
