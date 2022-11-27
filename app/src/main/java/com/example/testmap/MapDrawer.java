package com.example.testmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// Оптимизация рассчетов
public class MapDrawer extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = "DebugMapLoader";
    // получение ресурсов
    @SuppressLint("StaticFieldLeak")
    ImageView mapImage;
    @SuppressLint("StaticFieldLeak")
    Context context;

    private MapContainer data;
    private Bitmap bitmapPlace;

    Integer mapBegin, mapEnd, mapFloor;

    public MapDrawer(Context context, MapContainer data, ImageView mapImage, Integer mapBegin, Integer mapEnd, Integer mapFloor) {
        this.context = context;
        this.mapImage = mapImage;
        this.mapBegin = mapBegin;
        this.mapEnd = mapEnd;
        this.mapFloor = mapFloor;
        this.data = data;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // рисуем
        bitmapPlace = data.drawPathOnMapImage(context, mapBegin, mapEnd, mapFloor);
        return null;
    }

    // действие после
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        mapImage.setImageBitmap(bitmapPlace);
    }
}
