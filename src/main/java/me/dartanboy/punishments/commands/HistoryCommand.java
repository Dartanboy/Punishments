package me.dartanboy.punishments.commands;

import me.dartanboy.punishments.Punishments;
import me.dartanboy.punishments.commands.base.BaseCommand;
import me.dartanboy.punishments.punishments.Punishment;
import me.dartanboy.punishments.punishments.PunishmentType;
import me.dartanboy.punishments.utils.OfflineUtils;
import me.dartanboy.punishments.utils.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HistoryCommand extends BaseCommand implements CommandExecutor {

    public HistoryCommand(Punishments plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer target = OfflineUtils.getOfflinePlayerIfCached(args[0]);

        if (target == null) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                    "Messages.Never-Joined", "<player> has never joined!").replace("<player>", args[0])));
            return true;
        }

        UUID playerUUID = target.getUniqueId();
        List<Punishment> punishments = plugin.getPunishmentDB().getPunishments(playerUUID);

        if (punishments.isEmpty()) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString("Messages.No-History", "No history found for <player>")
                    .replace("<player>", args[0])));
            return true;
        }

        for (Punishment punishment : punishments) {
            String type = punishment.getPunishmentType().toString();
            String reason = punishment.getReason();
            boolean active = punishment.isActive();

            String activeStatus;

            if (!active || (punishment.getExpiryTime() != -1 && punishment.getExpiryTime() <= System.currentTimeMillis())) {
                activeStatus = plugin.getConfig().getString("Messages.Not-Active-History", "Inactive");
            } else if (punishment.getExpiryTime() == -1) {
                activeStatus = plugin.getConfig().getString("Messages.Perm-Active-History", "Permanent");
            } else {
                activeStatus = plugin.getConfig().getString("Messages.Temp-Active-History", "Until <time>")
                        .replace("<time>", new Date(punishment.getExpiryTime()) + "");
            }

            String line = StringUtils.colorize(plugin.getConfig().getString("Messages.Line-History", "<punishment>: <reason> (<active>)")
                    .replace("<punishment>", type)
                    .replace("<reason>", reason)
                    .replace("<active>", activeStatus));
            sender.sendMessage(line);
        }

        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, command, label, args, "punishments.history", 1, "/history <player>");
    }
}
