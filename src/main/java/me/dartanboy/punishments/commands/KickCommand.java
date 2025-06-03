package me.dartanboy.punishments.commands;

import me.dartanboy.punishments.Punishments;
import me.dartanboy.punishments.commands.base.BaseCommand;
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

public class KickCommand extends BaseCommand implements CommandExecutor {

    public KickCommand(Punishments plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, command, label, args, "punishments.kick", 2, "/kick <player> <reason>");
    }
}
