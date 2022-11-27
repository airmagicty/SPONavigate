package com.example.testmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

public class MapLoader extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    Context context;
    @SuppressLint("StaticFieldLeak")
    MainActivity main;

    MapContainer dataSave;

    public MapLoader(Context context, MainActivity main) {
        this.context = context;
        this.main = main;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MapContainer container = new MapContainer();
        container.loadData(context);
        dataSave = container;
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        main.saveMap(dataSave);
    }
}
