package haw.hamburg.eml.yolo;

import android.os.Environment;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import haw.hamburg.eml.MainActivity;
import haw.hamburg.eml.utils.FileHandler;

public class Detector extends Thread {

    private static final String INITIAL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    private static final String OBJ_NAMES = INITIAL_PATH + "obj.names";
    private static final String CFG_PATH = INITIAL_PATH + "yolov4-obj.cfg";
    private static final String WEIGHT_PATH = INITIAL_PATH + "yolov4-obj_best.weights";

    private static final int IMAGE_SIZE = 416;
    private static final float SCALAR_FACTOR = 1/255.0f;

    private static final int FONT_FACE = 0; // FONT_HERSHEY_SIMPLEX (normal size sans-serif font)
    private static final float FONT_SCALE = 1f;
    private static final int FONT_THICKNESS = 2;

    private static final float THRESHOLD = 0.5f;
    private static final float MIN_PROBABILITY = 0.3f;

    private static List<String> labels = null;
    private static List<Scalar> labelsColor = null;

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private static final List<String> outputLayersNames = new ArrayList<>();
    private static Net network = null;
    private static List<Integer> outputLayersIndexes= null;

    private final MainActivity instance;

    public Detector(MainActivity instance) {
        this.instance = instance;
        defineVariable();
    }

    private void defineVariable() {
        FileHandler.getInstance().copyAssets(instance);
        labels = FileHandler.getInstance().getLabels(OBJ_NAMES);

        labelsColor = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < labels.size(); i++)
            labelsColor.add(new Scalar(random.nextInt(256), random.nextInt(256), random.nextInt(256)));

        network = Dnn.readNetFromDarknet(CFG_PATH, WEIGHT_PATH);
        outputLayersIndexes = network.getUnconnectedOutLayers().toList();
        int amountOfOutputLayers = outputLayersIndexes.size();
        for (int i = 0; i < amountOfOutputLayers; i++)
            outputLayersNames.add(network.getLayerNames().get(outputLayersIndexes.get(i) - 1));
    }

    public static Mat detectImage(Mat frame) {
        long start = System.currentTimeMillis();
        Mat newFrame = new Mat();
        Imgproc.resize(frame, newFrame, new Size(IMAGE_SIZE, IMAGE_SIZE));
        Mat blob = Dnn.blobFromImage(newFrame, SCALAR_FACTOR);
        network.setInput(blob);

        int amountOfOutputLayers = outputLayersIndexes.size();
        List<Mat> outputFromNetwork = new ArrayList<>();
        for (int i = 0; i < amountOfOutputLayers; i++)
            outputFromNetwork.add(network.forward(outputLayersNames.get(i)));

        List<Rect2d> boundingBoxesList = new ArrayList<>();
        List<Float> confidencesList = new ArrayList<>();
        List<Integer> classIndexesList = new ArrayList<>();

        MatOfRect2d boundingBoxes = new MatOfRect2d();
        MatOfFloat confidences = new MatOfFloat();

        int numberOfLabels = labels.size();

        for (int i = 0; i < amountOfOutputLayers; i++){
            for (int j = 0; j < outputFromNetwork.get(i).size().height; j++) {

                int indexOfMaxValue = -1;
                double maxScore = -1;
                for (int c = 0; c < numberOfLabels; c++) {
                    double temp = outputFromNetwork.get(i).get(j, c + 5)[0];
                    Log.d("MainActivity", "Class: " + labels.get(c) + " Probabilidade: " + temp);

                    if (maxScore >= temp)
                        continue;

                    maxScore = temp;
                    indexOfMaxValue = c;
                }

                float maxProbability = (float) maxScore;
                if (maxProbability <= MIN_PROBABILITY)
                    continue;

                double boxWidth = outputFromNetwork.get(i).get(j, 2)[0] * frame.width();
                double boxHeight = outputFromNetwork.get(i).get(j, 3)[0] * frame.height();
                Rect2d boxRect2d = new Rect2d(
                        (outputFromNetwork.get(i).get(j, 0)[0] * frame.width()) - (boxWidth / 2),
                        (outputFromNetwork.get(i).get(j, 1)[0] * frame.height()) - (boxHeight / 2),
                        boxWidth,
                        boxHeight
                );

                boundingBoxesList.add(boxRect2d);
                confidencesList.add(maxProbability);
                classIndexesList.add(indexOfMaxValue);
            }
        }

        boundingBoxes.fromList(boundingBoxesList);
        confidences.fromList(confidencesList);

        MatOfInt indices = new MatOfInt();
        Dnn.NMSBoxes(boundingBoxes, confidences, MIN_PROBABILITY, THRESHOLD, indices);

        if (indices.size().height > 0) {
            List<Integer> indicesList = indices.toList();
            for (int i = 0; i < indicesList.size(); i++) {
                int classIndex = classIndexesList.get(indicesList.get(i));
                double x = boundingBoxesList.get(indicesList.get(i)).x;
                double y = boundingBoxesList.get(indicesList.get(i)).y;
                double width = boundingBoxesList.get(indicesList.get(i)).width;
                double height = boundingBoxesList.get(indicesList.get(i)).height;

                Point vertex = new Point(x, y);
                Point oppositeVertex = new Point(x + width, y + height);

                Imgproc.rectangle(frame, vertex, oppositeVertex, labelsColor.get(classIndex));

                Point textPoint = new Point(x, y - 10);
                String text = labels.get(classIndex) + " : [" + decimalFormat.format(confidencesList.get(i)) + "]";
                Imgproc.putText(frame, text, textPoint,  FONT_FACE, FONT_SCALE, labelsColor.get(classIndex), FONT_THICKNESS);
            }
        }

        long end = System.currentTimeMillis();
        Log.d("MainActivity", "Took: " + (end - start) + "ms");
        return frame;
    }
}
