package com.example.marius.specsdetecto;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SpecsActivity extends AppCompatActivity{
    byte[] byteArray;
    int clickCount;
    SharedPreferences sharedPreferences = null;
    public static Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specs);
        sharedPreferences = getSharedPreferences("com.example.marius.specsdetecto", MODE_PRIVATE);
        thisActivity = this;
        
        /*
            As there were some issues with the way the Android system outputs the system configuration to the application,
            I had to create a hidden button for these instances, which makes the affected fields editable by the user.
        */
        Button editSpecsHiddenButton = findViewById(R.id.editSpecsHiddenButton);
        TextView getProcessorModel = findViewById(R.id.getProcessorModel);
        TextView getScreenSize = findViewById(R.id.getScreenSize);
        TextView getGpuModel = findViewById(R.id.getGpuModel);
        TextView getBatteryCapacity = findViewById(R.id.getBatteryCapacity);
        TextView getStorageSize = findViewById(R.id.getStorageSize);
        TextView getBackCameraMp = findViewById(R.id.getCameraMp);
        TextView getBrand = findViewById(R.id.getBrand);
        TextView getSimState = findViewById(R.id.getSimState);
        TextView getScreenInches = findViewById(R.id.getScreenInches);
        TextView getModel = findViewById(R.id.getModel);
        TextView getRamSize = findViewById(R.id.getRamSize);
        TextView getAndroidVersion = findViewById(R.id.getAndroidVersion);
        TextView getWarranty = findViewById(R.id.getWarranty);
        TextView getFrontCameraMp = findViewById(R.id.getFrontCameraMp);

        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        ContextCompat.getColor(this, R.color.transparent),
                        ContextCompat.getColor(this, R.color.semi_transparent),
                        ContextCompat.getColor(this, R.color.white),
                        ContextCompat.getColor(this, R.color.white),
                        ContextCompat.getColor(this, R.color.white),
                        ContextCompat.getColor(this, R.color.white),
                        ContextCompat.getColor(this, R.color.white),
                        ContextCompat.getColor(this, R.color.semi_transparent),
                        ContextCompat.getColor(this, R.color.transparent)
                });
        findViewById(R.id.logo_background).setBackground(gradientDrawable);



        editSpecsHiddenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                clickCount = clickCount + 1;
                if(clickCount == 3){
                    Intent intent = new Intent(SpecsActivity.this, CheckPasswordActivity.class);
                    startActivity(intent);
                    clickCount = 0;
                }else{
                    int remainingSteps = 3 - clickCount;
                    Toast.makeText(getApplicationContext(), "Inca " + remainingSteps + " pasi pana la editare.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        byteArray = new byte[1024];
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WindowManager windowManager = SpecsActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int width = 0;
        int height = 0;
        //Get Real Screen Dimensions and save them in sharedPreferences
        if( Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17 ){
            try{
                width = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                height = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            }catch (Exception ignored){

            }
        }
        if (Build.VERSION.SDK_INT >= 17){
            try{
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                width = realSize.x;
                height = realSize.y;
            } catch (Exception ignored){

            }
        }
        String screenResolution = null;
        if (width < 720){
            screenResolution = "Non-HD";
        }else if(width >= 720 && width < 1080){
            screenResolution = "HD";
        }else if(width >= 1080 && width < 2160) {
            screenResolution = "Full HD";
        }
        if(!sharedPreferences.getBoolean("edited", false)){
            sharedPreferences.edit().putInt("screen_width", width).apply();
            sharedPreferences.edit().putInt("screen_height", height).apply();
            sharedPreferences.edit().putString("screen_type", screenResolution).apply();
        }
        sharedPreferences.edit().putString("screen_type", screenResolution).apply();
        getScreenSize.setText(sharedPreferences.getInt("screen_height", 0) + "x" + sharedPreferences.getInt("screen_width", 0) + " " +sharedPreferences.getString("screen_type", null));

        //Verify Cpu Model
        if (getCpuInfoMap().get("model name") == null){
            if(getCpuInfoMap().get("Hardware") == null){
                if(!sharedPreferences.getBoolean("edited",false)){
                    getProcessorModel.setText("Unknown");
                    sharedPreferences.edit().putString("processor_model", "Unknown").apply();
                }else{
                    getProcessorModel.setText(sharedPreferences.getString("processor_model", "Unknown"));
                }
            }else{
                if(!sharedPreferences.getBoolean("edited", false)){
                    getProcessorModel.setText(getCpuInfoMap().get("Hardware"));
                    sharedPreferences.edit().putString("processor_model", getCpuInfoMap().get("Hardware")).apply();
                }else{
                    getProcessorModel.setText(sharedPreferences.getString("processor_model", "Unkown"));
                }
            }
        }else{
            if(!sharedPreferences.getBoolean("edited", false)){
                getProcessorModel.setText(getCpuInfoMap().get("model name"));
                sharedPreferences.edit().putString("processor_model", getCpuInfoMap().get("model name")).apply();
            }else{
                getProcessorModel.setText(sharedPreferences.getString("processor_model", "Unknown"));
            }
        }

        //Set Battery Capacity
        if (!sharedPreferences.getBoolean("edited", false)){
            double battery_size = getBatteryCapacity(this);
            sharedPreferences.edit().putInt("battery_capacity", (int) battery_size).apply();
            getBatteryCapacity.setText(String.valueOf(sharedPreferences.getInt("battery_capacity", 0) + " mAh"));
        }else{
            getBatteryCapacity.setText(String.valueOf(sharedPreferences.getInt("battery_capacity", 0) + " mAh"));
        }


        //Get Ram Size Gradually
        if(!sharedPreferences.getBoolean("edited", false)){
            long totalMemory = totalMemoryRamSize();
            long memoryDivided = totalMemory/(1024*1024);
            sharedPreferences.edit().putString("total_ram_memory", String.valueOf(memoryDivided)).apply();
            String memory_type = null;
            if (totalMemory >= 1024){
                memory_type = "KB";
                totalMemory /= 1024;
                if(totalMemory >= 1024){
                    memory_type = "MB";
                    totalMemory /= 1024;
                    if(totalMemory >= 1024){
                        memory_type = "GB";
                    }
                }
            }
            sharedPreferences.edit().putString("memory_type", memory_type).apply();
            if(memoryDivided <= 512){
                getRamSize.setText(String.valueOf(512) + " " + sharedPreferences.getString("memory_type", "Unknown") + " RAM");
                sharedPreferences.edit().putString("total_ram_memory", String.valueOf(512)).apply();
            }else if(memoryDivided > 512 && memoryDivided <= 1024){
                getRamSize.setText(String.valueOf(1.0) + " " + sharedPreferences.getString("memory_type", "Unknown") + " RAM");
                sharedPreferences.edit().putString("total_ram_memory", String.valueOf(1.0)).apply();
            }else if(memoryDivided > 1024 && memoryDivided <= 1512){
                getRamSize.setText(String.valueOf(1.5) + " " + sharedPreferences.getString("memory_type", "Unknown") + " RAM");
                sharedPreferences.edit().putString("total_ram_memory", String.valueOf(1.5)).apply();
            }else if(memoryDivided > 1512 && memoryDivided <= 2048){
                getRamSize.setText(String.valueOf(2.0) + " " + sharedPreferences.getString("memory_type", "Unknown") + " RAM");
                sharedPreferences.edit().putString("total_ram_memory", String.valueOf(2.0)).apply();
            }else if(memoryDivided > 2048 && memoryDivided <= 2560){
                getRamSize.setText(String.valueOf(2.5) + " " + sharedPreferences.getString("memory_type", "Unknown") + " RAM");
                sharedPreferences.edit().putString("total_ram_memory", String.valueOf(2.5)).apply();
            }else if(memoryDivided > 2560 && memoryDivided <= 3072){
                getRamSize.setText(String.valueOf(3.0) + " " + sharedPreferences.getString("memory_type", "Unknown") + " RAM");
                sharedPreferences.edit().putString("total_ram_memory", String.valueOf(3.0)).apply();
            }else if(memoryDivided > 3072 && memoryDivided <= 3584){
                getRamSize.setText(String.valueOf(3.5) + " " + sharedPreferences.getString("memory_type", "Unknown") + " RAM");
                sharedPreferences.edit().putString("total_ram_memory", String.valueOf(3.5)).apply();
            }else if(memoryDivided > 3584 && memoryDivided <= 4096){
                getRamSize.setText(String.valueOf(4.0) + " " + sharedPreferences.getString("memory_type", "Unknown") + " RAM");
                sharedPreferences.edit().putString("total_ram_memory", String.valueOf(4.0)).apply();
            }
        }else{
            getRamSize.setText(String.valueOf(sharedPreferences.getString("total_ram_memory", "Unknown")) + " " + sharedPreferences.getString("memory_type", "Unknown") + " RAM");
        }

        //Get Storage Space
        if(!sharedPreferences.getBoolean("edited", false)){
            StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            long bytesAvailable = 0;
            if( Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 18 ){
                    bytesAvailable = (long)statFs.getBlockCount() *  (long)statFs.getBlockSize();
            }
            if (Build.VERSION.SDK_INT >= 18){
                    bytesAvailable = statFs.getBlockSizeLong() * statFs.getBlockCountLong();
            }
            long storageAvailable = bytesAvailable/(1024*1024);
            sharedPreferences.edit().putString("total_storage_space", String.valueOf(storageAvailable)).apply();
            String memory_type_storage = null;
            if (bytesAvailable >= 1024){
                memory_type_storage = "KB";
                bytesAvailable /= 1024;
                if(bytesAvailable >= 1024){
                    memory_type_storage = "MB";
                    bytesAvailable /= 1024;
                    if(bytesAvailable >= 1024){
                        memory_type_storage = "GB";
                    }
                }
            }
            getStorageSize.setText(String.valueOf(sharedPreferences.getString("total_storage_space", "Unknown"))+" "+ sharedPreferences.getString("storage_memory_type", "Unknown") + " Stocare");
        }else{
            getStorageSize.setText(String.valueOf(sharedPreferences.getString("total_storage_space", "Unknown"))+" "+ sharedPreferences.getString("storage_memory_type", "Unknown") + " Stocare");
        }


        //Camera Back, Front, Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        }
        if (!sharedPreferences.getBoolean("edited", false)){
            int a=2;
            sharedPreferences.edit().putString("back_camera_in_mp", String.valueOf(Math.round(getBackCameraResolutionInMegaPixels()))).apply();
            if (sharedPreferences.getString("back_camera_in_mp", "0").equals("0")){
                sharedPreferences.edit().putBoolean("has_back_camera", false).apply();
                getBackCameraMp.setText("Nu");
            }else{
                getBackCameraMp.setText(sharedPreferences.getString("back_camera_in_mp", "0") + " MP Spate");
            }
            sharedPreferences.edit().putString("front_camera_in_mp", String.valueOf((float)(((int)(Math.pow(10,a)*getFrontCameraResolutionInMegaPixels()))/Math.pow(10,a)))).apply();
            if (sharedPreferences.getString("front_camera_in_mp", "0").equals("0")){
                sharedPreferences.edit().putBoolean("has_front_camera", false).apply();
                getFrontCameraMp.setText("Nu");
            }else{
                getFrontCameraMp.setText(sharedPreferences.getString("front_camera_in_mp", "0") + " MP Fata");
            }
        }else{
            if (sharedPreferences.getString("back_camera_in_mp", "Unknown").equals("0")){
                getBackCameraMp.setText("Nu");
            }else{
                getBackCameraMp.setText(sharedPreferences.getString("back_camera_in_mp", "0") + " MP Spate");
            }
            if (sharedPreferences.getString("front_camera_in_mp", "0").equals("0")){
                getFrontCameraMp.setText("Nu");
            }else{
                getFrontCameraMp.setText(sharedPreferences.getString("front_camera_in_mp", "0") + " MP Fata");
            }
        }



        //Setup Manufacturer
        if (!sharedPreferences.getBoolean("edited", false)){
            sharedPreferences.edit().putString("manufacturer", Build.MANUFACTURER).apply();
            getBrand.setText(sharedPreferences.getString("manufacturer", "Unknown"));
        }else{
            getBrand.setText(sharedPreferences.getString("manufacturer", "Unknown"));
        }

        //Setup Model
        if (!sharedPreferences.getBoolean("edited", false)){
            sharedPreferences.edit().putString("model", Build.MODEL).apply();
            getModel.setText(sharedPreferences.getString("model", "Unknown"));
        }else{
            getModel.setText(sharedPreferences.getString("model", "Unknown"));
        }


        //Setup Android Version
        if (!sharedPreferences.getBoolean("edited", false)){
            switch (Build.VERSION.SDK_INT){
                case Build.VERSION_CODES.KITKAT:
                    sharedPreferences.edit().putString("android_version_name", "KitKat").apply();
                    break;
                case Build.VERSION_CODES.KITKAT_WATCH:
                    sharedPreferences.edit().putString("android_version_name", "KitKat").apply();
                    break;
                case Build.VERSION_CODES.LOLLIPOP:
                    sharedPreferences.edit().putString("android_version_name", "Lollipop").apply();
                    break;
                case Build.VERSION_CODES.LOLLIPOP_MR1:
                    sharedPreferences.edit().putString("android_version_name", "Lollipop").apply();
                    break;
                case Build.VERSION_CODES.M:
                    sharedPreferences.edit().putString("android_version_name", "Marshmallow").apply();
                    break;
                case Build.VERSION_CODES.N:
                    sharedPreferences.edit().putString("android_version_name", "Nougat").apply();
                    break;
                case Build.VERSION_CODES.N_MR1:
                    sharedPreferences.edit().putString("android_version_name", "Nougat").apply();
                    break;
                case Build.VERSION_CODES.O:
                    sharedPreferences.edit().putString("android_version_name", "Oreo").apply();
                    break;
                case Build.VERSION_CODES.O_MR1:
                    sharedPreferences.edit().putString("android_version_name", "Oreo").apply();
                    break;
                case Build.VERSION_CODES.P:
                    sharedPreferences.edit().putString("android_version_name", "Pie").apply();
                    break;
            }
            sharedPreferences.edit().putString("android_version", String.valueOf(Build.VERSION.RELEASE)).apply();
            getAndroidVersion.setText(sharedPreferences.getString("android_version", "Unknown") + " " + sharedPreferences.getString("android_version_name", "Unknown"));
        }else{
            getAndroidVersion.setText(sharedPreferences.getString("android_version", "Unknown") + " " + sharedPreferences.getString("android_version_name", "Unknown"));
        }

        //Screen Inches
        if(!sharedPreferences.getBoolean("edited", false)){
            sharedPreferences.edit().putString("screen_inches", String.valueOf(String.format("%.1f", getScreenSizeInches(SpecsActivity.this)))).apply();
            getScreenInches.setText(sharedPreferences.getString("screen_inches", "Unknown") + " Inch");
        }else{
            getScreenInches.setText(sharedPreferences.getString("screen_inches", "0") + " Inch");
        }

        //Check Sim Compatibility
        if(!sharedPreferences.getBoolean("edited", false)){
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getPhoneType() == telephonyManager.PHONE_TYPE_GSM){
                sharedPreferences.edit().putString("has_sim", "Da").apply();
            }else if(telephonyManager.getPhoneType() == telephonyManager.PHONE_TYPE_NONE){
                sharedPreferences.edit().putString("has_sim", "Nu").apply();
            }
            getSimState.setText(sharedPreferences.getString("has_sim", "Unknown"));
        }else{
            getSimState.setText(sharedPreferences.getString("has_sim", "Unknown"));
        }


        //Get Gpu Model
        if(!sharedPreferences.getBoolean("edited", false)){
            sharedPreferences.edit().putString("gpu_model", "Unknown");
            getGpuModel.setText(sharedPreferences.getString("gpu_model", "Unknown"));
        }else{
            getGpuModel.setText(sharedPreferences.getString("gpu_model", "Unknown"));
        }

        //Get Warranty
        if(!sharedPreferences.getBoolean("edited", false)){
            sharedPreferences.edit().putInt("warranty", 24).apply();
            getWarranty.setText(String.valueOf(sharedPreferences.getInt("warranty", 24))+ " luni");
        }else{
            getWarranty.setText(String.valueOf(sharedPreferences.getInt("warranty", 24))+ " luni");
        }

        //TODO Get Good Storage Space
        //TODO Get Good Storage Type
        //TODO Access Camera From App (Delete pictures made)
        //TODO Get Gpu Details

        //TODO Wake Lock

    }

    public static Map<String, String> getCpuInfoMap(){
        Map<String, String> map = new HashMap<String, String>();
        try{
            Scanner scanner = new Scanner(new File("/proc/cpuinfo"));
            while (scanner.hasNextLine()){
                String[] values = scanner.nextLine().split(": ");
                if (values.length > 1) map.put(values[0].trim(),values[1].trim());
            }
        }catch(Exception e){
            Log.e("getCpuInfoMap", Log.getStackTraceString(e));
        }
        return map;
    }

    public long totalMemoryRamSize(){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }

    public static String formatSize(double size){

        String type = null;

        if (size >= 1024){
            type = " KB";
            size /= 1024;
            if(size >= 1024){
                type = " MB";
                size /= 1024;
                if(size >= 1024){
                    type = " GB";
                }
            }
        }
        return type;
    }

    @Override
    public void onBackPressed(){
        //Do nothing
    }

    public float getBackCameraResolutionInMegaPixels(){
        int numOfCameras = Camera.getNumberOfCameras();
        float maxResolution_back = 0;
        long pixelCount_back = -1;
        for(int i = 0; i < numOfCameras; i++){
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                Camera camera = Camera.open(i);
                Camera.Parameters cameraParams = camera.getParameters();
                for (int j = 0; j < cameraParams.getSupportedPictureSizes().size(); j++){
                    long pixelCountTemp = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height;
                    if (pixelCountTemp > pixelCount_back){
                        pixelCount_back = pixelCountTemp;
                        maxResolution_back = ((float) pixelCountTemp) / (1024000.0f);
                    }
                }
                camera.release();
            }
        }
        return maxResolution_back;
    }
    public double getFrontCameraResolutionInMegaPixels(){
        int numOfCameras = Camera.getNumberOfCameras();
        float maxResolution_front = 0;
        long pixelCount_front = -1;
        for (int i = 0; i < numOfCameras; i++){
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                Camera camera = Camera.open(i);
                Camera.Parameters cameraParams = camera.getParameters();
                for (int j = 0; j < cameraParams.getSupportedPictureSizes().size(); j++){
                    long pixelCountTemp = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height;
                    if (pixelCountTemp > pixelCount_front){
                        pixelCount_front = pixelCountTemp;
                        maxResolution_front = ((float) pixelCountTemp) / (1024000.0f);
                    }
                }
                camera.release();
            }
        }
        return (double) maxResolution_front;
    }

    public static double getScreenSizeInches(Activity activity){
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;

        if( Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17 ){
            try{
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            }catch (Exception ignored){

            }
        }

        if (Build.VERSION.SDK_INT >= 17){
            try{
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored){

            }
        }

        DisplayMetrics display_metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
        double x = Math.pow(mWidthPixels / display_metrics.xdpi, 2);
        double y = Math.pow(mHeightPixels / display_metrics.ydpi, 2);
        return Math.sqrt(x + y);
    }
    public double getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(context);
            batteryCapacity = (double) Class.forName(POWER_PROFILE_CLASS).getMethod("getBatteryCapacity").invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;

    }
}
