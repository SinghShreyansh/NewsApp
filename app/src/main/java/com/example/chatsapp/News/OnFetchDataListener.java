package com.example.chatsapp.News;

import com.example.chatsapp.Models.NewsHeadlines;

import java.util.List;

// Interface that help to save fetched result
public interface OnFetchDataListener<NewsApiResponse> {

    void onFetchData(List<NewsHeadlines> list, String message);
    void onError(String message);
}
