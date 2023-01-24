package com.mclawa.playerdb;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mclawa.playerdb.event.DatabaseReloadEvent;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

@PieComponent
public class DatabaseAccessor {
    ConnectionSource connectionSource;
    DataSource dataSource;

    @Wire
    PlayerDB plugin;

    @Getter
    Dao<PlayerData, UUID> playerDataDao;

    @Wire
    @Getter
    private static DatabaseAccessor instance;

    public void reconnect() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        if(DatabaseConfiguration.useMysql) {
            hikariConfig.setJdbcUrl(DatabaseConfiguration.mysqlUrl);
            hikariConfig.setUsername(DatabaseConfiguration.mysqlUsername);
            hikariConfig.setPassword(DatabaseConfiguration.mysqlPassword);
        } else {
            File file = new File(plugin.getDataFolder(),"database");
            hikariConfig.setDriverClassName("org.h2.Driver");
            hikariConfig.setJdbcUrl("jdbc:h2:file:"+ file.getAbsolutePath());
        }
        dataSource = new HikariDataSource(hikariConfig);
        connectionSource = new DataSourceConnectionSource(dataSource, DatabaseConfiguration.mysqlUrl);
        playerDataDao = createDao(PlayerData.class,UUID.randomUUID());
        Bukkit.getPluginManager().callEvent(new DatabaseReloadEvent(this));
    }

    public void close() throws Exception {
        connectionSource.close();
    }

    public <K,ID> Dao<K,ID> createDao(Class<K> kClass,ID testValue) throws SQLException {
        Dao<K,ID> dao;
        try {
            //建立表关系
            dao = DaoManager.createDao(connectionSource, kClass);
            dao.idExists(testValue);//测试
        } catch (Exception e) {
            //未找到就新建表
            TableUtils.createTableIfNotExists(connectionSource, kClass);
            dao = DaoManager.createDao(connectionSource, kClass);
        }
        return dao;
    }
}
