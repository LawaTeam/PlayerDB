package com.mclawa.playerdb.bungee;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mclawa.playerdb.DataCallback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

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
        Type strMapType = new TypeToken<Map<String,String>>() {}.getType();
        return new Gson().fromJson(playerData, strMapType);
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
        playerData = new Gson().toJson(map);
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
