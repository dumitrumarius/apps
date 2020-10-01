package com.example.expovitadmin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Game> gamesList;
    private Context context;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();



    public RecyclerAdapter(ArrayList<Game> list, Context context){
        this.gamesList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i){

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        final Query query = databaseReference.child("games").orderByChild("name").equalTo(gamesList.get(position).name);
        holder.name.setText(gamesList.get(position).getName());
        if (gamesList.get(position).getNew()){
            holder.isNew.setVisibility(View.VISIBLE);
        }else{
            holder.isNew.setVisibility(View.INVISIBLE);
        }
        if (gamesList.get(position).getPreorder()){
            holder.preorder.setVisibility(View.VISIBLE);
        }else{
            holder.preorder.setVisibility(View.INVISIBLE);
        }

        Picasso.get().load(gamesList.get(position).getImage_path()).fit().centerInside().into(holder.gameImage, new Callback() {
            @Override
            public void onSuccess() {
                holder.gameImageProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERImage", "Error loading image.");
            }
        });

        /*holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                singleSnapshot.getRef().removeValue();
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });*/
        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    dataSnapshot.getRef().child(key).removeValue();
                    Log.i("DELETE", "Deleted " + dataSnapshot.getRef().child(key) + " from Firebase");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.addListenerForSingleValueEvent(valueEventListener);
            }
        });

    }

    @Override
    public int getItemCount(){
        return gamesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView gameImage, isNew, preorder;
        ProgressBar gameImageProgressBar;
        Button deleteItem, editItem;
        public DatabaseReference databaseReference;
        public FirebaseDatabase database;

        public ViewHolder(View itemView){
            super(itemView);
            deleteItem = itemView.findViewById(R.id.deleteButton);
            editItem = itemView.findViewById(R.id.editButton);
            preorder = itemView.findViewById(R.id.new_product_preorder);
            isNew = itemView.findViewById(R.id.new_product_flag);
            name = itemView.findViewById(R.id.game_title);
            gameImage = itemView.findViewById(R.id.gameImage);
            gameImageProgressBar = itemView.findViewById(R.id.gameImageProgressBar);
            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("games");
        }
    }
}
