package com.mclawa.playerdb.bungee;

import com.mclawa.playerdb.DatabaseAccessor;
import com.mclawa.playerdb.PlayerData;
import com.mclawa.playerdb.lang.PlayerLangManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import top.jingwenmc.spigotpie.common.event.BungeeEventListener;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.sql.SQLException;
import java.util.UUID;

@PieComponent(platform = Platform.BUNGEE_CORD)
@BungeeEventListener
public class CacheUpdateListener implements Listener {
    @Wire
    PlayerDBBC playerDB;
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PostLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        ProxiedPlayer player = event.getPlayer();
        playerDB.getProxy().getScheduler().runAsync(playerDB, new Runnable() {
            boolean executed = false;
            @Override
            public void run() {
                try {
                    if(!DatabaseAccessor.getInstance().getPlayerDataDao().idExists(uuid)) {
                        DatabaseAccessor.getInstance().getPlayerDataDao().create(new PlayerData(uuid, player.getName(), ""));
                    }
                    PlayerData playerData = DatabaseAccessor.getInstance().getPlayerDataDao().queryForId(uuid);
                    if(!playerData.containsDataKey("lang")) playerData.setPlayerData("lang","default");
                    playerData.setName(player.getName());
                    String selected = playerData.getPlayerData("lang");
                    PlayerLangManager.langCache.remove(uuid);
                    PlayerLangManager.langCache.put(uuid,selected);
                    DatabaseAccessor.getInstance().getPlayerDataDao().update(playerData);
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (executed) {
                        System.err.println("Reconnect failed!");
                        throw new RuntimeException(e);
                    }
                    System.err.println("Trying to reconnect and re-execute...");
                    executed = true;
                    run();
                }
            }
        });
    }
}
