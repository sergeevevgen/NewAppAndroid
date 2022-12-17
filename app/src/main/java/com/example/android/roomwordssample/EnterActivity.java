package com.example.android.roomwordssample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class EnterActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private SurfaceView surfaceView;
    private TextView scoreTV;
    private String movingPosition = "right";
    private List<SnakePoints> snakePointsList = new ArrayList<>();
    //для отрисовки на поверхности
    private SurfaceHolder surfaceHolder;
    private int score = 0;

    //размер части змеи
    private static final  int pointSize = 28;
    //изначальный размер змеи
    private static final int defaultTalePoints = 3;
    //Цвет змеи
    private static final int snakeColor =  Color.YELLOW;
    //Скорость змеи
    private static final int snakeMovingSpeed = 800;
    //Координаты
    private int positionX, positionY;
    //Таймер
    private Timer timer;

    //Canvas
    private Canvas canvas = null;

    //Цвет точки
    private Paint pointColor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity);

        surfaceView = findViewById(R.id.surface_view);
        scoreTV = findViewById(R.id.score);

        final AppCompatImageButton topBtn = findViewById(R.id.top_btn);
        final AppCompatImageButton downBtn = findViewById(R.id.down_btn);
        final AppCompatImageButton leftBtn = findViewById(R.id.left_btn);
        final AppCompatImageButton rightBtn = findViewById(R.id.right_btn);

        //добавляю callback к surfaceview
        surfaceView.getHolder().addCallback(this);
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!movingPosition.equals("bottom")) {
                    movingPosition = "top";
                }
            }
        });
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!movingPosition.equals("top")) {
                    movingPosition = "bottom";
                }
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!movingPosition.equals("right")) {
                    movingPosition = "left";
                }
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!movingPosition.equals("left")) {
                    movingPosition = "right";
                }
            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        surfaceHolder = holder;
        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
    //инициализация
    private void init() {
        snakePointsList.clear();

        scoreTV.setText("0");

        score = 0;

        movingPosition = "right";

        int startPositionX = (pointSize) / defaultTalePoints;

        for(int i = 0; i < defaultTalePoints; ++i) {
            //добавляем изначальные ячейки в змею
            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);

            startPositionX -= (pointSize * 2);
        }

        //Создание рандомных объектов для змеи (чтобы съесть)
        addPoint();

        //Начало движения
        moveSnake();
    }

    private void addPoint() {
        //получаем размеры игрового пространства
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);

        if (randomXPosition % 2 != 0) {
            randomXPosition++;
        }

        if (randomYPosition % 2 != 0) {
            randomYPosition++;
        }

        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;
    }

    private void moveSnake() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();

                if (checkEat(headPositionX, headPositionY)) {
                    //Если чет съела
                    growSnake();

                    addPoint();
                }

                switch (movingPosition) {
                    case "right":
                        snakePointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "left":
                        snakePointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "top":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        break;
                    case "bottom":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        break;
                }

                if (checkGameOver(headPositionX, headPositionY)) {

                    //всё, конец
                    timer.purge();
                    timer.cancel();
                    AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
                    builder.setMessage("Ваш результат: " + score);
                    builder.setTitle("Game Over");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //StartNewActivity
                            Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    //Таймер в другом потоке, синхронизируем их для показа диалога
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });
                }
                else {
                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                    //Изменяем позицию головы
                    canvas.drawCircle(snakePointsList.get(0).getPositionX(),
                            snakePointsList.get(0).getPositionY(), pointSize, createPointColor());

                    //Рисуем яблочко
                    canvas.drawCircle(positionX, positionY, pointSize, createPointColor());

                    //Делаем так, чтобы остальная часть следовала за головой
                    for (int i = 1; i < snakePointsList.size(); ++i) {
                        int getTempPositionX = snakePointsList.get(i).getPositionX();
                        int getTempPositionY = snakePointsList.get(i).getPositionY();

                        snakePointsList.get(i).setPositionX(headPositionX);
                        snakePointsList.get(i).setPositionY(headPositionY);
                        canvas.drawCircle(snakePointsList.get(i).getPositionX(), snakePointsList.get(i).getPositionY(),
                                pointSize, createPointColor());

                        headPositionX = getTempPositionX;
                        headPositionY = getTempPositionY;
                    }

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }, 1000 - snakeMovingSpeed, 1000 - snakeMovingSpeed);
    }

    private void growSnake() {
        SnakePoints snakePoints = new SnakePoints(0, 0);
        //Добавил в конец хвостик
        snakePointsList.add(snakePoints);
        score++;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));
            }
        });
    }

    private boolean checkGameOver(int headPositionX, int headPositionY) {
        boolean gameOver = false;
        //Проверяем, если змея коснулась края экрана
        if (snakePointsList.get(0).getPositionX() < 0 || snakePointsList.get(0).getPositionY() < 0
                || snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakePointsList.get(0).getPositionY() >= surfaceView.getHeight()) {
            gameOver = true;
        }
        else {
            //Проверяем, если змея коснулась сама себя
            for (int i = 0; i < snakePointsList.size(); ++i) {
                if (headPositionX == snakePointsList.get(i).getPositionX() &&
                headPositionY == snakePointsList.get(i).getPositionY()) {
                    gameOver = true;
                    break;
                }
            }
        }
        return gameOver;
    }

    private boolean checkEat(int headPosX, int headPosY) {
        boolean flagX = false;
        boolean flagY = false;
        if (headPosX >= positionX - pointSize && headPosX <= positionX + pointSize){
            flagX = true;
        }
        if (headPosY >= positionY - pointSize && headPosY <= positionY + pointSize) {
            flagY = true;
        }
        return flagX && flagY;
    }

    private Paint createPointColor() {
        if (pointColor == null) {
            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);
        }
        return pointColor;
    }
}