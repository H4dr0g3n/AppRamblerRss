package com.mirea.kt.ribo.ramblerrss;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RSSReader {

    private static final String TAG = "RSSReader";

    public interface OnDataLoadedListener {
        void onDataLoaded(List<RSSItem> items);
        void onError(String error);
    }

    public void loadRSS(String urlString, OnDataLoadedListener listener) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    List<RSSItem> items = parseRSS(inputStream);
                    if (listener != null) {
                        listener.onDataLoaded(items);
                    }
                } else {
                    if (listener != null) {
                        listener.onError("HTTP error code: " + responseCode);
                    }
                }
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        }).start();
    }

    private List<RSSItem> parseRSS(InputStream inputStream) throws IOException, XmlPullParserException {
        List<RSSItem> items = new ArrayList<>();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new InputStreamReader(inputStream));

        int eventType = parser.getEventType();
        RSSItem currentItem = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("item".equals(tagName)) {
                        currentItem = new RSSItem();
                    } else if (currentItem != null) {
                        if ("title".equals(tagName)) {
                            currentItem.setTitle(parser.nextText());
                        } else if ("link".equals(tagName)) {
                            currentItem.setLink(parser.nextText());
                        } else if ("description".equals(tagName)) {
                            currentItem.setDescription(parser.nextText());
                        } else if ("pubDate".equals(tagName)) {
                            currentItem.setPubDate(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("item".equals(tagName) && currentItem != null) {
                        items.add(currentItem);
                        currentItem = null;
                    }
                    break;
            }
            eventType = parser.next();
        }

        return items;
    }
}

