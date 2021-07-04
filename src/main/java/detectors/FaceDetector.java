/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectors;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_imgproc.INTER_AREA;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import presentation.Commons;
import presentation.ImageWithFaces;

/**
 *
 * @author Jamie Joao
 */
public class FaceDetector {

    CascadeClassifier faceCascade;

    public FaceDetector() {
        try {
            faceCascade = new CascadeClassifier(Commons.RESOURCES_PATH + "haars/haarcascade_frontalface_alt.xml");
        } catch (Exception e) {
            System.out.println("NO SE PUDO CARGAR EL CLASIFICADOR XML");
        }
    }

    public ImageWithFaces detect(Mat matOpen) {
        RectVector detectFaces = new RectVector();

        faceCascade.detectMultiScale(matOpen, detectFaces);

        ImageWithFaces saveImage = new ImageWithFaces(matOpen);
        Mat dstImage;

        for (int i = 0; i < detectFaces.size(); i++) {
            Rect rect = detectFaces.get(i);

            /**
             * Guardar el rostro con su respectiva recta
             */
            Rect cuttedRect = new Rect(rect.x(), rect.y(), rect.width(), rect.height());
            Mat cuttedImage = new Mat(matOpen, cuttedRect);
            dstImage = new Mat(150, 150, CV_8UC3);
            resize(cuttedImage, dstImage, new Size(150, 150), 0, 0, INTER_AREA);
            saveImage.addFace(dstImage, rect);
        }

        return saveImage;
    }
}
