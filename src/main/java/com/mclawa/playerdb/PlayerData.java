package com.mclawa.playerdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Level;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "pdb_player_data")
public class PlayerData {
    @DatabaseField(columnName = "player_uuid",id = true)
    private UUID playerUUID;

    @DatabaseField(index = true,unique = true)
    private String name;

    @DatabaseField(columnName = "player_data",dataType = DataType.LONG_STRING)
    private String playerData;

    public Map<String,String> getPlayerData() {
        Map<String,String> stringMap = new ConcurrentHashMap<>();
        for(String r : playerData.split(";;")) {
            String[] data = r.split(";",2);
            if(data.length<2)continue;
            stringMap.put(data[0],data[1]);
        }
        return stringMap;
    }

    public boolean containsDataKey(String key) {
        return getPlayerData().containsKey(key);
    }

    public String getPlayerData(String key) {
        return getPlayerData().get(key);
    }

    public void setPlayerData(String key,String value) {
        Map<String,String> map = getPlayerData();
        map.remove(key);
        map.put(key, value);
        setPlayerData(map);
    }

    public void setPlayerData(Map<String,String> map) {
        StringJoiner sj = new StringJoiner(";;");
        for(String k : map.keySet()) {
            sj.add(k+";"+map.get(k));
        }
        playerData = sj.toString();
    }

    public static @Nullable PlayerData fromUUID(UUID uuid) throws SQLException {
        return PlayerDB.getPlayerDataDao().queryForId(uuid);
    }

    public static BukkitTask fromUUIDAsync(UUID uuid, DataCallback<PlayerData> callback) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    callback.accept(fromUUID(uuid));
                } catch (SQLException e) {
                    callback.fail(e);
                }
            }
        }.runTaskAsynchronously(PlayerDB.getInstance());
    }

    public static @Nullable PlayerData fromName(String name) throws SQLException {
        return PlayerDB.getPlayerDataDao().queryBuilder().where().eq("name",name).queryForFirst();
    }

    public static BukkitTask fromNameAsync(String name, DataCallback<PlayerData> callback) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    callback.accept(fromName(name));
                } catch (SQLException e) {
                    callback.fail(e);
                }
            }
        }.runTaskAsynchronously(PlayerDB.getInstance());
    }

}
