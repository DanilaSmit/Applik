package com.example.applik;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener  {

    private float lastX = 0, lastY = 0;
    private float alpha = 0.5f; // коэффициент сглаживания
    float x=100.0f;
    float y=100.0f;
    int count=0;

    private List<ImageView> bulletList = new ArrayList<>();
    private PlaneView planeView;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 123;
    private static final int SENSY=100;
    private static final float SENSX=120.0f;
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ImageView ufoView;
    private ImageView gunView;

    public ImageView dronView;
    private TextView countView;
    private MediaPlayer pifpafSound, end, backmusic;
    private float xfbul=492;
    private float yfbul=1460;
    private int xbul=100;
    private int ybul=100;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                BulletShooter bulletShooter = new BulletShooter(this, findViewById(R.id.camera_preview), bulletList);
                bulletShooter.fireBullet(xfbul, yfbul, xbul, ybul);
                soundPlayShoot(pifpafSound);
            }
            return true;
        });
        pifpafSound=MediaPlayer.create(this, R.raw.pifpaf);
        end=MediaPlayer.create(this,R.raw.end);
        backmusic=MediaPlayer.create(this, R.raw.backmusic);
        planeView = new PlaneView(this, findViewById(R.id.plane));
        planeView.startPlaneAnimation();
        planeView.setPlaneY((float) getScreenHeight(this) / 5);
        ufoView = findViewById(R.id.ufo);
        gunView = findViewById(R.id.gun);
        dronView=findViewById(R.id.dron);
        countView=findViewById(R.id.countView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Проверяем доступность датчиков
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null ||
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null) {
            Toast.makeText(this, "Датчики не доступны", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initializeCamera();
        }
    }



    private void soundPlayShoot(MediaPlayer sound) {
        if(sound.isPlaying()){
            sound.pause();
            sound.seekTo(0);
        }
        sound.start();
    }
    private void soundPlayBack(MediaPlayer sound) {
        if(sound.isPlaying()){
            sound.pause();
            sound.seekTo(0);
        }
        sound.start();
        sound.setLooping(true);
    }


    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

      @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        soundPlayBack(backmusic);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    private void onStartAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(dronView.getY(), 1920);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 360);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                dronView.setRotation(value);
                dronView.setTranslationY(value);
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(4000);
        valueAnimator.start();
        soundPlayShoot(end);
    }
    private void onStartAnimation2() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(ufoView.getY(), 1920);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 360);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                ufoView.setRotation(value);
                ufoView.setTranslationY(value);
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(4000);
        valueAnimator.start();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] rotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        float[] remappedRotationMatrix = new float[16];
        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);

        float[] orientations = new float[3];
        SensorManager.getOrientation(remappedRotationMatrix, orientations);
        for (int i = 0; i < 3; i++) {
            orientations[i] = (float) Math.toDegrees(orientations[i]);
        }
        dronView.bringToFront();

        // Применение экспоненциального сглаживающего фильтра
        float y = (SENSY*event.values[1]);
        float orient=Math.round(-orientations[2]);
        float xkof= (float) Math.round(Math.cos(orient)*SENSY);
        float x= (-SENSX*(event.values[0])-xkof);
        ufoView.bringToFront();
        ufoView.setX(x);
        ufoView.setY(y);
        ufoView.setScaleX(event.values[1]/10);
        ufoView.setScaleY(event.values[1]/10);
        x=alpha * x + (1 - alpha) * x+alpha*lastX;
        y=alpha * y + (1 - alpha) * y+alpha*lastY;

        lastX = x;
        lastY  = y;
        orient=orient+90;
        if(orient<-90|| orient>90){
            y=20-y;
            x=-x;
            orient=90;
        }
        dronView.setRotation(orient-90);
        ufoView.setRotation(orient-90);
        countView.bringToFront();
        BulletShooter bulletShooter = new BulletShooter(this, findViewById(R.id.camera_preview), bulletList);
        float bulletX = bulletShooter.getBulletX(0);
        float bulletY = bulletShooter.getBulletY(0);

        dronView.bringToFront();
        dronView.setX(lastX+500);
        dronView.setY(lastY-200);
        gunView.bringToFront();
        gunView.setX(0);
        gunView.setY(1520);
        checkBulletHitsDron();
        checkBulletHitsUfo();
        countView.setText(" Сбито целей = " + count);

    }
    private boolean checkBulletHitsDron() {
        FrameLayout parentView = (FrameLayout) dronView.getParent();
        if (parentView != null) {
            for (ImageView bullet : bulletList) {
                if (bullet.getX() >= dronView.getX() && bullet.getX() <= dronView.getX() + dronView.getWidth() &&
                        bullet.getY() >= dronView.getY() && bullet.getY() <= dronView.getY() + dronView.getHeight()) {
                    // Пуля попала в дрон, удаляем дрон с экрана
                    //Toast.makeText(this, "Попадание!", Toast.LENGTH_SHORT).show();
                    //parentView.removeView(dronView);
                    onStartAnimation();
                    count++;
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkBulletHitsUfo() {
        FrameLayout parentView = (FrameLayout) ufoView.getParent();
        if (parentView != null) {
            for (ImageView bullet : bulletList) {
                if (bullet.getX() >= ufoView.getX() && bullet.getX() <= ufoView.getX() + ufoView.getWidth() &&
                        bullet.getY() >= ufoView.getY() && bullet.getY() <= ufoView.getY() + ufoView.getHeight()) {
                    onStartAnimation2();
                    count++;
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void initializeCamera() {
        mCamera = getCameraInstance();
        setCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK);

        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);
    }
    private void setCameraDisplayOrientation(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {

        }
        return c;
    }

    private class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(MainActivity context, Camera camera) {
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
            }
        }
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            if (mHolder.getSurface() == null) {
                return;
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
            }
            if (mCamera.getNumberOfCameras() > 1) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                int degrees = 0;
                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 0;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 180;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 270;
                        break;}
                int result;
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = (info.orientation + degrees) % 360;
                    result = (360 - result) % 360; // compensate the mirror
                } else { // back-facing
                    result = (info.orientation - degrees + 360) % 360;
                }
                mCamera.setDisplayOrientation(result);
            }
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
            }
        }
    }
}