package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "DebugMainActivity";

    private ImageButton buttonMap, buttonU, buttonD, buttonR, buttonL;
    private ImageView mapImage;
    private TextView textDebug;
    private Spinner bSpinner, eSpinner;

    MapDrawer loaderMap;
    public MapContainer map;

    private int rotate = 0;
    private int mapBegin, mapEnd, mapFloor = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Убирает titlebar сверху
        getSupportActionBar().hide();
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

        loadNewMap(); // Стартовая инициализация карты
        floorButtonVisible();
    }

    private void loadNewMap() {
        MapLoader loader = new MapLoader(this, this);
        loader.execute();
    }

    private void generateNewMap() {
        loaderMap = new MapDrawer(this, map, mapImage, mapBegin, mapEnd, mapFloor);
        loaderMap.execute();

//        textDebug.setText("fl="+mapFloor+" b="+mapBegin+" e="+mapEnd);
    }

    private void spinnerInitialization () {
        ArrayList<String> arraySpinner = new ArrayList<>();

        for (int i = 0; i < map.spinners.size(); i++) {
            arraySpinner.add(map.spinners.get(i).name);
        }

        ArrayAdapter<String> arraySpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        arraySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bSpinner.setAdapter(arraySpinnerAdapter);
        eSpinner.setAdapter(arraySpinnerAdapter);

        bSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                mapBegin = map.spinners.get(position).id;
                mapFloor = map.mapPoints.get(mapBegin).floor;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        eSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                mapEnd = map.spinners.get(position).id;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void saveMap (MapContainer map) {
        this.map = map;
        generateNewMap(); // отрисовка карты
        spinnerInitialization(); // инициализация массива для спиннеров
    }

    // Кнопка построения маршрута
    private void newRoute (View view) {
        generateNewMap();
    }

    // Этаж выше
    private void floorUp (View view) {
        if (mapFloor < 3) {
            mapFloor = mapFloor+1;
            generateNewMap();
        }
        floorButtonVisible();
    }

    // Этаж ниже
    private void floorDown (View view) {
        if (mapFloor > 1) {
            mapFloor = mapFloor-1;
            generateNewMap();
        }
        floorButtonVisible();
    }

    private void floorButtonVisible () {
        switch (mapFloor) {
            case 1:
                buttonD.setVisibility(View.INVISIBLE);
                buttonU.setVisibility(View.VISIBLE);
                break;
            case 2:
                buttonD.setVisibility(View.VISIBLE);
                buttonU.setVisibility(View.VISIBLE);
                break;
            case 3:
                buttonD.setVisibility(View.VISIBLE);
                buttonU.setVisibility(View.INVISIBLE);
                break;
        }
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