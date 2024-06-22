package com.mclawa.playerdb.bungee;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mclawa.playerdb.DatabaseConfiguration;
import com.mclawa.playerdb.PlayerDB;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;
import top.jingwenmc.spigotpie.common.instance.Wire;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

@PieComponent(platform = Platform.BUNGEE_CORD)
public class DatabaseAccessorBC {
    ConnectionSource connectionSource;
    DataSource dataSource;

    @Wire
    PlayerDB plugin;

    @Getter
    Dao<PlayerDataBC, UUID> playerDataDao;

    @Wire
    @Getter
    private static DatabaseAccessorBC instance;

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
        playerDataDao = createDao(PlayerDataBC.class,UUID.randomUUID());
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
