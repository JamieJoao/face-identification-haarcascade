/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.bytedeco.opencv.global.opencv_imgproc;
import static org.bytedeco.opencv.global.opencv_imgproc.INTER_AREA;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 *
 * @author Jamie Joao
 */
public class Commons {

    public static String BASE_PATH = "src/main/java/";
    public static String RESOURCES_PATH = "src/main/resources/";
    public static String STORAGE_PATH = "src/main/java/storage/";

    public Commons() {

    }
    
    public static org.bytedeco.opencv.opencv_core.Mat cropFace(Rect rectFace, org.bytedeco.opencv.opencv_core.Mat matOpen, boolean grayscale, int w, int h) {
        /**
         * Cortar el trozo de cara a la imagen
         */
        Rect cropRect = new Rect(rectFace.x(), rectFace.y(), rectFace.width(), rectFace.height());
        org.bytedeco.opencv.opencv_core.Mat cropImage = new org.bytedeco.opencv.opencv_core.Mat(matOpen, cropRect);

        /**
         * Estandarizar los tamaños a 256x256
         */
        org.bytedeco.opencv.opencv_core.Mat cropface = new org.bytedeco.opencv.opencv_core.Mat(w, h, CvType.CV_8UC3);
        resize(cropImage, cropface, new Size(w, h), 0, 0, INTER_AREA);

        if (grayscale) {
            opencv_imgproc.cvtColor(cropface, cropface, opencv_imgproc.COLOR_BGR2GRAY);
        }

        return cropface;
    }

    public static BufferedImage matOpenToBuffered(Mat matOpen) {
        int type = BufferedImage.TYPE_BYTE_GRAY;

        if (matOpen.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = matOpen.channels() * matOpen.cols() * matOpen.rows();
        byte[] b = new byte[bufferSize];

        matOpen.get(0, 0, b);
        BufferedImage image = new BufferedImage(matOpen.cols(), matOpen.rows(), type);

        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        System.arraycopy(b, 0, targetPixels, 0, b.length);

        return image;
    }

    public static org.opencv.core.Mat matJavaToMatOpen(org.bytedeco.javacpp.opencv_core.Mat matJava) {
        org.opencv.core.Mat matOpen = new org.opencv.core.Mat(matJava.address());

        return matOpen;
    }

    public static org.bytedeco.javacpp.opencv_core.Mat matOpenToMatJava(org.opencv.core.Mat matOpen) {
        org.bytedeco.javacpp.opencv_core.Mat matJava = new org.bytedeco.javacpp.opencv_core.Mat(matOpen.nativeObj);

        return matJava;
    }

    public static void paintImageInLabel(Mat matOpen, JLabel label) {
        if (matOpen == null) {
            label.setIcon(null);
        } else {
            ImageIcon image = new ImageIcon(Commons.matOpenToBuffered(matOpen));
            label.setIcon(image);
        }
    }

    public static void paintImageInLabel(BufferedImage buffer, JLabel label) {
        if (buffer == null) {
            label.setIcon(null);
        } else {
            ImageIcon image = new ImageIcon(buffer);
            label.setIcon(image);
        }
    }

    public static int[] getDimentions() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        int STANDAR_W = (int) width;
        int STANDAR_H = (int) height;

        int isFull = 0;

        if (STANDAR_W > 1366 && STANDAR_H > 768) {
            STANDAR_W = 1366;
            STANDAR_H = 768;
            isFull = 1;
        }

        int[] dimentions = {STANDAR_W, STANDAR_H, isFull};

        return dimentions;
    }
}
