package com.example.marius.specsdetecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EditSpecsActivity extends AppCompatActivity{

    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_specs);
        sharedPreferences = getSharedPreferences("com.example.marius.specsdetecto", MODE_PRIVATE);

        //Set Everything Up
        final EditText setProcessorModel = findViewById(R.id.setProcessorModel);
        final EditText setBatteryCapacity = findViewById(R.id.setBatteryCapacity);
        final EditText setRamSize = findViewById(R.id.setRamSize);
        final EditText setScreenSizeWidth = findViewById(R.id.setScreenSizeWidth);
        final EditText setScreenSizeHeight = findViewById(R.id.setScreenSizeHeight);
        final EditText setBrandName = findViewById(R.id.setBrandName);
        final EditText setModelName = findViewById(R.id.setModelName);
        final EditText setScreenInches = findViewById(R.id.setScreenInches);
        final EditText setGpuModel = findViewById(R.id.setGpuModel);
        final EditText setStorageSize = findViewById(R.id.setStorageSize);
        final EditText setBackCameraMp = findViewById(R.id.setCameraMp);
        final EditText setAndroidVersion = findViewById(R.id.setAndroidVersion);
        final EditText setWarranty = findViewById(R.id.setWarranty);
        final EditText setFrontCameraMp = findViewById(R.id.setFrontCameraMp);
        final Spinner setScreenType = findViewById(R.id.setScreenTypeSpinner);
        final Spinner setRamMemoryType = findViewById(R.id.setRamTypeSpinner);
        final Spinner setStorageType = findViewById(R.id.setStorageTypeSpinner);
        final Spinner setHasSim = findViewById(R.id.setHasSim);
        final Spinner setAndroidName = findViewById(R.id.setAndroidName);
        Button saveSpecs = findViewById(R.id.saveSpecs);
        Button exit = findViewById(R.id.exit);

        //Spinner Things
        ArrayAdapter<CharSequence> screenTypeAdapter = ArrayAdapter.createFromResource(this, R.array.screen_type, android.R.layout.simple_spinner_item);
        screenTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setScreenType.setAdapter(screenTypeAdapter);
        ArrayAdapter<CharSequence> memoryTypeAdapter = ArrayAdapter.createFromResource(this, R.array.ram_memory_type, android.R.layout.simple_spinner_item);
        memoryTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setRamMemoryType.setAdapter(memoryTypeAdapter);
        ArrayAdapter<CharSequence> storageTypeAdapter = ArrayAdapter.createFromResource(this, R.array.ram_memory_type, android.R.layout.simple_spinner_item);
        storageTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setStorageType.setAdapter(storageTypeAdapter);
        ArrayAdapter<CharSequence> setHasSimAdapter = ArrayAdapter.createFromResource(this, R.array.has_sim, android.R.layout.simple_spinner_item);
        setHasSimAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setHasSim.setAdapter(setHasSimAdapter);
        ArrayAdapter<CharSequence> setAndroidNameAdapter = ArrayAdapter.createFromResource(this, R.array.android_names, android.R.layout.simple_spinner_item);
        setAndroidNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAndroidName.setAdapter(setAndroidNameAdapter);

        //Set Shared Preferences
        setProcessorModel.setText(sharedPreferences.getString("processor_model", "Unknown"));
        setScreenSizeHeight.setText(""+sharedPreferences.getInt("screen_height", 0));
        setScreenSizeWidth.setText(""+sharedPreferences.getInt("screen_width", 0));
        setBrandName.setText(sharedPreferences.getString("manufacturer", "Unknown"));
        setModelName.setText(sharedPreferences.getString("model", "Unknown"));
        setScreenInches.setText(sharedPreferences.getString("screen_inches", "0"));
        setGpuModel.setText(sharedPreferences.getString("gpu_model", "Unknown"));
        setStorageSize.setText(sharedPreferences.getString("total_storage_space", "Unknown"));
        setBackCameraMp.setText(String.valueOf(sharedPreferences.getString("back_camera_in_mp", "0")));
        setFrontCameraMp.setText(String.valueOf(sharedPreferences.getString("front_camera_in_mp", "0")));
        setAndroidVersion.setText(sharedPreferences.getString("android_version", "0"));
        setWarranty.setText(String.valueOf(sharedPreferences.getInt("warranty", 24)));

        //setScreenType.setText(sharedPreferences.getString("screen_type", "Non-HD"));
        if(sharedPreferences.getString("screen_type", "Unknown").equals("Non-HD")){
            setScreenType.setSelection(0);
        }else if(sharedPreferences.getString("screen_type", "Unknown").equals("HD")){
            setScreenType.setSelection(1);
        }else if(sharedPreferences.getString("screen_type", "Unknown").equals("Full HD")){
            setScreenType.setSelection(2);
        }else if(sharedPreferences.getString("screen_type", "Unknown").equals("4K")){
            setScreenType.setSelection(3);
        }else if(sharedPreferences.getString("screen_type", "Unknown").equals("Default")){
            setScreenType.setSelection(4);
        }

        if(sharedPreferences.getString("memory_type", "Unknown").equals("KB")){
            setRamMemoryType.setSelection(0);
        }else if(sharedPreferences.getString("memory_type", "Unknown").equals("MB")){
            setRamMemoryType.setSelection(1);
        }else if(sharedPreferences.getString("memory_type", "Unknown").equals("GB")){
            setRamMemoryType.setSelection(2);
        }

        if(sharedPreferences.getString("storage_memory_type", "Unknown").equals("KB")){
            setStorageType.setSelection(0);
        }else if(sharedPreferences.getString("storage_memory_type", "Unknown").equals("KB")){
            setStorageType.setSelection(1);
        }else if(sharedPreferences.getString("storage_memory_type", "Unknown").equals("KB")){
            setStorageType.setSelection(2);
        }

        if(sharedPreferences.getString("has_sim", "Unknown").equals("Da")){
            setHasSim.setSelection(0);
        }else if(sharedPreferences.getString("has_sim", "Unknown").equals("Nu")){
            setHasSim.setSelection(1);
        }

        if(sharedPreferences.getString("android_version_name", "Unknown").equals("KitKat")){
            setAndroidName.setSelection(0);
        }else if(sharedPreferences.getString("android_version_name", "Unknown").equals("Lollipop")){
            setAndroidName.setSelection(1);
        }else if(sharedPreferences.getString("android_version_name", "Unknown").equals("Marshmallow")) {
            setAndroidName.setSelection(2);
        }else if(sharedPreferences.getString("android_version_name", "Unknown").equals("Nougat")) {
            setAndroidName.setSelection(3);
        }else if(sharedPreferences.getString("android_version_name", "Unknown").equals("Oreo")) {
            setAndroidName.setSelection(4);
        }else if(sharedPreferences.getString("android_version_name", "Unknown").equals("Pie")) {
            setAndroidName.setSelection(5);
        }
        setBatteryCapacity.setText(""+sharedPreferences.getInt("battery_capacity", 0));
        setRamSize.setText(sharedPreferences.getString("total_ram_memory", "Unknown"));

        //Save specs
        saveSpecs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String screen_height = setScreenSizeHeight.getText().toString();
                String screen_width = setScreenSizeWidth.getText().toString();
                String processor_model = setProcessorModel.getText().toString();
                String battery_capacity = setBatteryCapacity.getText().toString();
                String brand_name = setBrandName.getText().toString();
                String model_name = setModelName.getText().toString();
                String screen_inches = setScreenInches.getText().toString();
                String gpu_model = setGpuModel.getText().toString();
                String storage_size = setStorageSize.getText().toString();
                String back_camera_mp = setBackCameraMp.getText().toString();
                String front_camera_mp = setFrontCameraMp.getText().toString();
                String android_version = setAndroidVersion.getText().toString();
                String warranty = setWarranty.getText().toString();
                //Set screen type
                String screen_type = setScreenType.getSelectedItem().toString();
                String android_version_name = setAndroidName.getSelectedItem().toString();
                //Memory value + type
                String total_memory = setRamSize.getText().toString();
                String memory_type = setRamMemoryType.getSelectedItem().toString();
                String storage_type = setStorageType.getSelectedItem().toString();
                String has_sim = setHasSim.getSelectedItem().toString();

                sharedPreferences.edit().putInt("screen_height", Integer.parseInt(screen_height)).apply();
                sharedPreferences.edit().putInt("screen_width", Integer.parseInt(screen_width)).apply();
                sharedPreferences.edit().putString("screen_type", screen_type).apply();
                sharedPreferences.edit().putString("processor_model", processor_model).apply();
                sharedPreferences.edit().putInt("battery_capacity", Integer.valueOf(battery_capacity)).apply();
                sharedPreferences.edit().putString("total_ram_memory", total_memory).apply();
                sharedPreferences.edit().putString("memory_type", memory_type).apply();
                sharedPreferences.edit().putString("manufacturer", brand_name).apply();
                sharedPreferences.edit().putString("model", model_name).apply();
                sharedPreferences.edit().putString("screen_inches", screen_inches).apply();
                sharedPreferences.edit().putString("gpu_model", gpu_model).apply();
                sharedPreferences.edit().putString("total_storage_space", storage_size).apply();
                sharedPreferences.edit().putString("storage_memory_type", storage_type).apply();
                sharedPreferences.edit().putString("back_camera_in_mp", back_camera_mp).apply();
                sharedPreferences.edit().putString("front_camera_in_mp", front_camera_mp).apply();
                sharedPreferences.edit().putString("android_version_name", android_version_name).apply();
                sharedPreferences.edit().putString("has_sim", has_sim).apply();
                sharedPreferences.edit().putString("android_version", android_version).apply();
                sharedPreferences.edit().putInt("warranty", Integer.parseInt(warranty)).apply();
                sharedPreferences.edit().putBoolean("edited", true).apply();
                Intent intent = new Intent(EditSpecsActivity.this, SpecsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        exit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    SpecsActivity.thisActivity.finish();
                    finish();
        }
        });
    }
}
