package com.example.tpandroid_pomiesrizzettotroussier;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.util.ArrayList;

/**
    Cette classe permet de réaliser un scan sur le périphérique en question
    afin de trouver le chemin de l'ensemblke des images de l'appareil
    Cette classe s'appuie sur le contexte et retourne une liste de chemin
    @author Romain
    @author Julien
    @author Matthieu
    @version 1.0
 */
public class Scan extends View {

    public static final String CAMERA_IMAGE_BUCKET_NAME =
                Environment.getExternalStorageDirectory().toString()
                        + "/DCIM/Camera";
    public static final String DONNEES_PHOTO = MediaStore.Images.Media.DATA;

    public static final String DATE_PHOTO = MediaStore.Images.Media.DATE_ADDED;

    public static final Uri URI_EXT = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public Scan(Context context) {
        super(context);
    }


    // Liste de l'ensemble des chemins des images du périphérique
    public static ArrayList<String> cheminImagePeriph(Context context) {
        String[] mes_donnees = {DONNEES_PHOTO, DATE_PHOTO};
        // Curseur permettant de prendre toutes les images et de les trier par ordre décroissant
        Cursor fetch = context.getContentResolver().query(URI_EXT,mes_donnees,null,
                null, DATE_PHOTO + " DESC");
        ArrayList<String> liste_resultat_return = new ArrayList<>(fetch.getCount());

        if (fetch.moveToFirst()) {
            int image_path_col = fetch.getColumnIndexOrThrow(DONNEES_PHOTO);
            do {
                liste_resultat_return.add(fetch.getString(image_path_col));
            } while (fetch.moveToNext());
        }
        fetch.close();
        return liste_resultat_return;
    }
}
