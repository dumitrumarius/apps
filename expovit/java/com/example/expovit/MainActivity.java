package com.example.expovit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private Context context;

    private ArrayList<Game> gamesList;
    private RecyclerAdapter recyclerAdapter;

    private Spinner platform_spinner;

    private ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        imageButton =findViewById(R.id.question_mark);
        recyclerView =findViewById(R.id.recyclerView);
        int columnsNumber = Utility.calculateColumns(this, 280);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnsNumber);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        //SearchView
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchViewMain);
        searchView.setQueryHint("ex: Grand Theft Auto V");

        //RecyclerView

        platform_spinner = findViewById(R.id.platform_spinner);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        gamesList = new ArrayList<>();

        initialize();

        if (searchView != null){
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    platform_spinner.setSelection(0);
                    return true;
                }
            });
        }
        if (platform_spinner != null){
            platform_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Object object = parent.getItemAtPosition(position);
                    String platform_string = object.toString();
                    switch (platform_string){
                        case "Playstation 4":
                            getPlatform("ps4");
                            break;
                        case "Xbox One":
                            getPlatform("x1");
                            break;
                        case "Xbox 360":
                            getPlatform("x360");
                            break;
                        case "Nintendo Switch":
                            getPlatform("ns");
                            break;
                        case "PC":
                            getPlatform("pc");
                            break;
                        case "Playstation 3":
                            getPlatform("ps3");
                            break;
                            default:
                                platform_spinner.setSelection(0);
                                initialize();

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    platform_spinner.setSelection(0);
                }
            });
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, How.class);
                startActivity(intent);
            }
        });

    }

    private void initialize(){
        clearAll();

        Query query = databaseReference.child("games");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                gamesList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Game games = new Game();
                    if (snapshot != null){
                        games.setImage_path(snapshot.child("image_path").getValue().toString());
                        games.setName(snapshot.child("name").getValue().toString());
                        games.setPlatform(snapshot.child("platform").getValue().toString());
                        games.setEan(snapshot.child("ean").getValue().toString());

                        gamesList.add(games);
                    }

                }
                recyclerAdapter = new RecyclerAdapter(gamesList, context);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void clearAll(){
        if (gamesList != null){
            gamesList.clear();
            if (recyclerAdapter != null){
                recyclerAdapter.notifyDataSetChanged();
            }
        }
        gamesList = new ArrayList<>();
    }

    private void search(String string){
        ArrayList<Game> gamesSearchList = new ArrayList<>();
        for(Game object: gamesList){
            if (object.getName().toLowerCase().contains(string.toLowerCase())){
                gamesSearchList.add(object);
            }
        }
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(gamesSearchList, getApplicationContext());
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void getPlatform(String string){
        ArrayList<Game> gamesPlaformChosen = new ArrayList<>();
        for (Game object: gamesList){
            if (object.getPlatform().contains(string)) {
                gamesPlaformChosen.add(object);
            }
        }
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(gamesPlaformChosen, getApplicationContext());
        recyclerView.setAdapter(recyclerAdapter);
    }

    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int imgWidth, int imgHeight) throws WriterException {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, imgWidth, imgHeight, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
