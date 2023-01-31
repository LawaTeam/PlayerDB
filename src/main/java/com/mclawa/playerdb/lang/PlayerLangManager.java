package com.mclawa.playerdb.lang;

import top.jingwenmc.spigotpie.common.configuration.ConfigurationAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerLangManager {
    protected static Map<String, ConfigurationAdapter> langMap = new ConcurrentHashMap<>();
    protected static Map<UUID,String> langCache = new ConcurrentHashMap<>();

    public static void init(File dataFolder,Class<? extends ConfigurationAdapter> configAdapterClass) throws Exception {
        File mainFolder = new File(dataFolder,"module_lang");
        mainFolder.mkdirs();
        langMap.put(null,configAdapterClass.getConstructor().newInstance());
        try {
            new File(mainFolder,"lang_default.yml").createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        langMap.get(null).init(new File(mainFolder,"lang_default.yml"));
        for(String s : LangConfiguration.enabledLanguages) {
            langMap.put(null,configAdapterClass.getConstructor().newInstance());
            try {
                new File(mainFolder,"lang_"+s+".yml").createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            langMap.get(null).init(new File(mainFolder,"lang_"+s+".yml"));
        }
    }
}
