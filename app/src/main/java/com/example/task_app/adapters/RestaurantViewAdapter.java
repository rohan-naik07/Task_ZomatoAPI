package com.example.task_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.task_app.R;
import com.example.task_app.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantViewAdapter extends RecyclerView.Adapter<RestaurantViewAdapter.ResViewHolder>{
    private Context context;
    private List<Restaurant> hotelsList;

    public RestaurantViewAdapter(Context context, List<Restaurant> hotelsList) {
        this.context = context;
        this.hotelsList = hotelsList;
    }

    @NonNull
    @Override
    public ResViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant,parent,false);
        return new ResViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResViewHolder holder, int position) {
        Restaurant restaurant  = hotelsList.get(position);
        holder.hotelName.setText(restaurant.getName());
        holder.hotelAddress.setText(restaurant.getAddress());
        String imageUrl = restaurant.getPhotoUrl();
        if(!imageUrl.equals("")){
            final float scale = context.getResources().getDisplayMetrics().density;
            int Width  = (int) (120 * scale);
            int Height = (int) (120 * scale);
            Picasso.with(context).load(imageUrl).resize(Width,Height).into(holder.hotelPhotoView);
        }
    }

    @Override
    public int getItemCount() {
        return hotelsList.size();
    }

    class ResViewHolder extends RecyclerView.ViewHolder{
        TextView hotelName,hotelAddress;
        ImageView hotelPhotoView;

        public ResViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelName = itemView.findViewById(R.id.name);
            hotelPhotoView = itemView.findViewById(R.id.photo);
            hotelAddress = itemView.findViewById(R.id.address);
        }
    }
}
