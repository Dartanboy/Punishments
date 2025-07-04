package me.dartanboy.punishments.commands;

import me.dartanboy.punishments.Punishments;
import me.dartanboy.punishments.commands.base.BaseCommand;
import me.dartanboy.punishments.punishments.Punishment;
import me.dartanboy.punishments.punishments.PunishmentType;
import me.dartanboy.punishments.utils.OfflineUtils;
import me.dartanboy.punishments.utils.StringUtils;
import me.dartanboy.punishments.utils.TimeUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class TempBanCommand extends BaseCommand implements CommandExecutor {

    public TempBanCommand(Punishments plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer target = OfflineUtils.getOfflinePlayerIfCached(args[0]);

        if (target == null) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                    "Messages.Never-Joined", "<player> has never joined").replace("<player>", args[0])));
            return true;
        }

        long durationMillis = TimeUtils.parseDuration(args[1]);
        if (durationMillis <= 0) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                    "Messages.Invalid-Duration", "Invalid duration! Use formats like 1d, 2h30m, etc.")));
            return true;
        }

        UUID playerUUID = target.getUniqueId();
        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        reason = StringUtils.colorize(reason);
        long expiry = System.currentTimeMillis() + durationMillis;

        Punishment punishment = new Punishment(PunishmentType.TEMP_BAN, playerUUID, reason, expiry);
        plugin.getPunishmentDB().addPunishment(playerUUID, punishment);

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().kickPlayer(StringUtils.colorize(plugin.getConfig().getString(
                            "Messages.Temp-Ban-Display", "You are temporarily banned until <time> for <reason>")
                    .replace("<time>", new Date(expiry) + "")
                    .replace("<reason>", reason)));
        }

        sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                        "Messages.Temp-Banned", "You have temp-banned <player> for <time>")
                .replace("<player>", args[0])
                .replace("<time>", args[1])));
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, command, label, args, "punishments.tempban", 3, "/tempban <player> <time> <reason>");
    }
}
