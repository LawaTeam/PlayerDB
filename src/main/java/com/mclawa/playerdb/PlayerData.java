package com.mclawa.playerdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "pdb_player_data")
public class PlayerData {
    @DatabaseField(columnName = "player_uuid",id = true)
    private UUID playerUUID;

    @DatabaseField(columnName = "player_data",dataType = DataType.LONG_STRING)
    private String playerData;

    public Map<String,String> getPlayerData() {
        Map<String,String> stringMap = new ConcurrentHashMap<>();
        for(String r : playerData.split(";;")) {
            String[] data = r.split(";",2);
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
}
