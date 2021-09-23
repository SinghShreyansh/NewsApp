package com.example.chatsapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsapp.Models.NewsHeadlines;
import com.example.chatsapp.News.SelectListener;
import com.example.chatsapp.R;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    Context context;
    List<NewsHeadlines> headlines;
    SelectListener listener;

    public CustomAdapter(Context context, List<NewsHeadlines> headlines,SelectListener listener) {
        this.context = context;
        this.headlines = headlines;
        this.listener=listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.headline_list_items,parent,false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.text_title.setText(headlines.get(position).getTitle());
        holder.text_source.setText(headlines.get(position).getSource().getName());
        if (headlines.get(position).getUrlToImage()!= null){
            Glide.with(context).load(headlines.get(position).getUrlToImage())
                    .into(holder.img_headline);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnNewsClicked(headlines.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return headlines.size();
    }
}

class CustomViewHolder extends RecyclerView.ViewHolder{

    TextView text_title, text_source;
    ImageView img_headline;
    CardView cardView;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);

        text_title =itemView.findViewById(R.id.text_title);
        text_source=itemView.findViewById(R.id.text_source);
        img_headline=itemView.findViewById(R.id.img_headline);
        cardView = itemView.findViewById(R.id.main_container);
    }
}
