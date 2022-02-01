package haw.hamburg.eml.camera;


import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import haw.hamburg.eml.MainActivity;
import haw.hamburg.eml.yolo.Detector;

public class Camera implements CameraBridgeViewBase.CvCameraViewListener2 {

    private final int CAMERA_ID;

    private Mat mRGBA, newFrame;
    protected JavaCameraView javaCameraView;

    public Camera(JavaCameraView javaCameraView, int cameraID) {
        this.javaCameraView = javaCameraView;
        this.CAMERA_ID = cameraID;
    }

    public void enableView() {
        javaCameraView.enableView();
    }

    public void turnOn() {
        javaCameraView.setCameraPermissionGranted();
        javaCameraView.setCameraIndex(CAMERA_ID);
        javaCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBA = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRGBA.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Imgproc.cvtColor(inputFrame.rgba(), mRGBA, Imgproc.COLOR_RGBA2RGB);
        Core.transpose(mRGBA, mRGBA);
        Core.flip(mRGBA, mRGBA, 1);

        if (!MainActivity.start)
            return mRGBA;

        return Detector.detectImage(mRGBA);
    }
}
