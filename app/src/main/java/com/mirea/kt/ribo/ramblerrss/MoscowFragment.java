package com.mirea.kt.ribo.ramblerrss;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoscowFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private EditText editTextSearch;
    private Button buttonSearch;

    private RSSReader rssReader = new RSSReader();

    public MoscowFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_heading, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        editTextSearch = rootView.findViewById(R.id.editTextSearch);
        buttonSearch = rootView.findViewById(R.id.buttonSearch);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = editTextSearch.getText().toString().trim();
                searchNews(searchQuery);
            }
        });

        loadNews();

        return rootView;
    }

    private void loadNews() {
        rssReader.loadRSS("https://news.rambler.ru/rss/Moscow/", new RSSReader.OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<RSSItem> items) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsAdapter = new NewsAdapter(items);
                        recyclerView.setAdapter(newsAdapter);
                    }
                });
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void searchNews(String query) {
        if (newsAdapter != null) {
            RSSItem foundItem = null;
            for (RSSItem item : newsAdapter.newsList) {
                if (item.getTitle().trim().toLowerCase().contains(query.toLowerCase())) {
                    foundItem = item;
                    break;
                }
            }

            if (foundItem != null) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("title", foundItem.getTitle());
                intent.putExtra("description", foundItem.getDescription());
                intent.putExtra("link", foundItem.getLink());
                intent.putExtra("pubDate", foundItem.getPubDate());
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Новость не найдена", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

        private List<RSSItem> newsList;

        public NewsAdapter(List<RSSItem> newsList) {
            this.newsList = newsList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RSSItem item = newsList.get(position);
            holder.textViewTitle.setText(item.getTitle());

            String formattedDate = formatPubDate(item.getPubDate());
            holder.textViewPubDate.setText(formattedDate);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("description", item.getDescription());
                    intent.putExtra("link", item.getLink());
                    intent.putExtra("pubDate", formatPubDate(item.getPubDate()));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewTitle;
            TextView textViewPubDate;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewTitle = itemView.findViewById(R.id.textViewTitle);
                textViewPubDate = itemView.findViewById(R.id.textViewPubDate);
            }
        }
    }

    private String formatPubDate(String pubDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

        try {
            Date date = inputFormat.parse(pubDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.d("DEBUG", e.getMessage());
            return pubDate;
        }
    }
}
