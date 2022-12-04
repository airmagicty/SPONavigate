package com.example.testmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

// Оптимизация рассчетов с помощью фонового потока
public class MapDrawer extends AsyncTask<Void, Void, Void> {
    //private static final String LOG_TAG = "DebugMapLoader";
    // получение ресурсов
    @SuppressLint("StaticFieldLeak")
    ImageView mapImage;
    @SuppressLint("StaticFieldLeak")
    Context context;

    // создание контейнеров с данными
    private final MapContainer data;
    private Bitmap bitmapPlace;

    // объявление локальных переменных для работы с контекстом
    Integer mapBegin, mapEnd, mapFloor;

    // инициализируем переменные из Активити
    public MapDrawer(Context context, MapContainer data, ImageView mapImage, Integer mapBegin, Integer mapEnd, Integer mapFloor) {
        this.context = context;
        this.mapImage = mapImage;
        this.mapBegin = mapBegin;
        this.mapEnd = mapEnd;
        this.mapFloor = mapFloor;
        this.data = data;
    }

    // рисуем карту
    @Override
    protected Void doInBackground(Void... voids) {
        // рисуем
        bitmapPlace = data.drawPathOnMapImage(context, mapBegin, mapEnd, mapFloor);
        return null;
    }

    // возвращаем отрисованную текстуру контекстом
    // действие после
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        mapImage.setImageBitmap(bitmapPlace);
    }
}
