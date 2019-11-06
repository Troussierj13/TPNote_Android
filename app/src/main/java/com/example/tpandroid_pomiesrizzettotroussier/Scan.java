package com.example.tpandroid_pomiesrizzettotroussier;


import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.util.ArrayList;

public class Scan extends View {

    public static final String CAMERA_IMAGE_BUCKET_NAME =
                Environment.getExternalStorageDirectory().toString()
                        + "/DCIM/Camera";
    public static final String DONNEES = MediaStore.Images.Media.DATA;

    public static final String DATE_PHOTO = MediaStore.Images.Media.DATE_ADDED;

    public Scan(Context context) {
        super(context);
    }


    // Liste de l'ensemble des chemins des images du périphérique
    public static ArrayList<String> getImagePaths(Context context) {
        String[] columns = {DONNEES, DATE_PHOTO};
        // Curseur permettant de prendre toutes les images et de les trier par ordre
        // décroissant
        final Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        DATE_PHOTO + " DESC"
                );

        ArrayList<String> result = new ArrayList<>(cursor.getCount());

        if (cursor.moveToFirst()) {
            final int image_path_col = cursor.getColumnIndexOrThrow(DONNEES);
            do {
                result.add(cursor.getString(image_path_col));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return result;
    }
}
