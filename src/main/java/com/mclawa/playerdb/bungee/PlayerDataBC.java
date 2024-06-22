package com.mclawa.playerdb.bungee;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mclawa.playerdb.DataCallback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "pdb_player_data")
public class PlayerDataBC {
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

    public static @Nullable PlayerDataBC fromUUID(UUID uuid) throws SQLException {
        return PlayerDBBC.getPlayerDataDao().queryForId(uuid);
    }

    public static ScheduledTask fromUUIDAsync(UUID uuid, DataCallback<PlayerDataBC> callback) {
        return PlayerDBBC.getInstance().getProxy().getScheduler().runAsync(PlayerDBBC.getInstance(), () -> {
            try {
                callback.accept(fromUUID(uuid));
            } catch (SQLException e) {
                callback.fail(e);
            }
        });
    }

    public static @Nullable PlayerDataBC fromName(String name) throws SQLException {
        return PlayerDBBC.getPlayerDataDao().queryBuilder().where().eq("name",name).queryForFirst();
    }

    public static ScheduledTask fromNameAsync(String name, DataCallback<PlayerDataBC> callback) {
        return PlayerDBBC.getInstance().getProxy().getScheduler().runAsync(PlayerDBBC.getInstance(), () -> {
            try {
                callback.accept(fromName(name));
            } catch (SQLException e) {
                callback.fail(e);
            }
        });
    }

}
