package me.dartanboy.punishments.commands;

import me.dartanboy.punishments.Punishments;
import me.dartanboy.punishments.commands.base.BaseCommand;
import me.dartanboy.punishments.punishments.Punishment;
import me.dartanboy.punishments.punishments.PunishmentType;
import me.dartanboy.punishments.utils.OfflineUtils;
import me.dartanboy.punishments.utils.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

public class BanIpCommand extends BaseCommand implements CommandExecutor {

    public BanIpCommand(Punishments plugin) {
        super(plugin);
    }

    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer target = OfflineUtils.getOfflinePlayerIfCached(args[0]);

        if (target == null) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                    "Messages.Never-Joined", "<player> has never joined").replace("<player>", args[0])));
            return true;
        }

        UUID playerUUID = target.getUniqueId();
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        reason = StringUtils.colorize(reason);
        long expiry = -1;

        Punishment punishment = new Punishment(PunishmentType.IP_BAN, playerUUID, reason, expiry);
        plugin.getPunishmentDB().addPunishment(playerUUID, punishment);

        for (String ip : plugin.getPunishmentDB().getIps(playerUUID)) {
            plugin.getPunishmentDB().banIp(ip, reason);
        }

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().kickPlayer(StringUtils.colorize(plugin.getConfig().getString(
                            "Messages.Ban-Display", "You have been permanently banned for <reason>")
                    .replace("<reason>", reason)));
        }

        sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                "Messages.IP-Banned", "You have ip-banned <player>").replace("<player>", args[0])));
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, command, label, args, "punishments.banip", 2, "/banip <player> <reason>");
    }
}
