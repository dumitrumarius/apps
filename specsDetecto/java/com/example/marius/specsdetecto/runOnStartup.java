package com.example.marius.specsdetecto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class runOnStartup extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent intent1 = new Intent(context, SpecsActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
