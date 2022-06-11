package com.example.poecraftsimulator.item.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ModifierModel {
    @PrimaryKey(autoGenerate = true)
    public int id = 0;
    public char type;
    public String tag;
    public int value;
    public char postfix;
    public double weight;
    public int tier;

    public ModifierModel(char type, String tag, int value, char postfix, double weight, int tier) {
        this.type = type;
        this.tag = tag;
        this.value = value;
        this.postfix = postfix;
        this.weight = weight;
        this.tier = tier;
    }
}
