package me.dartanboy.punishments.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflineUtils {

    public static OfflinePlayer getOfflinePlayerIfCached(String name) {
        OfflinePlayer offlinePlayer = null;

        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (name.equalsIgnoreCase(op.getName())) {
                offlinePlayer = op;
                break;
            }
        }

        return offlinePlayer;
    }
}
