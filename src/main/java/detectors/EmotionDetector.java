/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectors;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;
import presentation.Commons;

/**
 *
 * @author Jamie Joao
 */
public class EmotionDetector {

    FisherFaceRecognizer fisherRecognizer;
    String[] labels = {"Temeroso", "Enojado", "Disgustado", "Feliz", "Neutral", "Triste", "Sorprendido"};

    public EmotionDetector() {
        this.fisherRecognizer = FisherFaceRecognizer.create();
        this.fisherRecognizer.read(Commons.RESOURCES_PATH + "models/emotion_classifier_model.xml");
    }

    public String detect(Rect rect, Mat matOpen) {
        /**
         * El modelo emotion_classifier_model.xml está entrenado con imágenes de 350x350
         */
        Mat cropFace = Commons.cropFace(rect, matOpen, true, 350, 350);

        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);

        this.fisherRecognizer.predict(cropFace, label, confidence);

        return labels[label.get()];
    }
}
