/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation;

import detectors.AgeDetector;
import detectors.EmotionDetector;
import detectors.FaceDetector;
import detectors.GenderDetector;
import java.awt.List;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.bytedeco.opencv.global.opencv_imgproc;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_AA;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_FONT_HERSHEY_PLAIN;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

/**
 *
 * @author Jamie Joao
 */
public class PaintInformation {

    private final FaceDetector faceDetector;
    private final AgeDetector ageDetector;
    private final EmotionDetector emotionDetector;
    private final GenderDetector genderDetector;

    public PaintInformation() {
        this.faceDetector = new FaceDetector();
        this.ageDetector = new AgeDetector();
        this.emotionDetector = new EmotionDetector();
        this.genderDetector = new GenderDetector();
    }

    public ImageWithFaces paintGeneric(Mat mat, GUI gui) {
        ImageWithFaces result = faceDetector.detect(mat);
        Rect[] rects = result.getRects();
        Mat[] faces = result.getFaces();

        for (int i = 0; i < rects.length; i++) {
            Rect rect = rects[i];
            Mat face = faces[i];
            int[] age = ageDetector.detect(rect, mat);

            if (age[0] <= 20) {
                PaintInformation.paintGaussianBlurByAge(rect, mat);
            } else {
                PaintInformation.paintSquare(rect, mat, Scalar.WHITE);
            }

            if (gui.activeItems[0]) {
//                PaintInformation.paintSquare(rect, mat, Scalar.BLUE);
            }

            if (gui.activeItems[1]) {
                PaintInformation.paintData(rect, String.format("%s-%d", age[0], age[1]), mat, Scalar.GREEN);
//                PaintInformation.paintSquare(rect, mat, Scalar.GREEN);
            }

            if (gui.activeItems[2]) {
                String emotion = emotionDetector.detect(rect, mat);
                PaintInformation.paintData(rect, emotion, mat, Scalar.RED);
//                PaintInformation.paintSquare(rect, mat, Scalar.RED);
            }

            if (gui.activeItems[3]) {
                String gender = genderDetector.detect(rect, mat);
                PaintInformation.paintData(rect, gender, mat, Scalar.YELLOW);
//                PaintInformation.paintSquare(rect, mat, Scalar.YELLOW);
            }

            if (gui.activeItems[4]) {
                String predictInfo = "";
                Mat cloneFace = face.clone();
                opencv_imgproc.cvtColor(cloneFace, cloneFace, opencv_imgproc.COLOR_BGR2GRAY);

                if (gui.currentType.equals("Fisherface")) {
                    predictInfo = gui.fisherTraining.predict(cloneFace);
                } else if (gui.currentType.equals("Eigenface")) {
                    predictInfo = gui.eigenTraining.predict(cloneFace);
                }

                PaintInformation.paintData(rect, predictInfo, mat, Scalar.MAGENTA);
//                PaintInformation.paintSquare(rect, mat, Scalar.MAGENTA);
            }
        }

        return result;
    }

    public static void paintSquare(Rect rect, Mat matOpen, Scalar color) {
        rectangle(matOpen,
                new Point(rect.x(), rect.y()),
                new Point(rect.x() + rect.width(), rect.y() + rect.height()),
                color,
                2,
                CV_AA,
                0);
    }

    // new Scalar(255, 255, 255, 2.0)
    public static void paintData(Rect rect, String data, Mat matOpen, Scalar color) {
        int posX = Math.max(rect.x() - 10, 0);
        int posY = Math.max(rect.y() - 10, 0);

        putText(matOpen,
                data,
                new Point(posX, posY),
                CV_FONT_HERSHEY_PLAIN,
                2,
                color);
    }

    /**
     * Aplicar efecto de desenfoque gaussiano para los rostros de niños
     *
     * @param rect
     * @param matOpen
     */
    public static void paintGaussianBlurByAge(Rect rect, Mat matOpen) {
        Mat cropMat = new Mat(matOpen, rect);
        GaussianBlur(cropMat, cropMat, new Size(51, 51), 0);
    }

    public static void paintName() {

    }
}
