package com.example.tpandroid_pomiesrizzettotroussier;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        super.onCreate(savedInstanceState);
        DrawImageView view = new DrawImageView(this, width, height);
        setContentView(view);
    }



}
