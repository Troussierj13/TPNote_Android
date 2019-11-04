package com.example.tpandroid_pomiesrizzettotroussier;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size. x;
        int height = size. y;

        DrawImageView view = new DrawImageView(this, width, height);
        setContentView(view);
    }
}
