package com.mclawa.playerdb;

import com.j256.ormlite.dao.Dao;
import com.mclawa.playerdb.lang.PlayerLangManager;
import org.bukkit.plugin.java.JavaPlugin;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;
import top.jingwenmc.spigotpie.spigot.SpigotPieSpigot;

import java.sql.SQLException;
import java.util.UUID;

@PieComponent(platform = Platform.SPIGOT)
public final class PlayerDB extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("[LOAD] PlayerDB...");
        getLogger().info("[LOAD] SpigotPie...");
        SpigotPieSpigot.inject(this,"com.mysql","org","META-INF");
        getLogger().info("[FINE] SpigotPie");
        getLogger().info("[LOAD] Database...");
        try {
            DatabaseAccessor.getInstance().reconnect();
        } catch (SQLException e) {
            getLogger().warning("[FAIL] Database load failed!");
            getLogger().warning("[FAIL] Check stacktrace to learn more!");
            throw new RuntimeException(e);
        }
        getLogger().info("[FINE] Database");
        getLogger().info("[LOAD] [Module] Language...");
        PlayerLangManager.init(getDataFolder());
        getLogger().info("[FINE] [Module] Language");
        getLogger().info("[FINE] PlayerDB");
    }

    @Override
    public void onDisable() {
        getLogger().info("[CLOSE] Database...");
        try {
            DatabaseAccessor.getInstance().close();
        } catch (Exception e) {
            getLogger().warning("[FAIL] Database close failed!");
            getLogger().warning("[FAIL] Check stacktrace to learn more!");
            throw new RuntimeException(e);
        }
        getLogger().info("[FINE] Database");
        getLogger().info("[FINE] Thank you and goodbye.");
        // Plugin shutdown logic
    }

    public static Dao<PlayerData, UUID> getPlayerDataDao() {
        return DatabaseAccessor.getInstance().getPlayerDataDao();
    }
}
