package com.example.poecraftsimulator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.poecraftsimulator.item.model.ItemModel;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        RadioButton radioButton;
        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");
        if(name.equals("Sword")){
            radioButton = findViewById(R.id.sword);
        }else{
            radioButton = findViewById(R.id.armour);
        }
        radioGroup = findViewById(R.id.radio_group);
        radioButton.setChecked(true);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        ItemModel itemModel = new ItemModel();
        Intent intent = new Intent();
        switch (i){
            case R.id.armour:
                itemModel.setName("Armour");
                itemModel.setIMAGE_ID(R.drawable.astral_plate_ic);
                intent.putExtra("ITEM_NAME", "Armour");
                intent.putExtra("ITEM_SIGNATURE", "armour");
                intent.putExtra("ITEM_IMAGE", R.drawable.astral_plate_ic);
                break;
            case R.id.sword:
                itemModel.setIMAGE_ID(R.drawable.sword);
                intent.putExtra("ITEM_SIGNATURE", "attack");
                intent.putExtra("ITEM_NAME", "Sword");
                intent.putExtra("ITEM_IMAGE", R.drawable.sword);
                break;
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}