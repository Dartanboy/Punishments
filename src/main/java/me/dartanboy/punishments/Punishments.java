package me.dartanboy.punishments;

import me.dartanboy.punishments.commands.*;
import me.dartanboy.punishments.db.PunishmentDB;
import me.dartanboy.punishments.db.impl.MemoryPunishmentDB;
import me.dartanboy.punishments.db.impl.SQLitePunishmentDB;
import me.dartanboy.punishments.listeners.PunishmentListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Punishments extends JavaPlugin {
    private PunishmentDB punishmentDB;

    private void setupDatabase() {
        if (getConfig().getString("Settings.Storage").equalsIgnoreCase("sqlite")) {
            punishmentDB = new SQLitePunishmentDB(getDataFolder() + "/punishments.db");
        } else {
            punishmentDB = new MemoryPunishmentDB();
        }
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        setupDatabase();

        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("tempban").setExecutor(new TempBanCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("tempmute").setExecutor(new TempMuteCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("banip").setExecutor(new BanIpCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("history").setExecutor(new HistoryCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));

        getServer().getPluginManager().registerEvents(new PunishmentListener(this), this);
    }

    public PunishmentDB getPunishmentDB() {
        return punishmentDB;
    }
}
