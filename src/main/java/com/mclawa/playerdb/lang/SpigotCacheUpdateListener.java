package com.mclawa.playerdb.lang;

import com.mclawa.playerdb.DatabaseAccessor;
import com.mclawa.playerdb.PlayerDB;
import com.mclawa.playerdb.PlayerData;
import com.mclawa.playerdb.event.PlayerJoinAfterLangLoadEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import top.jingwenmc.spigotpie.common.SpigotPie;
import top.jingwenmc.spigotpie.common.command.CommandSender;
import top.jingwenmc.spigotpie.common.command.PieCommand;
import top.jingwenmc.spigotpie.common.event.SpigotEventListener;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PieComponent(platform = Platform.SPIGOT)
@SpigotEventListener
public class SpigotCacheUpdateListener implements Listener {
    @Wire
    PlayerDB playerDB;
    @Wire
    LangConfiguration langConfiguration;
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Player player = event.getPlayer();
        new BukkitRunnable() {
            boolean executed = false;
            @Override
            public void run() {
                try {
                    if(!DatabaseAccessor.getInstance().getPlayerDataDao().idExists(uuid)) {
                        DatabaseAccessor.getInstance().getPlayerDataDao().create(new PlayerData(uuid,""));
                    }
                    PlayerData playerData = DatabaseAccessor.getInstance().getPlayerDataDao().queryForId(uuid);
                    if(!playerData.containsDataKey("lang")) playerData.setPlayerData("lang","default");
                    String selected = playerData.getPlayerData("lang");
                    if(selected.equalsIgnoreCase("default"))selected=null;
                    PlayerLangManager.langCache.remove(uuid);
                    PlayerLangManager.langCache.put(uuid,selected);
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            Bukkit.getPluginManager().callEvent(new PlayerJoinAfterLangLoadEvent(player));
                        }
                    }.runTask(playerDB);
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
        }.runTaskAsynchronously(playerDB);
    }

    @PieCommand(value = "pdb lang reload",bungeeCord = false,spigot = true)
    public void reload(CommandSender sender) throws IOException, IllegalAccessException {
        if(!sender.hasPermission("pdb.admin")) {
            sender.sendMessage(ChatColor.RED+"[PERM] Access denied.");
            return;
        }
        langConfiguration.reloadConfig();
        PlayerLangManager.langMap = new ConcurrentHashMap<>();
        PlayerLangManager.init(SpigotPie.getEnvironment().getWorkFolder());
        sender.sendMessage(ChatColor.GREEN+"[PDB] Reload complete.");
    }
}
