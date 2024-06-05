package com.mirea.kt.ribo.ramblerrss;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HTTPAuth {
    public ArrayList<String> postRequest(String login, String password) throws JSONException {
        String server = "https://android-for-students.ru";
        String serverPath = "/coursework/login.php";
        String group = "RIBO-02-22";
        HashMap<String, String> map = new HashMap<>();
        map.put("lgn", login);
        map.put("pwd", password);
        map.put("g", group);
        HTTPRunnable httpRunnable = new HTTPRunnable(server + serverPath, map);
        ArrayList<String> data = new ArrayList<>();
        Thread th = new Thread(httpRunnable);
        th.start();
        try {
            th.join();
        } catch (InterruptedException ex) {
            //
        } finally {
            JSONObject jsonObject = new JSONObject(httpRunnable.getResponseBody());
            int result = jsonObject.getInt("result_code");
            switch (result) {
                case 1:
                    String resultCode = jsonObject.getString("result_code");
                    String variant = jsonObject.getString("variant");
                    String title = jsonObject.getString("title");
                    String task = jsonObject.getString("task");
                    data.add(resultCode);
                    data.add(variant);
                    data.add(title);
                    data.add(task);
                default:
                    data.add("-1");
            }
        }
        return data;
    }
}
