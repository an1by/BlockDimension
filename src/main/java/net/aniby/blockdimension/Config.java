package net.aniby.blockdimension;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = BlockDimension.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LOCKED_DIMENSIONS = BUILDER
            .comment("Locked dimensions")
            .defineListAllowEmpty(
                    "dimensions",
                    List.of("minecraft:the_nether", "minecraft:the_end"),
                    object -> true
            );

    protected static final ForgeConfigSpec SPEC = BUILDER.build();
}
