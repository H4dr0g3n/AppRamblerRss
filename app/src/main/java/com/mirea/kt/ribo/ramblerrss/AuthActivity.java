package com.mirea.kt.ribo.ramblerrss;

import static java.lang.String.valueOf;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText EditTextLogin, EditTextPassword;
    CheckBox authCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        EditTextLogin = findViewById(R.id.etLogin);
        EditTextPassword = findViewById(R.id.etPassword);
        Button btnAuth = findViewById(R.id.btnAuth);
        authCheckBox = findViewById(R.id.cbSaveAuth);
        btnAuth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAuth) {
            String login = EditTextLogin.getText().toString();
            String password = EditTextPassword.getText().toString();
            HTTPAuth httpAuth = new HTTPAuth();
            try {
                if (!login.isEmpty() && !password.isEmpty()) {
                    ArrayList<String> data = null;
                    try {
                        data = httpAuth.postRequest(login, password);
                    } catch (RuntimeException e) {
                        Toast error = Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_LONG);
                        error.show();
                    }
                    boolean flag = false;
                    try {
                        flag = !Objects.equals(data.get(0), "-1");
                    } catch (NullPointerException e) {
                        Log.d("DEBUG", "NullPointerException");
                    }
                    if (flag) {
                        String variant = "Вариант: " + data.get(1);
                        String title = "Заголовок: " + data.get(2);
                        String task = "Задание: " + data.get(3);
                        boolean IS_AUTHORIZED = authCheckBox.isChecked();
                        SharedPreferences sharedPreferences = getSharedPreferences("AUTH_PREFERENCES", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("IS_AUTHORIZED", IS_AUTHORIZED);
                        Log.d("IS_AUTHORIZED", valueOf(IS_AUTHORIZED));
                        editor.putBoolean("HAS_LOGGED_IN", true);
                        editor.apply();
                        showPopup(variant, title, task);
                    } else {
                        Toast error = Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_LONG);
                        error.show();
                    }
                } else {
                    Toast error = Toast.makeText(this, "Пустые текстовые поля", Toast.LENGTH_LONG);
                    error.show();
                }
            } catch (JSONException e) {
                Toast error = Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_LONG);
                error.show();
            }
        }
    }
    private void showPopup (String variant, String title, String task){
        PopupDialogFragment popupDialogFragment = new PopupDialogFragment(variant, title, task);
        popupDialogFragment.show(getSupportFragmentManager(), "PopupDialog");
    }
}