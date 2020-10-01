package com.example.expovitadmin;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utility {
    public static int calculateColumns(Context context, float columnWidth){

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels / displayMetrics.density;
        int columns = (int) (screenWidth/columnWidth + 0.5);
        return columns;

    }
}
