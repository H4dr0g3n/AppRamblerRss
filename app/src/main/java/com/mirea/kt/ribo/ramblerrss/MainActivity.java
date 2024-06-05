package com.mirea.kt.ribo.ramblerrss;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("AUTH_PREFERENCES", Context.MODE_PRIVATE);
        boolean IS_AUTHORIZED = sharedPreferences.getBoolean("IS_AUTHORIZED", false);
        boolean HAS_LOGGED_IN = sharedPreferences.getBoolean("HAS_LOGGED_IN", false);
        if (!IS_AUTHORIZED & !HAS_LOGGED_IN) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                if (item.getItemId() == R.id.nav_moscow) {
                    fragment = new MoscowFragment();
                } else if (item.getItemId() == R.id.nav_politics) {
                    fragment = new PoliticsFragment();
                } else if (item.getItemId() == R.id.nav_community) {
                    fragment = new CommunityFragment();
                } else if (item.getItemId() == R.id.nav_incidents) {
                    fragment = new IncidentsFragment();
                } else {
                    fragment = new MoscowFragment();
                }
                openFragment(fragment);
                return true;
            }
        });
        openFragment(new MoscowFragment());
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("AUTH_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("HAS_LOGGED_IN", false);
        editor.apply();
    }
}