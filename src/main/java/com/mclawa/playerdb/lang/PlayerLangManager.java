package com.mclawa.playerdb.lang;

import net.md_5.bungee.api.ChatColor;
import top.jingwenmc.spigotpie.common.SpigotPie;
import top.jingwenmc.spigotpie.common.command.CommandSender;
import top.jingwenmc.spigotpie.common.command.PieCommand;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationAdapter;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Wire;
import top.jingwenmc.spigotpie.spigot.configuration.SpigotConfigurationAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PieComponent
public class PlayerLangManager {
    public static Map<String, ConfigurationAdapter> langMap = new ConcurrentHashMap<>();
    public static Map<UUID, String> langCache = new ConcurrentHashMap<>();
    @Wire
    LangConfiguration langConfiguration;

    public static void init(File dataFolder, Class<? extends ConfigurationAdapter> configAdapterClass) throws Exception {
        File mainFolder = new File(dataFolder,"module_lang");
        mainFolder.mkdirs();
        langMap.put("default",  configAdapterClass.getConstructor().newInstance());
        try {
            new File(mainFolder,"lang_default.yml").createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        langMap.get("default").init(new File(mainFolder,"lang_default.yml"));
        for(String s : LangConfiguration.enabledLanguages) {
            langMap.put("default", configAdapterClass.getConstructor().newInstance());
            try {
                new File(mainFolder,"lang_" + s + ".yml").createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            langMap.get("default").init(new File(mainFolder,"lang_" + s + ".yml"));
        }
    }

    @PieCommand(value = "pdb lang reload",bungeeCord = false,spigot = true)
    public void reload(CommandSender sender) throws Exception {
        if(!sender.hasPermission("pdb.admin")) {
            sender.sendMessage(ChatColor.RED+"[PERM] Access denied.");
            return;
        }
        langConfiguration.reloadConfig();
        PlayerLangManager.langMap = new ConcurrentHashMap<>();
        PlayerLangManager.init(SpigotPie.getEnvironment().getWorkFolder(), SpigotConfigurationAdapter.class);
        sender.sendMessage(ChatColor.GREEN+"[PDB] Reload complete.");
    }

}
