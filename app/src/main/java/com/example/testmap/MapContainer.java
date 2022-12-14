package com.example.testmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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
    // описываем классы контейнеров
    public Map<Integer, MapPoint> mapPoints;
    public List<Route> routes;
    public List<ListSpinner> spinners;

    // загружаем данные в объекты из json
    public void loadData(Context context) {
        mapPoints = loadSourceRaw_data_vectors(context); // загрузка карты из json
        routes = loadSourceRaw_data_map(context); // загрузка пути из json
        spinners = loadSourceRaw_data_spinner(context);
    }

    // загрузка из data_vectors.json в mapPoints
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

    // загрузка из data_map.json в routes
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

    // загрузка из data_spinner.json в spinners
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

    // сама функция рисования из mapBegin в mapEnd
    public Bitmap drawPathOnMapImage (Context context, int mapBegin, int mapEnd, int mapFloor) {
        List<Integer> routelist = new ArrayList<>();

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
        int drawableTextId = 0;
        switch (mapFloor) {
            case 1: // загружаем ресурс поверх которого рисуем h1
                drawableId = R.drawable.h1;
                drawableTextId = R.drawable.h1_text;
                break;
            case 2: // загружаем ресурс поверх которого рисуем h2
                drawableId = R.drawable.h2;
                drawableTextId = R.drawable.h2_text;
                break;
            case 3: // загружаем ресурс поверх которого рисуем h3
                drawableId = R.drawable.h3;
                drawableTextId = R.drawable.h3_text;
                break;
        }

        Paint mPaint = initPaint(); // Иниц. главной кисти для пути
        Paint ePaint = initPaintE(); // Иниц. кисти для отрисовки последней точки
        Paint bPaint = initPaintB(); // Иниц. кисти для отрисовки первой точки
        Paint textPaint = initPaintText();  // Иниц. кисти для отрисовки текста поверх отрисованных путей

        // создание битмапа для рисования
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false; // убираем внутренне масштабирование
        Bitmap bitmapPlace = BitmapFactory.decodeResource(
                context.getResources(), drawableId, opts);

        Bitmap bitmapPlaceText = BitmapFactory.decodeResource(
                context.getResources(), drawableTextId, opts);

        bitmapPlace = bitmapPlace.copy(Bitmap.Config.ARGB_8888, true); // копируем результат для изменения
        Canvas bitmapPlaceDraw = new Canvas(bitmapPlace); // создаем канвас для рисования

        // Если выбранные точки не одинаковы, то будет отрисовка пути | рисовать по List
        if (!Objects.equals(mapBegin, mapEnd)) {
            // локальные буферы
            int srcW = bitmapPlace.getWidth();
            int srcH = bitmapPlace.getHeight();
            int dstW = bitmapPlaceDraw.getWidth();
            int dstH = bitmapPlaceDraw.getHeight();

            // идем по роутелисту и рисуем линии из начала в конец
            for (int i = 1; i < routelist.size(); i++) {
                MapPoint buffGetB = mapPoints.get(routelist.get(i - 1));
                MapPoint buffGetE = mapPoints.get(routelist.get(i));

                if (buffGetE == null || buffGetB == null) {
                    continue;
                }

                if (buffGetE.floor != mapFloor) {
                    continue;
                }

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

            // Проверка на первую точку и отрисовка поверх неё точки начала
            if (Objects.requireNonNull(mapPoints.get(mapBegin)).floor == mapFloor) {
                bitmapPlaceDraw.drawCircle(Objects.requireNonNull(mapPoints.get(mapBegin)).pos.get(0),
                                           Objects.requireNonNull(mapPoints.get(mapBegin)).pos.get(1), 20, bPaint);
            }

            // Проверка на последнюю точку и отрисовка поверх неё точки конца
            if (Objects.requireNonNull(mapPoints.get(mapEnd)).floor == mapFloor) {
                bitmapPlaceDraw.drawCircle(Objects.requireNonNull(mapPoints.get(mapEnd)).pos.get(0),
                                           Objects.requireNonNull(mapPoints.get(mapEnd)).pos.get(1), 20, ePaint);
            }

            bitmapPlaceDraw.drawBitmap(bitmapPlaceText, 0, 0, textPaint);  // Отрисовка текста

        } else {
            // Или отрисовка текста поверх карты этажа
            bitmapPlaceDraw.drawBitmap(bitmapPlaceText, 0, 0, textPaint);  // Отрисовка текста
        }
        return bitmapPlace;
    }

    // настройка кисти
    private Paint initPaint() {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#559cff"));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(20);
        return mPaint;

    }

    // настройка кисти для конеченой точки
    private Paint initPaintE() {
        // begin brush
        Paint ePaint = new Paint();
        ePaint.setColor(Color.parseColor("#f3412f"));
        ePaint.setStrokeCap(Paint.Cap.ROUND);
        ePaint.setAntiAlias(true);
        ePaint.setStrokeWidth(20);

        return ePaint;
    }

    // настройка кисти для начальной точки
    private Paint initPaintB() {
        // end brush
        Paint bPaint = new Paint();
        bPaint.setStrokeCap(Paint.Cap.ROUND);
        bPaint.setColor(Color.parseColor("#2663B1"));
        bPaint.setAntiAlias(true);
        bPaint.setStrokeWidth(20);

        return bPaint;
    }

    // настройка кисти для текста
    private Paint initPaintText() {
        return new Paint();
    }
}
