package com.kimjongwoo.paintapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;

public class PaintActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout canvas;
    private PaintView paintView;
    private static final int MY_PERMISSION_REQUEST_CODE = 1001;
    private static final int PICK_FROM_ALBUM = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        checkPermission();
        setView();
        setCanvas();
        connection();
    }

    private void connection() {
        String url = "http://jang.anymobi.kr/";
        HashMap<String, String> input = new HashMap<>();
        input.put("name", "김종우");
        input.put("age", "29");

        ConnRetro connRetro = new ConnRetro(url, this, input);
        connRetro.connection_retrofit();
    }

    private void setView() {
        Button save_btn = findViewById(R.id.save_btn);
        Button load_btn = findViewById(R.id.load_btn);
        Button pen_btn = findViewById(R.id.pen_btn);
        Button era_btn = findViewById(R.id.era_btn);
        save_btn.setOnClickListener(this);
        load_btn.setOnClickListener(this);
        pen_btn.setOnClickListener(this);
        era_btn.setOnClickListener(this);
        canvas = findViewById(R.id.canvas);
    }

    // canvas 관련 화면 해상도 및 초기화 등 진행.
    private void setCanvas() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView = new PaintView(this);
        paintView.init(metrics);
        canvas.addView(paintView, 0);
        paintView.requestFocus();
    }

    private void saveBitmap() {
        ImageToGallery imageToGallery = new ImageToGallery();
        try {
            Bitmap well = paintView.getBitmap();
            Bitmap save = Bitmap.createBitmap(well.getWidth(), well.getHeight(), Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            Canvas now = new Canvas(save);
            now.drawRect(new Rect(0, 0, well.getWidth() - 1, well.getHeight() - 1), paint);
            now.drawBitmap(well, new Rect(0, 0, well.getWidth(), well.getHeight()), new Rect(0, 0, well.getWidth(), well.getHeight()), null);
            imageToGallery.insertImage(getContentResolver(), save);
            Toast.makeText(this, "파일 저장 완료.", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "파일이 없습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, MY_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)
                    break;
        }
    }

    public void loadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }

            Uri selectedImage = data.getData();
            try {
                String path = getPath(selectedImage);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                paintView.load_image(path, metrics);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                saveBitmap();
                break;

            case R.id.load_btn:
                loadImage();
                break;

            case R.id.pen_btn:
                paintView.setErase(false);
                paintView.setBrushSize(30);
                break;

            case R.id.era_btn:
                paintView.setErase(true);
                paintView.setBrushSize(30);
                break;
        }
    }


}
