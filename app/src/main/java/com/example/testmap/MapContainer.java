package com.example.testmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.ColorInt;

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

public class MapContainer {
    public Map<Integer, MapPoint> mapPoints;
    public List<Route> routes;
    public List<ListSpinner> spinners;

    public void loadData(Context context) {
        mapPoints = loadSourceRaw_data_vectors(context); // загрузка карты из json
        routes = loadSourceRaw_data_map(context); // загрузка пути из json
        spinners = loadSourceRaw_data_spinner(context);
    }

    private Map<Integer, MapPoint> loadSourceRaw_data_vectors (Context context) {
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

    private List<Route> loadSourceRaw_data_map (Context context) {
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

    private List<ListSpinner> loadSourceRaw_data_spinner (Context context) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    context.getResources().openRawResource(R.raw.data_spinner)));
            Type mapType = new TypeToken<List<ListSpinner>>() {}.getType();
            List<ListSpinner> spinners = new Gson().fromJson(reader, mapType);
            reader.close();
            return spinners;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap drawPathOnMapImage (Context context, int mapBegin, int mapEnd, int mapFloor) {
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

        Paint mPaint = initPaint();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        Bitmap bitmapPlace = BitmapFactory.decodeResource(
                context.getResources(), drawableId, opts);
        bitmapPlace = bitmapPlace.copy(Bitmap.Config.ARGB_8888, true); // копируем результат для изменения
        Canvas bitmapPlaceDraw = new Canvas(bitmapPlace); // создаем канвас для рисования

        // рисовать по List
        if (!Objects.equals(mapBegin, mapEnd)) {
            int srcW = bitmapPlace.getWidth();
            int srcH = bitmapPlace.getHeight();
            int dstW = bitmapPlaceDraw.getWidth();
            int dstH = bitmapPlaceDraw.getHeight();

            // рисуем начало и конец

            if (mapPoints.get(mapBegin).floor == mapFloor) {
                bitmapPlaceDraw.drawCircle(mapPoints.get(mapBegin).pos.get(0), mapPoints.get(mapBegin).pos.get(1), 20, mPaint);
            }

            if (mapPoints.get(mapEnd).floor == mapFloor) {
                bitmapPlaceDraw.drawCircle(mapPoints.get(mapEnd).pos.get(0), mapPoints.get(mapEnd).pos.get(1), 20, mPaint);
            }

            // идем по роутелисту и рисуем линии из начала в конец
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

                // сама линия
                bitmapPlaceDraw.drawLine(
                        bPosAx, bPosAy,
                        ePosAx, ePosAy,
                        mPaint
                );
            }
        }
        return bitmapPlace;
    }

    // настройка кисти
    private Paint initPaint() {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(20);
        return mPaint;
    }
}
