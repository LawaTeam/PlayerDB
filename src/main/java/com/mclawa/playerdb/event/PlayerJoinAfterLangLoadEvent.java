package com.mclawa.playerdb.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class PlayerJoinAfterLangLoadEvent extends PDBEvent{
    private Player player;
}
