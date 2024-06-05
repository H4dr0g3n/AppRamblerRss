package com.mirea.kt.ribo.ramblerrss;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        TextView textViewPubDate = findViewById(R.id.textViewPubDate);

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String pubDate = getIntent().getStringExtra("pubDate");
        String link = getIntent().getStringExtra("link");

        textViewTitle.setText(title);
        textViewDescription.setText(description);
        textViewPubDate.setText(pubDate);

        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button shareButton = findViewById(R.id.button_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareNews(title, link);
            }
        });
    }
    private void shareNews(String title, String link) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Заголовок новости");
        shareIntent.putExtra(Intent.EXTRA_TEXT, title + ". Ссылка: " + link);

        startActivity(Intent.createChooser(shareIntent, "Поделиться новостью"));
    }
}