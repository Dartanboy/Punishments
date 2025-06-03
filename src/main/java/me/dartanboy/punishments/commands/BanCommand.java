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

public class BanCommand extends BaseCommand implements CommandExecutor {

    public BanCommand(Punishments plugin) {
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

        Punishment punishment = new Punishment(PunishmentType.BAN, playerUUID, reason, expiry);
        plugin.getPunishmentDB().addPunishment(playerUUID, punishment);

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().kickPlayer(StringUtils.colorize(plugin.getConfig().getString(
                            "Messages.Ban-Display", "You have been permanently banned for <reason>")
                    .replace("<reason>", reason)));
        }

        sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                "Messages.Banned", "You have banned <player>").replace("<player>", args[0])));
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, command, label, args, "punishments.ban", 2, "/ban <player> <reason>");
    }
}
