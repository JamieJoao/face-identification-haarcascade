/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectors;

import java.io.File;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_dnn.Net;
import static org.bytedeco.opencv.global.opencv_dnn.blobFromImage;
import static org.bytedeco.opencv.global.opencv_dnn.readNetFromCaffe;
import static org.bytedeco.opencv.global.opencv_imgproc.INTER_AREA;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.core.CvType;
import presentation.Commons;

/**
 *
 * @author Jamie Joao
 */
public class GenderDetector {

    private Net genderNet;

    public GenderDetector() {
        try {
            this.genderNet = new Net();
            File protoTxt = new File(Commons.RESOURCES_PATH + "caffe/deploy_gendernet.prototxt");
            File caffeModel = new File(Commons.RESOURCES_PATH + "caffe/gender_net.caffemodel");

            genderNet = readNetFromCaffe(protoTxt.getAbsolutePath(), caffeModel.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("NO SE PUDIERON CARGAR LOS MODELOS DE DETECCIÓN DE GÉNERO");
        }
    }

    public String detect(Rect rect, Mat matOpen) {
        try {
            Mat cropFace = Commons.cropFace(rect, matOpen, false, 256, 256);

            Mat inputBlob = blobFromImage(cropFace);
            genderNet.setInput(inputBlob, "data", 1.0, null);

            Mat probability = genderNet.forward("prob");

            Indexer indexer = probability.createIndexer();
            double probMan = indexer.getDouble(0, 0);
            double probWoman = indexer.getDouble(0, 1);
            
            String gender = probMan > probWoman
                    ? "hombre"
                    : "mujer";

            return gender;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
