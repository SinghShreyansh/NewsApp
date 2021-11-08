package com.example.chatsapp.News;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatsapp.Models.NewsHeadlines;
import com.example.chatsapp.R;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
// declaring variables
    NewsHeadlines headlines;
    TextView txt_title,txt_author,txt_time,txt_detail,txt_content;
    ImageView img_news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

       // getting all element by id
        txt_title=findViewById(R.id.text_detailed_title);
        txt_detail=findViewById(R.id.text_detail_detail);
        txt_author=findViewById(R.id.text_detail_author);
        txt_time=findViewById(R.id.text_detail_time);
        txt_content=findViewById(R.id.text_detail_content);
        img_news= findViewById(R.id.img_detail_news);


        headlines = (NewsHeadlines) getIntent().getSerializableExtra("data");

        // setting all attributes of newsItem
        txt_title.setText(headlines.getTitle());
        txt_author.setText(headlines.getAuthor());
        txt_time.setText(headlines.getPublishedAt());
        txt_detail.setText(headlines.getDescription());
        txt_content.setText(headlines.getContent());

        Picasso.get().load(headlines.getUrlToImage()).into(img_news);
    }
}