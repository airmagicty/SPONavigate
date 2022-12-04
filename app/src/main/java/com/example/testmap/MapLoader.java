package com.example.testmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

// оптимизация инициализации с помощью фонового потока
public class MapLoader extends AsyncTask<Void, Void, Void> {

    // создание объектов для передачи наружу
    @SuppressLint("StaticFieldLeak")
    Context context;
    @SuppressLint("StaticFieldLeak")
    MainActivity main;

    // создание контейнера с данными
    MapContainer dataSave;

    // связываем глобальные переменные с внутренними
    public MapLoader(Context context, MainActivity main) {
        this.context = context;
        this.main = main;
    }

    // производим инициализацию и записываем ее в переменную
    @Override
    protected Void doInBackground(Void... voids) {
        MapContainer container = new MapContainer();
        container.loadData(context);
        dataSave = container;
        return null;
    }

    // сохраняем результат и передаем его в мейн активити
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        main.saveMap(dataSave);
    }
}
