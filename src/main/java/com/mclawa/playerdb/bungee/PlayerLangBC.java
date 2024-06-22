package com.mclawa.playerdb.bungee;

import com.mclawa.playerdb.lang.PlayerLang;
import com.mclawa.playerdb.lang.PlayerLangManager;
import org.jetbrains.annotations.Nullable;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@PieComponent(platform = Platform.BUNGEE_CORD)
public class PlayerLangBC {
    @Wire
    private static PlayerDBBC playerDB;
    @Wire
    private static DatabaseAccessorBC databaseAccessor;
    public static String getMessage(UUID uuid,String key) {
        return getMessage(getPlayerLang(uuid),key);
    }

    public static void addMessage(String key, String defaultValue) throws IOException {
        PlayerLang.addMessage(key, defaultValue);
    }

    public static void setPlayerLang(UUID uuid,String langKey) {
        PlayerLangManager.langCache.remove(uuid);
        PlayerLangManager.langCache.put(uuid,langKey);
        playerDB.getProxy().getScheduler().runAsync(playerDB,()->{
            try {
                PlayerDataBC playerData = databaseAccessor.getPlayerDataDao().queryForId(uuid);
                playerData.setPlayerData("lang",langKey);
                databaseAccessor.getPlayerDataDao().update(playerData);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public static String getPlayerLang(@Nullable UUID uuid) {
        return PlayerLang.getPlayerLang(uuid);
    }

    public static String getMessage(String lang,String key) {
        return PlayerLang.getMessage(lang, key);
    }
}
