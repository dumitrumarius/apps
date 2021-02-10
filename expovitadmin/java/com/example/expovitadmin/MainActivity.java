package com.example.expovitadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private Context context;

    private ArrayList<Game> gamesList;
    private RecyclerAdapter recyclerAdapter;
    private ImageButton addNewGame;

    private Spinner platform_spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        addNewGame = findViewById(R.id.add_new_game);
        recyclerView =findViewById(R.id.recyclerView);
        int columnsNumber = Utility.calculateColumns(this, 280);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnsNumber);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        //SearchView
        final androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchViewMain);
        searchView.setQueryHint("ex: Grand Theft Auto V");

        //RecyclerView

        platform_spinner = findViewById(R.id.platform_spinner);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        gamesList = new ArrayList<>();

        initialize();

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
            }});
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
                        case "Nintendo 3DS":
                            getPlatform("3ds");
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

    }

    private void initialize(){
        clearAll();

        Query query = databaseReference.child("games");//.orderByChild("new");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                gamesList.clear();
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Game games = new Game();
                    if (snapshot != null){
                        games.setImage_path(snapshot.child("image_path").getValue().toString());
                        games.setName(snapshot.child("name").getValue().toString());
                        games.setPlatform(snapshot.child("platform").getValue().toString());
                        games.setNew(snapshot.hasChild("isNew"));
                        games.setPreorder(snapshot.hasChild("preorder"));
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
        ArrayList<Game> gamesPlatformChosen = new ArrayList<>();
        for (Game object: gamesList){
            if (object.getPlatform().contains(string)) {
                gamesPlatformChosen.add(object);
            }
        }
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(gamesPlatformChosen, getApplicationContext());
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void addNewGame(View v){
        Intent intent = new Intent(MainActivity.this, AddNewGameActivity.class);
        startActivity(intent);
    }

}
