package net.aniby.blockdimension;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DimensionCommand {
    public DimensionCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("dimension")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .then(Commands.argument("lock", BoolArgumentType.bool())
                                        .executes(this::execute)
                                )
                        )
        );
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Level level = DimensionArgument.getDimension(context, "dimension");
        boolean lock = BoolArgumentType.getBool(context, "lock");

        String name = level.dimension().location().toString();
        List<String> lockedDimensions = new ArrayList<>(Config.LOCKED_DIMENSIONS.get());
        if (lock) {
            lockedDimensions.add(name);
            source.sendSuccess(
                    () -> Component.literal("Dimension \"" + name + "\" locked"),
                    true
            );
        } else {
            lockedDimensions.remove(name);
            source.sendSuccess(
                    () -> Component.literal("Dimension \"" + name + "\" unlocked"),
                    true
            );
        }
        Config.LOCKED_DIMENSIONS.set(lockedDimensions);
        Config.SPEC.save();

        return 0;
    }
}
