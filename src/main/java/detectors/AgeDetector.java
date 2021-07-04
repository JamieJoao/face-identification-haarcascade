/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectors;

import java.io.File;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_dnn.Net;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_dnn.blobFromImage;
import static org.bytedeco.opencv.global.opencv_dnn.readNetFromCaffe;
import org.bytedeco.opencv.opencv_core.Rect;
import presentation.Commons;

/**
 *
 * @author Jamie Joao
 */
public class AgeDetector {

    // private String[] ages = {"0-2", "4-6", "8-13", "15-20", "25-32", "38-43", "48-53", "60-"};
    private int[] rangeLeft = {0, 4, 8, 15, 25, 38, 48, 60};
    private int[] rangeRight = {2, 6, 13, 20, 32, 43, 53, 60};
    private Net ageNet;

    public AgeDetector() {
        try {
            ageNet = new Net();
            File protoBuf = new File(Commons.RESOURCES_PATH + "caffe/deploy_agenet.prototxt");
            File caffeModel = new File(Commons.RESOURCES_PATH + "caffe/age_net.caffemodel");

            ageNet = readNetFromCaffe(protoBuf.getAbsolutePath(), caffeModel.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] detect(Rect rect, Mat matOpen) {
        try {
            Mat cropFace = Commons.cropFace(rect, matOpen, false, 256, 256);

            Mat inputBlob = blobFromImage(cropFace);
            ageNet.setInput(inputBlob, "data", 1.0, null);

            Mat probality = ageNet.forward("prob");

            DoublePointer pointer = new DoublePointer(new double[1]);
            Point max = new Point();
            minMaxLoc(probality, null, pointer, null, max, null);

            int[] result = {rangeLeft[max.x()], rangeRight[max.x()]};

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
