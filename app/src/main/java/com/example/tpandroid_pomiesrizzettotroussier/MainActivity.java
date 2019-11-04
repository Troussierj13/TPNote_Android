package com.example.tpandroid_pomiesrizzettotroussier;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DrawImageView view = new DrawImageView(this);
        setContentView(view);
    }
}
