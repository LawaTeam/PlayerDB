package com.mclawa.playerdb.bungee;

import com.j256.ormlite.dao.Dao;
import com.mclawa.playerdb.lang.PlayerLangManager;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import top.jingwenmc.spigotpie.bungee.SpigotPieBungee;
import top.jingwenmc.spigotpie.bungee.configuration.BungeeConfigurationAdapter;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.sql.SQLException;
import java.util.UUID;

@PieComponent(platform = Platform.BUNGEE_CORD)
public final class PlayerDBBC extends Plugin {
    @Wire
    @Getter
    private static PlayerDBBC instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("[LOAD] PlayerDB...");
        getLogger().info("[LOAD] SpigotPie...");
        SpigotPieBungee.inject(this,"com.mysql","org","META-INF","com.zaxxer");
        getLogger().info("[FINE] SpigotPie");
        getLogger().info("[LOAD] Database...");
        try {
            DatabaseAccessorBC.getInstance().reconnect();
        } catch (SQLException e) {
            getLogger().warning("[FAIL] Database load failed!");
            getLogger().warning("[FAIL] Check stacktrace to learn more!");
            throw new RuntimeException(e);
        }
        getLogger().info("[FINE] Database");
        getLogger().info("[LOAD] [Module] Language...");
        try {
            PlayerLangManager.init(getDataFolder(), BungeeConfigurationAdapter.class);
        } catch (Exception e) {
            getLogger().warning("[FAIL] Language load failed!");
            getLogger().warning("[FAIL] Check stacktrace to learn more!");
            throw new RuntimeException(e);
        }
        getLogger().info("[FINE] [Module] Language");
        getLogger().info("[FINE] PlayerDB");
    }

    @Override
    public void onDisable() {
        getLogger().info("[CLOSE] Database...");
        try {
            DatabaseAccessorBC.getInstance().close();
        } catch (Exception e) {
            getLogger().warning("[FAIL] Database close failed!");
            getLogger().warning("[FAIL] Check stacktrace to learn more!");
            throw new RuntimeException(e);
        }
        getLogger().info("[FINE] Database");
        getLogger().info("[FINE] Thank you and goodbye.");
        // Plugin shutdown logic
    }

    public static Dao<PlayerDataBC, UUID> getPlayerDataDao() {
        return DatabaseAccessorBC.getInstance().getPlayerDataDao();
    }
}
