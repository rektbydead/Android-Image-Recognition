package haw.hamburg.eml;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import haw.hamburg.eml.camera.CameraInitializer;
import haw.hamburg.eml.utils.EventsInitializer;
import haw.hamburg.eml.yolo.DetectorInitializer;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    private static final String TAG = "MainActivity";
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_PERMISSION_REQUEST_STORAGE = 1;

    private CameraInitializer cameraInitializer;
    private DetectorInitializer detectorInitializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
        setContentView(R.layout.activity_main);

        EventsInitializer.getInstance().initializeEvents(this);

        OpenCVLoader.initDebug();

        detectorInitializer = new DetectorInitializer(this);
        detectorInitializer.initializeDetector();

        cameraInitializer = new CameraInitializer(this);
        cameraInitializer.initializeCamera();
        cameraInitializer.getBaseLoaderCallBack().onManagerConnected(BaseLoaderCallback.SUCCESS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_CAMERA_REQUEST_CODE: {
                boolean permission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d(TAG, "Camera: Resultado da permissão -> " + permission);

                if (permission) cameraInitializer.initializeCamera();
                break;
            }
            case MY_PERMISSION_REQUEST_STORAGE: {
                boolean permission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d(TAG, "DETECTOR: Resultado da permissão -> " + permission);

                if (permission) detectorInitializer.initializeDetector();
                break;
            }
        }
    }
}