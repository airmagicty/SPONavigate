package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
//    private ScaleGestureDetector mScaleGestureDetector;
//    private float mScaleFactor = 1.0f;

    private Button buttonL, buttonR, buttonU, buttonD;
    private View map;
    private ImageButton buttonMap;
    private ImageView mapImage;
    private TextView textDebug;

    MapLoader loaderMap;

    private int rotate = 0;
    private int mapBegin = 3, mapEnd = 4, mapFloor = 3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        mapImage = findViewById(R.id.mapImage);

        buttonMap = findViewById(R.id.buttonMap);
        buttonR = findViewById(R.id.buttonR);
        buttonL = findViewById(R.id.buttonL);
        buttonU = findViewById(R.id.buttonU);
        buttonD = findViewById(R.id.buttonD);

        textDebug = findViewById(R.id.textDebug);

        buttonMap.setOnClickListener(this::newRoute);
        buttonR.setOnClickListener(this::rotateMapRight);
        buttonL.setOnClickListener(this::rotateMapLeft);
        buttonU.setOnClickListener(this::floorUp);
        buttonD.setOnClickListener(this::floorDown);

//        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    private void generateNewRoute() {
        if (mapBegin != mapEnd) {
            loaderMap = new MapLoader(this, mapImage, mapBegin, mapEnd, mapFloor);
            loaderMap.execute();
        }
        textDebug.setText("fl="+mapFloor+" b="+mapBegin+" e="+mapEnd);
    }

    // Кнопка построения маршрута
    private void newRoute(View view) {
        generateNewRoute();
    }

    // Этаж выше
    private void floorUp(View view) {
        if (mapFloor < 3) {
            mapFloor = mapFloor+1;
        }
        generateNewRoute();
    }

    // Этаж ниже
    private void floorDown(View view) {
        if (mapFloor > 1) {
            mapFloor = mapFloor-1;
        }
        generateNewRoute();
    }

    // Функция поворота Image вправо
    private void rotateMapRight(View view) {
        if (rotate < 900000) {
            rotate -= 90;
        } else {
            rotate = 0;
        }
        mapImage.animate().rotation(rotate);
    }

    // Функция поворота Image влево
    private void rotateMapLeft(View view) {
        if (rotate > -900000) {
            rotate += 90;
        } else {
            rotate = 0;
        }
        mapImage.animate().rotation(rotate);
    }

//    // масштабирование
//    public boolean onTouchEvent(MotionEvent motionEvent) {
//        mScaleGestureDetector.onTouchEvent(motionEvent);
//        return true;
//    }
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
//            mScaleFactor *= scaleGestureDetector.getScaleFactor();
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
//            map.setScaleX(mScaleFactor);
//            map.setScaleY(mScaleFactor);
//            return true;
//        }
//    }

}