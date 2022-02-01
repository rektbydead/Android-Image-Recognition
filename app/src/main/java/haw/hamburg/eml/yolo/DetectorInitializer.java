package haw.hamburg.eml.yolo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import haw.hamburg.eml.MainActivity;

public class DetectorInitializer {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSION_REQUEST_STORAGE = 1;

    private static MainActivity instance;
    private Detector detector;

    public DetectorInitializer(MainActivity instance) {
        this.instance = instance;
    }

    public void initializeDetector() {
        Log.d(TAG, "Detector: Chegou");
        if (!checkIfHasPermission()) {
            Log.d(TAG, "Detector: Não tinha permissao");
            askForPermission();
            return;
        }

        detector = new Detector(instance);
        Log.d(TAG, "Detector: Já tinha permissão");
    }

    private boolean checkIfHasPermission() {
        Log.d(TAG, "Detector: Verificou se tinha permissao");
        return ContextCompat.checkSelfPermission(instance, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void askForPermission() {
        Log.d(TAG, "Detector: Pediu permissao");
        ActivityCompat.requestPermissions(instance, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, MY_PERMISSION_REQUEST_STORAGE);
    }
}
