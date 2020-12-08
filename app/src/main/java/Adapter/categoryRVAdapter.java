package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import com.example.tutorial_v1.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.category_item;

public class categoryRVAdapter extends RecyclerView.Adapter<categoryRVAdapter.CustomViewHolder> {
    private final Context context;
    private final ArrayList<category_item> listItem;
    String url = "http://149.28.24.98:9000/upload/category/";

    public categoryRVAdapter(Context context, ArrayList<category_item> items)
    {
        this.context = context;
        this.listItem = items;
    }

    @NonNull
    @Override
    public categoryRVAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.category_item2, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categoryRVAdapter.CustomViewHolder holder, int position) {
        category_item item = listItem.get(position);
        holder.textView.setText(item.getCategoryName());
        Picasso.get().load(url+item.getImg()).placeholder(R.drawable.image1).error(R.drawable.image1).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.category_image);
            textView = itemView.findViewById(R.id.category_tv);
        }
    }


    //TO - DO - CATEGORY
}
