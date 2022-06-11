package com.example.poecraftsimulator.item.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.poecraftsimulator.item.model.ModifierModel;

import java.util.List;

@Dao
public interface ModifierModelDAO {
    @Query("SELECT * FROM ModifierModel")
    List<ModifierModel> getAll();

    @Query(
            "SELECT * FROM MODIFIERMODEL" +
            " WHERE tag NOT IN (:list) AND type == :type" +
            " ORDER BY ABS((weight - :weight) / :div), RANDOM()" +
            " LIMIT :quantity"
    )
    List<ModifierModel> getRandomMods(double weight, char type, int div, int quantity, String[] list);

    @Query("SELECT Sum(weight) from ModifierModel WHERE type == :type")
    int getAllMaxWeight(char type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMods(ModifierModel... modifierModels);
}