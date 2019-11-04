package com.example.tpandroid_pomiesrizzettotroussier;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DrawImageView view = new DrawImageView(this);
        setContentView(view);
    }

    /**
     * Display display = getWindowManager(). getDefaultDisplay();
     * Point size = new Point();
     * display. getSize(size);
     * int width = size. x;
     * int height = size. y;
     */
}
