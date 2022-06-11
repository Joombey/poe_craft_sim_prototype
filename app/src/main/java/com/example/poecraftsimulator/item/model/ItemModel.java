package com.example.poecraftsimulator.item.model;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.poecraftsimulator.R;
import com.example.poecraftsimulator.item.model.ModifierModel;
import java.util.ArrayList;
import java.util.List;

public class ItemModel {

    private final int[] RARITIES = {0, 2, 6};
    private int IMAGE_ID = R.drawable.sword;

    public void setSignature(String signature) {
        this.signature = signature;
    }

    private String name = "Sword";
    private String signature = "attack";
    private float signatureValue = 100;
    private int rarity = 0;

    private List<ModifierModel> pref = new ArrayList<ModifierModel>();
    private List<ModifierModel> suf = new ArrayList<ModifierModel>();

    public String getCurrentSignatureValue(){
        List<ModifierModel> modelList = new ArrayList<ModifierModel>(pref);
        modelList.addAll(suf);
        for(ModifierModel modifierModel: modelList){
            if(signature.equals(modifierModel.tag)){
                if(modifierModel.postfix == '%') return signature + ": " + ((int)(signatureValue * (1 + ((float)modifierModel.value)/100)));
                else return signature + ": " + ((int)(signatureValue + modifierModel.value));
            }
        }
        return signature + ": " + ((int)signatureValue);
    }


    public int getRarity() {
        switch (rarity){
            case 0:
                return 0;
            case 2:
                return 1;
            case 6:
                return 2;
            default: return -1;
        }
    }

    public int getIMAGE_ID() {
        return IMAGE_ID;
    }

    public void setIMAGE_ID(int IMAGE_ID) {
        this.IMAGE_ID = IMAGE_ID;
    }

    public void setName(String name) {
        this.name = name;
    }



    public List<ModifierModel> getSuf() {
        return suf;
    }

    public List<ModifierModel> getPref(){
        return pref;
    }

    public void setRarity(int rarity) {
        if (rarity > 2) return;
        this.rarity = RARITIES[rarity];
        if (this.rarity == 0) {
            pref = new ArrayList<ModifierModel>();
            suf = new ArrayList<ModifierModel>();
        }
    }


    public void setAffixesList(@NonNull List<ModifierModel> list){
        if(list.isEmpty()) return;
        if(list.get(0).type == 'p' && pref.size() < rarity/2){
            pref.addAll(list);
        } else if(list.get(0).type == 's' && suf.size() < rarity/2){
            suf.addAll(list);
        }
    }
    public void setNewAffixes(@NonNull List<ModifierModel> list1, @NonNull List<ModifierModel> list2){
        pref = list1;
        suf = list2;
    }

    public String getName() {
        return name;
    }

    public int getSize(){
        Log.d("PREFIX", "GET SIZE: " + (pref.size() + suf.size()));
        return pref.size() + suf.size();
    }


    public int[] getOpenAffixes(){
        if(rarity == 0){
            int[] affixes = {0, 0};
            return affixes;
        }
        int[] affixes = {rarity/2 - pref.size(), rarity/2 - suf.size()};
        return affixes;
    }
}
