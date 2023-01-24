package com.mclawa.playerdb.event;

import com.mclawa.playerdb.DatabaseAccessor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DatabaseReloadEvent extends PDBEvent {
    public DatabaseAccessor databaseAccessor;
}
