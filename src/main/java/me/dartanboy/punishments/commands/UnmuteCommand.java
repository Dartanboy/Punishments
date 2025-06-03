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

import java.util.Arrays;
import java.util.UUID;

public class UnmuteCommand extends BaseCommand implements CommandExecutor {

    public UnmuteCommand(Punishments plugin) {
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
            if ((punishment.getPunishmentType() == PunishmentType.MUTE || punishment.getPunishmentType() == PunishmentType.TEMP_MUTE) &&
                punishment.isActive()) {
                plugin.getPunishmentDB().removePunishment(punishment.getPunishmentId());
            }
        }

        sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                "Messages.Unmuted", "You have unmuted <player>!").replace("<player>", args[0])));
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, command, label, args, "punishments.unmute", 1, "/unmute <player>");
    }
}
