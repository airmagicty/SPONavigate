package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "DebugMainActivity";

    private ImageButton buttonMap, buttonU, buttonD, buttonR, buttonL;
    private ImageView mapImage;
    private TextView textDebug;
    private Spinner bSpinner, eSpinner;

    MapLoader loaderMap;

    private int rotate = 0;
    private int mapBegin, mapEnd, mapFloor = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapImage = findViewById(R.id.mapImage);
        buttonMap = findViewById(R.id.buttonMap);
        buttonR = findViewById(R.id.buttonR);
        buttonL = findViewById(R.id.buttonL);
        buttonU = findViewById(R.id.buttonU);
        buttonD = findViewById(R.id.buttonD);
        bSpinner = findViewById(R.id.bSpinner);
        eSpinner = findViewById(R.id.eSpinner);

        textDebug = findViewById(R.id.textDebug);

        buttonMap.setOnClickListener(this::newRoute);
        buttonR.setOnClickListener(this::rotateMapRight);
        buttonL.setOnClickListener(this::rotateMapLeft);
        buttonU.setOnClickListener(this::floorUp);
        buttonD.setOnClickListener(this::floorDown);

        generateNewMap(); // Стартовая инициализация карты
//        spinnerInitialization(); // инициализация массива для спиннеров
    }

    private void generateNewMap() {
        loaderMap = new MapLoader(this, mapImage, mapBegin, mapEnd, mapFloor);
        loaderMap.execute();

        textDebug.setText("fl="+mapFloor+" b="+mapBegin+" e="+mapEnd);
    }

    private void spinnerInitialization () {
        String[] arraySpinner = {"Toyota","BMW","Audi"};
//        for (int i = 1; i < 100; i++) {
//            assert loaderMap != null;
//            if (loaderMap.mapPoints.get(i) != null) {
//                if (!Objects.requireNonNull(loaderMap.mapPoints.get(i)).spinnerlist)
//                    continue;
//                arraySpinner.add(Objects.requireNonNull(loaderMap.mapPoints.get(i)).name);
//            }
//        }
        ArrayAdapter<String> arraySpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        arraySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bSpinner.setAdapter(arraySpinnerAdapter);
        eSpinner.setAdapter(arraySpinnerAdapter);
    }

    // Кнопка построения маршрута
    private void newRoute (View view) {
//        mapBegin = 1;
//        mapEnd = 2;
        spinnerInitialization(); // инициализация массива для спиннеров
//        generateNewMap();
    }

    // Этаж выше
    private void floorUp (View view) {
        if (mapFloor < 3) {
            mapFloor = mapFloor+1;
        }
        generateNewMap();
    }

    // Этаж ниже
    private void floorDown (View view) {
        if (mapFloor > 1) {
            mapFloor = mapFloor-1;
        }
        generateNewMap();
    }

    // Функция поворота Image вправо
    private void rotateMapRight (View view) {
        if (rotate < 900000) {
            rotate -= 90;
        } else {
            rotate = 0;
        }
        mapImage.animate().rotation(rotate);
    }

    // Функция поворота Image влево
    private void rotateMapLeft (View view) {
        if (rotate > -900000) {
            rotate += 90;
        } else {
            rotate = 0;
        }
        mapImage.animate().rotation(rotate);
    }
}