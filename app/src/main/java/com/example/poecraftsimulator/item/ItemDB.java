package com.example.poecraftsimulator.item;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.poecraftsimulator.item.dao.ModifierModelDAO;
import com.example.poecraftsimulator.item.model.ModifierModel;

@Database(
        entities = {ModifierModel.class},
        version = 2,
        exportSchema = false
)
public abstract class ItemDB extends RoomDatabase {
    public abstract ModifierModelDAO modifierModel();
}
