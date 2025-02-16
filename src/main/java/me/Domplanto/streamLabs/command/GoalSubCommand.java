package me.Domplanto.streamLabs.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.Domplanto.streamLabs.StreamLabs;
import me.Domplanto.streamLabs.command.argument.GoalArgumentType;
import me.Domplanto.streamLabs.statistics.goal.DonationGoal;
import me.Domplanto.streamLabs.util.components.ColorScheme;
import me.Domplanto.streamLabs.util.components.Translations;
import org.bukkit.command.CommandSender;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class GoalSubCommand extends SubCommand {
    public GoalSubCommand(StreamLabs pluginInstance) {
        super(pluginInstance);
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> buildCommand() {
        return literal("goal")
                .then(literal("start")
                        .then(argument("goal_type", GoalArgumentType.goal(getPlugin().pluginConfig()))
                                .then(argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> exceptionHandler(ctx, sender -> {
                                            DonationGoal goal = GoalArgumentType.getGoal(ctx, "goal_type");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            getPlugin().getExecutor().activateGoal(goal, amount);
                                            Translations.sendPrefixedResponse("streamlabs.commands.goal.goal_started", ColorScheme.SUCCESS, sender, text(goal.id));
                                        }))))
                )
                .then(literal("stop")
                        .executes(ctx -> exceptionHandler(ctx, sender -> {
                            if (checkGoalNotActive(sender)) return;
                            getPlugin().getExecutor().stopGoal();
                            Translations.sendPrefixedResponse("streamlabs.commands.goal.goal_stopped", ColorScheme.SUCCESS, sender);
                        }))
                )
                .then(literal("remove")
                        .executes(ctx -> exceptionHandler(ctx, sender -> {
                            if (checkGoalNotActive(sender)) return;
                            getPlugin().getExecutor().removeGoal();
                            Translations.sendPrefixedResponse("streamlabs.commands.goal.goal_removed", ColorScheme.SUCCESS, sender);
                        }))
                )
                .build();
    }

    private boolean checkGoalNotActive(CommandSender sender) {
        boolean notActive = getPlugin().getExecutor().getActiveGoal() == null;
        if (notActive)
            Translations.sendPrefixedResponse("streamlabs.commands.goal.error_no_goal_active", ColorScheme.DISABLE, sender);
        return notActive;
    }
}
