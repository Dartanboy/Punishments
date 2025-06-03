package me.dartanboy.punishments.listeners;

import me.dartanboy.punishments.Punishments;
import me.dartanboy.punishments.punishments.Punishment;
import me.dartanboy.punishments.punishments.PunishmentType;
import me.dartanboy.punishments.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PunishmentListener implements Listener {

    private record MuteResult(boolean muted, String reason, Date expiry) {}

    private final Punishments plugin;

    public PunishmentListener(Punishments plugin) {
        this.plugin = plugin;
    }

    private MuteResult isMuted(UUID playerUUID) {
        List<Punishment> punishments = plugin.getPunishmentDB().getPunishments(playerUUID);

        for (Punishment punishment : punishments) {
            if ((punishment.getPunishmentType() == PunishmentType.MUTE ||
                    (punishment.getPunishmentType() == PunishmentType.TEMP_MUTE &&
                            punishment.getExpiryTime() >= System.currentTimeMillis())) && punishment.isActive()) {
                return new MuteResult(true, punishment.getReason(), new Date(punishment.getExpiryTime()));
            }
        }

        return new MuteResult(false, null, null);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        MuteResult result = isMuted(uuid);

        if (result.muted) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(StringUtils.colorize(plugin.getConfig().getString(
                    "Messages.Mute-Display", "You are muted for <reason> until <time>!")
                    .replace("<reason>", result.reason)
                    .replace("<time>", result.expiry + "")));
        }
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String playerIp = event.getAddress().getHostAddress();
        plugin.getPunishmentDB().registerIp(uuid, playerIp);

        List<Punishment> punishments = plugin.getPunishmentDB().getPunishments(uuid);
        for (Punishment punishment : punishments) {
            if (punishment.getPunishmentType() == PunishmentType.BAN && punishment.isActive()) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                event.setKickMessage(StringUtils.colorize(plugin.getConfig().getString(
                                "Messages.Ban-Display", "You have been banned for <reason>")
                        .replace("<reason>", punishment.getReason())));
            }
            if (punishment.getPunishmentType() == PunishmentType.TEMP_BAN &&
                            punishment.getExpiryTime() >= System.currentTimeMillis() &&
                            punishment.isActive()
            ) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                Date expiryDate = new Date(punishment.getExpiryTime());
                event.setKickMessage(StringUtils.colorize(plugin.getConfig().getString(
                                "Messages.Ban-Display", "You have been temp-banned until <time> for <reason>")
                        .replace("<reason>", punishment.getReason())
                        .replace("<time>", expiryDate + "")));
            }
        }
    }
}
