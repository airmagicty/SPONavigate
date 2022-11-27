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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


// Оптимизация рассчетов
public class MapLoader extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = "DebugMapLoader";
    // получение ресурсов
    @SuppressLint("StaticFieldLeak")
    Context context;
    @SuppressLint("StaticFieldLeak")
    ImageView mapImage;

    Integer mapBegin, mapEnd, mapFloor;

    private Bitmap bitmapPlace;
    private Paint mPaint;
    public Map<Integer, MapPoint> mapPoints;
    public List<Route> routes;

    public MapLoader(Context context, ImageView mapImage, Integer mapBegin, Integer mapEnd, Integer mapFloor) {
        this.context = context;
        this.mapImage = mapImage;
        this.mapBegin = mapBegin;
        this.mapEnd = mapEnd;
        this.mapFloor = mapFloor;
    }

    // действие перед
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // настройка кисти
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(20);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        // инициализируем ресурсы
        mapPoints = loadSourceRaw_data_vectors(); // загрузка карты из json
        routes = loadSourceRaw_data_map(); // загрузка пути из json
        // рисуем
        drawPathOnMapImage();
        // загружаем
        return null;
    }

    // действие после
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        mapImage.setImageBitmap(bitmapPlace);
    }

    private Map<Integer, MapPoint> loadSourceRaw_data_vectors () {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    context.getResources().openRawResource(R.raw.data_vectors)));
            Type mapType = new TypeToken<Map<Integer, MapPoint>>() {}.getType();
            Map<Integer, MapPoint> mapPoints = new Gson().fromJson(reader, mapType);
            reader.close();
            return mapPoints;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Route> loadSourceRaw_data_map () {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    context.getResources().openRawResource(R.raw.data_map)));
            Type mapType = new TypeToken<List<Route>>() {}.getType();
            List<Route> routes = new Gson().fromJson(reader, mapType);
            reader.close();
            return routes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void drawPathOnMapImage () {
        List<Integer> routelist = new ArrayList<Integer>();

        // поиск path в mapPathes по полям (beg = mapBegin && end = mapEnd)
        if (!Objects.equals(mapBegin, mapEnd)) {
            for (int i = 0; i < routes.size(); i++) {
                Route buffGetI = routes.get(i);
                if (buffGetI.beg == mapBegin && buffGetI.end == mapEnd) {
                    routelist = buffGetI.path;
                }
            }
        }
//        Log.d(LOG_TAG, routelist.toString());

        // создаем изображение для рисования
        int drawableId = 0;
        switch (mapFloor) {
            case 1: // загружаем ресурс поверх которого рисуем h1
                drawableId = R.drawable.h1;
                break;
            case 2: // загружаем ресурс поверх которого рисуем h2
                drawableId = R.drawable.h2;
                break;
            case 3: // загружаем ресурс поверх которого рисуем h3
                drawableId = R.drawable.h3;
                break;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        bitmapPlace = BitmapFactory.decodeResource(
                context.getResources(), drawableId, opts);
        bitmapPlace = bitmapPlace.copy(Bitmap.Config.ARGB_8888, true); // копируем результат для изменения
        Canvas bitmapPlaceDraw = new Canvas(bitmapPlace); // создаем канвас для рисования

        // рисовать по List
        if (!Objects.equals(mapBegin, mapEnd)) {
            initPaint(); // описываем кисть
            int srcW = bitmapPlace.getWidth();
            int srcH = bitmapPlace.getHeight();
            int dstW = bitmapPlaceDraw.getWidth();
            int dstH = bitmapPlaceDraw.getHeight();

            for (int i = 1; i < routelist.size(); i++) {
                MapPoint buffGetB = mapPoints.get(routelist.get(i-1));
                MapPoint buffGetE = mapPoints.get(routelist.get(i));

                if (buffGetE == null || buffGetB == null)
                    continue;

                if (buffGetE.floor != mapFloor)
                    continue;

                // рисуем из предыдущего в следующий линию
                int bPosAx = buffGetB.pos.get(0) * dstW / srcW;
                int bPosAy = buffGetB.pos.get(1) * dstH / srcH;
                int ePosAx = buffGetE.pos.get(0) * dstW / srcW;
                int ePosAy = buffGetE.pos.get(1) * dstH / srcH;
                bitmapPlaceDraw.drawLine(
                        bPosAx, bPosAy,
                        ePosAx, ePosAy,
                        mPaint
                );
            }
        }
    }
}
