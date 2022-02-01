package haw.hamburg.eml.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;

import haw.hamburg.eml.MainActivity;
import haw.hamburg.eml.R;

public class CameraInitializer {

    private static final String TAG = "MainActivity";
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_ID = CameraBridgeViewBase.CAMERA_ID_BACK;

    private static MainActivity instance;

    private Camera camera;
    private BaseLoaderCallback baseLoaderCallback;

    public CameraInitializer(MainActivity instance) {
        this.camera = new Camera(instance.findViewById(R.id.my_camera_view), CAMERA_ID);

        this.instance = instance;
        baseLoaderCallback = new BaseLoaderCallback(this.instance) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case BaseLoaderCallback.SUCCESS: {
                        camera.enableView();
                        Log.d(TAG, "Camera: ativou com sucesso");
                        break;
                    }
                    default: {
                        super.onManagerConnected(status);
                        break;
                    }
                }
            }
        };
    }

    public BaseLoaderCallback getBaseLoaderCallBack() {
        return baseLoaderCallback;
    }

    public void initializeCamera() {
        Log.d(TAG, "Camera: Chegou");
        if (!checkIfHasPermission()) {
            Log.d(TAG, "Camera: Não tinha permissao");
            askForPermission();
            return;
        }

        camera.turnOn();
        Log.d(TAG, "Camera: Ja tinha permissao");
    }

    private boolean checkIfHasPermission() {
        Log.d(TAG, "Camera: Verificou se tinha permissao");
        return ContextCompat.checkSelfPermission(instance, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void askForPermission() {
        Log.d(TAG, "Camera: Pediu permissao");
        ActivityCompat.requestPermissions(instance, new String[]{ Manifest.permission.CAMERA }, MY_CAMERA_REQUEST_CODE);
    }
}
