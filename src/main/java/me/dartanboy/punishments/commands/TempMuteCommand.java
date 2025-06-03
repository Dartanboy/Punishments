package me.dartanboy.punishments.commands;

import me.dartanboy.punishments.Punishments;
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

public class TempMuteCommand implements CommandExecutor {

    private final Punishments plugin;

    public TempMuteCommand(Punishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("punishments.tempmute")) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                    "Messages.No-Permission", "Insufficient permissions.")));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                            "Messages.Incorrect-Args", "Incorrect args! Try <suggestion>")
                    .replace("<suggestion>", "/tempmute <player> <duration> <reason>")));
            return true;
        }

        OfflinePlayer target = OfflineUtils.getOfflinePlayerIfCached(args[0]);
        if (target == null) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                    "Messages.Never-Joined", "<player> has never joined!").replace("<player>", args[0])));
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

        Punishment punishment = new Punishment(PunishmentType.TEMP_MUTE, playerUUID, reason, expiry);
        plugin.getPunishmentDB().addPunishment(playerUUID, punishment);

        sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                        "Messages.Temp-Muted", "You have temp-muted <player> for <time>!")
                .replace("<player>", args[0])
                .replace("<time>", args[1])));
        return true;
    }
}
