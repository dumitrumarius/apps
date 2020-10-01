package com.example.expovit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Game> gamesList;
    private Context context;


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
    public void onBindViewHolder(final ViewHolder holder, int position){
        holder.name.setText(gamesList.get(position).getName());
        //holder.platform.setText(gamesList.get(position).getPlatform());
        Picasso.get().load(gamesList.get(position).getImage_path()).into(holder.gameImage, new Callback() {
            @Override
            public void onSuccess() {
                holder.gameImageProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERImage", "Error loading image.");
            }
        });
        try {
            holder.ean.setImageBitmap(MainActivity.encodeAsBitmap(gamesList.get(position).getEan(), BarcodeFormat.EAN_13, 180, 50));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
        return gamesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, platform;
        ImageView gameImage, ean;
        ProgressBar gameImageProgressBar;

        public ViewHolder(View itemView){
            super(itemView);
            ean = itemView.findViewById(R.id.barcodeImage);
            name = itemView.findViewById(R.id.game_title);
            gameImage = itemView.findViewById(R.id.gameImage);
            gameImageProgressBar = itemView.findViewById(R.id.gameImageProgressBar);
        }

    }
}
