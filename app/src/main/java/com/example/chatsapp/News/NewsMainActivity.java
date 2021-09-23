package com.example.chatsapp.News;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chatsapp.Adapter.CustomAdapter;
import com.example.chatsapp.ChatActivity;
import com.example.chatsapp.GroupChatActivity;
import com.example.chatsapp.MainActivity;
import com.example.chatsapp.Models.NewsApiResponse;
import com.example.chatsapp.Models.NewsHeadlines;
import com.example.chatsapp.R;

import java.util.List;

public class NewsMainActivity extends AppCompatActivity implements SelectListener, View.OnClickListener{
    RecyclerView recyclerView;
    CustomAdapter adapter;
    ProgressDialog dialog;
    Button b1,b2,b3,b4,b5,b6,b7;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main);

        searchView=findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dialog.setTitle("Fetching news articles of "+query);
                dialog.show();
                RequestManager manager= new RequestManager(NewsMainActivity.this);
                manager.getNewsHeadlines(listener,"general",query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setTitle("Fetching news articles...");
        dialog.show();

        b1=findViewById(R.id.btn_1);
        b1.setOnClickListener(this);
        b2=findViewById(R.id.btn_2);
        b2.setOnClickListener(this);
        b3=findViewById(R.id.btn_3);
        b3.setOnClickListener(this);
        b4=findViewById(R.id.btn_4);
        b4.setOnClickListener(this);
        b5=findViewById(R.id.btn_5);
        b5.setOnClickListener(this);
        b6=findViewById(R.id.btn_6);
        b6.setOnClickListener(this);
        b7=findViewById(R.id.btn_7);
        b7.setOnClickListener(this);

        RequestManager manager= new RequestManager(this);
        manager.getNewsHeadlines(listener,"general",null);
    }

    private  final OnFetchDataListener<NewsApiResponse> listener= new OnFetchDataListener<NewsApiResponse>() {
        @Override
        public void onFetchData(List<NewsHeadlines> list, String message) {
            if (list.isEmpty()){
                Toast.makeText(NewsMainActivity.this, "No data found!!!", Toast.LENGTH_SHORT).show();
            }
            else {
                showNews(list);
                dialog.dismiss();
            }
        }

        @Override
        public void onError(String message) {
            Toast.makeText(NewsMainActivity.this, "An Error Occured!!!", Toast.LENGTH_SHORT).show();

        }
    };

    private void showNews(List<NewsHeadlines> list) {
        recyclerView = findViewById(R.id.recycler_mainNew);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        adapter = new CustomAdapter(this,list,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnNewsClicked(NewsHeadlines headlines) {
        startActivity(new Intent(NewsMainActivity.this,DetailsActivity.class)
        .putExtra("data",headlines));

    }

    @Override
    public void onClick(View view) {
        Button button =(Button) view ;
        String category = button.getText().toString();
        dialog.setTitle("Fetching news articles of " + category);
        dialog.show();

        RequestManager manager= new RequestManager(this);
        manager.getNewsHeadlines(listener,category,null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.topSetting:
                Toast.makeText(this, "Topsetting clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Groups:
                Toast.makeText(this, "Groups clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewsMainActivity.this, GroupChatActivity.class));
                break;
            case R.id.Chats:
                Toast.makeText(this, "Chats Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewsMainActivity.this, MainActivity.class));
                break;
            case R.id.News:
                Toast.makeText(NewsMainActivity.this, "You are in News Activity", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}