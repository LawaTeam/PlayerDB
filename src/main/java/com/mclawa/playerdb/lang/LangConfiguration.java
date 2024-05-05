package com.mclawa.playerdb.lang;

import top.jingwenmc.spigotpie.common.configuration.BaseConfiguration;
import top.jingwenmc.spigotpie.common.configuration.Configuration;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationFile;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.ArrayList;
import java.util.List;

@PieComponent
@ConfigurationFile("module_lang/config.yml")
public class LangConfiguration extends BaseConfiguration {

    @Configuration("enabled_lang")
    public static List<String> enabledLanguages = new ArrayList<>();

}
