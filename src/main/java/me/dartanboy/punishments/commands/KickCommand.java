package me.dartanboy.punishments.commands;

import me.dartanboy.punishments.Punishments;
import me.dartanboy.punishments.punishments.Punishment;
import me.dartanboy.punishments.punishments.PunishmentType;
import me.dartanboy.punishments.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class KickCommand implements CommandExecutor {

    private final Punishments plugin;

    public KickCommand(Punishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("punishments.kick")) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                    "Messages.No-Permission", "Insufficient permissions.")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                            "Messages.Incorrect-Args", "Incorrect args! Try <suggestion>")
                    .replace("<suggestion>", "/kick <player> <reason>")));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                    "Messages.Not-Online", "<player> is not online! You cannot kick them!")
                    .replace("<player>", args[0])));
            return true;
        }

        UUID playerUUID = target.getUniqueId();
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        reason = StringUtils.colorize(reason);

        Punishment punishment = new Punishment(PunishmentType.KICK, playerUUID, reason, -1);
        plugin.getPunishmentDB().addPunishment(playerUUID, punishment);

        target.kickPlayer(StringUtils.colorize(plugin.getConfig().getString(
                "Messages.Kick-Display", "You have been kicked for <reason>")
                .replace("<reason>", reason)));

        sender.sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                "Messages.Kicked", "You have kicked <player>!").replace("<player>", target.getName())));
        return true;
    }
}
