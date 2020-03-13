package com.kimjongwoo.paintapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class PaintView extends View {
    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint bitmapPaint;
    private Paint paint;
    private boolean isErase;

    public PaintView(Context context) {
        super(context);
        int size = 30;
        path = new Path();

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        paint.setStrokeWidth(size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    public void init(DisplayMetrics metrics) {
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    public void load_image(String background_file, DisplayMetrics metrics) {
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        // 파일정보 분석 후 돌아가는 것 방지.
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(background_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert exif != null;
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap origin = BitmapFactory.decodeFile(background_file);
        origin = rotateBitmap(origin, orientation);
        assert origin != null;
        bitmap = Bitmap.createScaledBitmap(origin, width, height, true).copy(Bitmap.Config.ARGB_8888, true);
        assert bitmap != null;
        canvas = new Canvas(bitmap);
        Toast.makeText(getContext(), "이미지 불러오기 완료", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.drawPath(path, paint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touchStart(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        path.lineTo(mX, mY);
        // commit the path to our offscreen
        canvas.drawPath(path, paint);
        // kill this so we don't double draw
        path.reset();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isErase) {
                    touchStart(x, y);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(x, y, 25, paint);
                } else {
                    touchStart(x, y);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (isErase) {
                    touchMove(x, y);
                    path.reset();
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(x, y, 50, paint);
                } else {
                    touchMove(x, y);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bmp;
    }

    public void setErase(boolean b) {
        isErase = b;
        if (isErase) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
            paint.setXfermode(null);
        }
    }

    public void setBrushSize(int i) {
        paint.setStrokeWidth(i);
    }

    // 큰 이미지 미 의도 회전 발생시 정정.
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}
