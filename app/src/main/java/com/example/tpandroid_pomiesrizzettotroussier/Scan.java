package com.example.tpandroid_pomiesrizzettotroussier;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Scan extends View {

        public static final String CAMERA_IMAGE_BUCKET_NAME =
                Environment.getExternalStorageDirectory().toString()
                        + "/DCIM/Camera";
        public static final String CAMERA_IMAGE_BUCKET_ID =
                getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    public Scan(Context context) {

        super(context);
    }

    /**
         * Matches code in MediaProvider.computeBucketValues. Should be a common
         * function.
         */
        public static String getBucketId(String path) {
            return String.valueOf(path.toLowerCase().hashCode());
        }


        public static List<String> getCameraImages(Context context) {
            System.out.println("1");
            final String[] projection = { MediaStore.Images.Media.DATA };
            final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
            final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };
            final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            ArrayList<String> result = new ArrayList<String>(cursor.getCount());
            if (cursor.moveToFirst()) {
                System.out.println("2");
                final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                do {
                    final String data = cursor.getString(dataColumn);
                    result.add(data);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return result;
        }
}
