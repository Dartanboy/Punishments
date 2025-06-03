package me.dartanboy.punishments.commands.base;

import me.dartanboy.punishments.Punishments;
import me.dartanboy.punishments.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class BaseCommand {

    protected Punishments plugin;

    public BaseCommand(Punishments plugin) {
        this.plugin = plugin;
    }

    protected abstract boolean execute(CommandSender sender, Command command, String label, String[] args);

    public boolean execute(CommandSender sender, Command command, String label,
                           String[] args, String permission, int requiredArgs, String suggestion) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString("Messages.No-Permission", "Insufficient permissions")));
            return true;
        }

        if (args.length < requiredArgs) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString("Messages.Incorrect-Args", "Incorrect args! Try <suggestion>")
                    .replace("<suggestion>", suggestion)));
            return true;
        }

        return execute(sender, command, label, args);
    }
}
