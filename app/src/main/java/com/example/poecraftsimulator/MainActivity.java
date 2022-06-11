package com.example.poecraftsimulator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.poecraftsimulator.item.ItemDB;
import com.example.poecraftsimulator.item.dao.ModifierModelDAO;
import com.example.poecraftsimulator.item.model.ItemModel;
import com.example.poecraftsimulator.item.model.ModifierModel;
import com.google.gson.Gson;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private final String SHARED_PREFERENCES_KEY = "ItemModel";
    private boolean toClear;

    private static final char PREFIX = 'p';
    private static final char SUFFIX = 's';

    private ModifierModelDAO modifierModelDAO;
    ItemModel itemModel = new ItemModel();

    private final int[] bIds = {
            R.id.Chaos,
            R.id.Alchemy,
            R.id.Exalt,
            R.id.Regal,
            R.id.Scouring
    };

    private final int[] vIds = {
            R.id.modfield1,
            R.id.modfield2,
            R.id.modfield3, R.id.modfield4,
            R.id.modfield5,
            R.id.modfield6,
            R.id.signatureView
    };

    private final TextView[] textViews = new TextView[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toClear = false;

        ItemDB itemDB = Room
                .databaseBuilder(getApplicationContext(), ItemDB.class, "DB1")
                .allowMainThreadQueries()
                .build();
        modifierModelDAO = itemDB.modifierModel();

        for (int i = 0; i < 7; i++) textViews[i] = findViewById(vIds[i]);
        for (int i = 0; i < 5; i++) {
            findViewById(bIds[i]).setOnClickListener(this);
            findViewById(bIds[i]).setOnTouchListener(this);
        }

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        if (!sharedPreferences.contains(SHARED_PREFERENCES_KEY)) return;
        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHARED_PREFERENCES_KEY, null);
        itemModel = gson.fromJson(json, ItemModel.class);
        ((ImageView) findViewById(R.id.Item)).setImageResource(itemModel.getIMAGE_ID());
        show();
    }

    public void exalt(int prefs) {
        char[] affix_name = {PREFIX, SUFFIX};
        int maxWeight = modifierModelDAO.getAllMaxWeight(affix_name[prefs]) + 1;
        List<ModifierModel> modifierList;
        if (affix_name[prefs] == PREFIX) {
            modifierList = itemModel.getPref();
        } else {
            modifierList = itemModel.getSuf();
        }

        String[] list = new String[modifierList.size()];
        for (int i = 0; i < list.length; i++) {
            list[i] = modifierList.get(i).tag;
        }

        List<ModifierModel> modifierModelList = modifierModelDAO.getRandomMods(
                (Math.random() * maxWeight),
                affix_name[prefs],
                maxWeight,
                1,
                list
        );
        itemModel.setAffixesList(modifierModelList);
    }

    public void roll(int prefs, int sufs) {
        double luckiness = random(modifierModelDAO.getAllMaxWeight(PREFIX), 0);
        String[] list = new String[prefs];
        List<ModifierModel> modelList = new ArrayList<>();

        for (int i = 0; i < prefs; i++) {
            list[i] = "";
        }

        for (int i = 0; i < prefs; i++) {
            List<ModifierModel> modifierModels = modifierModelDAO.getRandomMods(luckiness,
                    PREFIX,
                    modifierModelDAO.getAllMaxWeight(PREFIX) + 1,
                    1,
                    list
            );
            modelList.addAll(modifierModels);
            list[i] = modelList.get(i).tag;
        }

        List<ModifierModel> Prefixes = modelList;
        modelList = new ArrayList<>();
        luckiness = random(modifierModelDAO.getAllMaxWeight(SUFFIX), 0);
        list = new String[sufs];

        for (int i = 0; i < sufs; i++) {
            list[i] = "";
        }

        for (int i = 0; i < sufs; i++) {
            List<ModifierModel> modifierModels = modifierModelDAO.getRandomMods(luckiness,
                    SUFFIX,
                    modifierModelDAO.getAllMaxWeight(SUFFIX) + 1,
                    1,
                    list
            );
            modelList.addAll(modifierModels);
            list[i] = modelList.get(i).tag;
        }


        List<ModifierModel> Suffixes = modelList;
        itemModel.setNewAffixes(Prefixes, Suffixes);
    }

    private double random(int max, int min) {
        return (int) (Math.random() * ((max + 1) - min) + min);
    }

    @Override
    public void onClick(View view) {
        int[] affixes;
        switch (view.getId()) {
            case R.id.Alchemy:
                if (itemModel.getRarity() != 0) return;
                itemModel.setRarity(2);
                roll((int) random(3, 1), (int) random(3, 1));
                break;
            case R.id.Chaos:
                int rarity = itemModel.getRarity();
                if (rarity == 0) {
                    return;
                } else if (rarity == 1) {
                    roll((int) random(1, 0), (int) random(1, 0));
                } else {
                    roll((int) random(3, 2), (int) random(3, 2));
                }
                break;
            case R.id.Exalt:
                if (itemModel.getRarity() == 0) return;
                affixes = itemModel.getOpenAffixes();
                if (random(1, 0) == 1 && affixes[0] != 0) {
                    exalt(0);
                } else if (affixes[1] != 0) {
                    exalt(1);
                }
                break;
            case R.id.Regal:
                if (itemModel.getRarity() == 2) return;
                itemModel.setRarity(itemModel.getRarity() + 1);
                affixes = itemModel.getOpenAffixes();
                if (Math.random() > 0.5 && affixes[0] != 0) {
                    exalt(0);
                } else if (affixes[1] != 0) {
                    exalt(1);
                }
                break;
            case R.id.Scouring:
                itemModel.setRarity(0);
                break;
        }
        textViews[6].setText(itemModel.getCurrentSignatureValue());
        show();
    }

    private void show() {
        Map<Character, String> map = new HashMap<>();
        map.put(PREFIX, "Prefix");
        map.put(SUFFIX, "Suffix");
        for (int i = 0; i < 6; i++) {
            textViews[i].setText("");
        }
        textViews[6].setText(itemModel.getCurrentSignatureValue());
        ModifierModel modifierModel;
        List<ModifierModel> affixList = itemModel.getPref();
        int k = 0;
        for (int i = 0; i < affixList.size(); i++) {
            modifierModel = affixList.get(i);
            textViews[i].setText(
                    MessageFormat.format(
                            "{0} tier - {1}  {2}: {3}{4}",
                            map.get(modifierModel.type),
                            modifierModel.tier,
                            modifierModel.tag,
                            modifierModel.value,
                            modifierModel.postfix)
            );
            k = i + 1;
        }
        affixList = itemModel.getSuf();
        for (int i = 0; i < affixList.size(); i++) {
            modifierModel = affixList.get(i);
            textViews[i + k].setText(
                    MessageFormat.format(
                            "{0} tier - {1}  {2}: {3}{4}",
                            map.get(modifierModel.type),
                            modifierModel.tier,
                            modifierModel.tag,
                            modifierModel.value,
                            modifierModel.postfix)
            );

        }
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        if (toClear) {
            super.onStop();
            return;
        }
        Gson gson = new Gson();
        ItemModel itemModel1 = itemModel;
        String json = gson.toJson(itemModel1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_KEY, json);
        editor.apply();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ItemModel newItemModel = new ItemModel();

        assert data != null;
        newItemModel.setIMAGE_ID(data.getIntExtra("ITEM_IMAGE", R.drawable.sword));
        newItemModel.setName(data.getStringExtra("ITEM_NAME"));
        newItemModel.setSignature(data.getStringExtra("ITEM_SIGNATURE"));
        itemModel = newItemModel;

        ((ImageView) findViewById(R.id.Item)).setImageResource(itemModel.getIMAGE_ID());
        show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int REQUEST_CODE = 1;
        switch (item.getItemId()) {
            case R.id.infoPage:
                startActivity(new Intent(MainActivity.this, InfoActivity.class));
                break;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("NAME", itemModel.getName());
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.add:
                modifierModelDAO.insertMods(
                        new ModifierModel('p', "life", 10, '+', 10000, 4),
                        new ModifierModel('p', "life", 40, '+', 7000, 3),
                        new ModifierModel('p', "life", 70, '+', 1000, 2),
                        new ModifierModel('p', "life", 120, '+', 500, 1),

                        new ModifierModel('p', "mana", 5, '+', 15000, 4),
                        new ModifierModel('p', "mana", 25, '+', 10000, 3),
                        new ModifierModel('p', "mana", 40, '+', 2000, 2),
                        new ModifierModel('p', "mana", 60, '+', 600, 1),

                        new ModifierModel('p', "attack", 10, '+', 4000, 4),
                        new ModifierModel('p', "attack", 15, '+', 1500, 3),
                        new ModifierModel('p', "attack", 30, '+', 600, 2),
                        new ModifierModel('p', "attack", 50, '+', 200, 1),

                        new ModifierModel('p', "attack", 40, '%', 6000, 4),
                        new ModifierModel('p', "attack", 70, '%', 2000, 3),
                        new ModifierModel('p', "attack", 100, '%', 500, 2),
                        new ModifierModel('p', "attack", 140, '%', 50, 1),

                        new ModifierModel('s', "cold", 10, '%', 5000, 3),
                        new ModifierModel('s', "cold", 30, '%', 1200, 2),
                        new ModifierModel('s', "cold", 45, '%', 200, 1),

                        new ModifierModel('s', "fire", 10, '%', 5000, 3),
                        new ModifierModel('s', "fire", 30, '%', 1200, 2),
                        new ModifierModel('s', "fire", 45, '%', 200, 1),

                        new ModifierModel('s', "lightning", 10, '%', 5000, 3),
                        new ModifierModel('s', "lightning", 30, '%', 1200, 2),
                        new ModifierModel('s', "lightning", 45, '%', 200, 1),

                        new ModifierModel('s', "armour", 50, '%', 6000, 3),
                        new ModifierModel('s', "armour", 80, '%', 3000, 2),
                        new ModifierModel('s', "armour", 100, '%', 1000, 1)
                );
            case R.id.clear:
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                toClear = true;
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.getBackground().setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                view.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                view.getBackground().clearColorFilter();
                view.invalidate();
                break;
        }
        return false;
    }
}