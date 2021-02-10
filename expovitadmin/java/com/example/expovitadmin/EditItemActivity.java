package com.example.expovitadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

public class EditItemActivity extends AppCompatActivity {

    EditText editItemName;
    EditText editItemEan;
    ImageView editImageView;
    CheckBox editNewCheck;
    CheckBox editPreorderCheck;

    boolean isNew;
    boolean preorder;
    String name;
    String ean;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        editItemName = findViewById(R.id.editItemName);
        editItemEan = findViewById(R.id.editItemEan);
        editImageView = findViewById(R.id.editItemImage);
        editNewCheck = findViewById(R.id.isNewCheck);
        editPreorderCheck = findViewById(R.id.preorderCheck);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            isNew = extras.getBoolean("new", false);
            preorder = extras.getBoolean("preorder", false);
            name = extras.getString("name");
            ean = extras.getString("ean");
            imageUrl = extras.getString("image_path");
        }
        //Log.i("NAME", name);

        editItemName.setText(name, TextView.BufferType.EDITABLE);
        editItemEan.setText(ean, TextView.BufferType.EDITABLE);
        Log.i("IMAGE_URL", "Image url: " + imageUrl);
        Log.i("GAME_EAN", "Game ean: "+ ean);
        Picasso.get().load(imageUrl).fit().centerInside().into(editImageView);
        //new DownloadImageTask((ImageView) findViewById(R.id.editItemImage)).execute(imageUrl);
        if (isNew){
            editNewCheck.setChecked(true);
        }
        if (preorder){
            editPreorderCheck.setChecked(true);
        }


    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
