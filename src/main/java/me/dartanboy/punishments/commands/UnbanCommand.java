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

import java.util.UUID;

public class UnbanCommand extends BaseCommand implements CommandExecutor {

    public UnbanCommand(Punishments plugin) {
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

        for (Punishment punishment : plugin.getPunishmentDB().getPunishments(playerUUID)) {
            if ((punishment.getPunishmentType() == PunishmentType.BAN || (punishment.getPunishmentType() == PunishmentType.TEMP_BAN)) &&
                punishment.isActive()) {
                plugin.getPunishmentDB().removePunishment(punishment.getPunishmentId());
            }

            if (punishment.getPunishmentType() == PunishmentType.IP_BAN && punishment.isActive()) {
                plugin.getPunishmentDB().removePunishment(punishment.getPunishmentId());
                for (String ip : plugin.getPunishmentDB().getIps(playerUUID)) {
                    plugin.getPunishmentDB().unbanIp(ip);
                }
            }
        }

        sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                "Messages.Unbanned", "You have unbanned <player>!").replace("<player>", args[0])));
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, command, label, args, "punishments.unban", 1, "/unban <player>");
    }
}
