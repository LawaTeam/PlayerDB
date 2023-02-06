package com.mclawa.playerdb.lang;

import com.mclawa.playerdb.DatabaseAccessor;
import com.mclawa.playerdb.PlayerDB;
import com.mclawa.playerdb.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@PieComponent
public class PlayerLang {
    @Wire
    private static PlayerDB playerDB;
    @Wire
    private static DatabaseAccessor databaseAccessor;
    public static String getMessage(Player player,String key) {
        return getMessage(getPlayerLang(player),key);
    }

    public static void addMessage(String key,String defaultValue) throws IOException {
        if(PlayerLangManager.langMap.get("default").contains(key))return;
        PlayerLangManager.langMap.get("default").set(key,defaultValue);
        PlayerLangManager.langMap.get("default").save();
    }

    public static void setPlayerLang(Player player,String langKey) {
        PlayerLangManager.langCache.remove(player.getUniqueId());
        PlayerLangManager.langCache.put(player.getUniqueId(),langKey);
        UUID uuid = player.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PlayerData playerData = databaseAccessor.getPlayerDataDao().queryForId(uuid);
                    playerData.setPlayerData("lang",langKey);
                    databaseAccessor.getPlayerDataDao().update(playerData);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(playerDB);
    }

    public static String getPlayerLang(Player player) {
        return PlayerLangManager.langCache.getOrDefault(player.getUniqueId(), "default");
    }

    public static String getMessage(String lang,String key) {
        boolean fallback = false;
        if(!PlayerLangManager.langMap.containsKey(lang))fallback = true;
        if(!PlayerLangManager.langMap.get(lang).contains(key))fallback = true;
        if(fallback) {
            return (PlayerLangManager.langMap.get("default").contains(key)) ?
                    "[NOL]"+PlayerLangManager.langMap.get("default").get(key) : "[NOL][Error : Message Not Found]";
        } else return (String) PlayerLangManager.langMap.get(lang).get(key);
    }
}
