package com.kimjongwoo.paintapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

class ImageToGallery {
    void insertImage(ContentResolver cr, Bitmap source) {

        File sdCard = Environment.getExternalStorageDirectory();
        File folder = new File(sdCard.getAbsolutePath() + "/Pictures/animobi_files");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", java.util.Locale.getDefault());
        String format_date = format.format(date);
        String file_date = "Animobi_" + format_date + ".png";
        File file = new File(folder, file_date);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, file_date);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file_date);
        values.put(MediaStore.Images.Media.DESCRIPTION, file_date);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());

        Uri url = null;

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


            if (source != null) {
                assert url != null;
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.PNG, 50, imageOut);
                } finally {
                    assert imageOut != null;
                    imageOut.close();
                }
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
            }
        }

    }
}
