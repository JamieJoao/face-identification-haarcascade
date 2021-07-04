/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;

/**
 *
 * @author Jamie Joao
 */
public class Grabber extends Thread {

    private final GUI currentGUI;
    private FrameGrabber grabber;
    private final Log log;
    private boolean running = false, capturing = false;
    private final ArrayList<ImageWithFaces> images;
    private int limitOfFrames = 0;

    Java2DFrameConverter converter;
    OpenCVFrameConverter.ToMat matConverter;
    PaintInformation paintInformation;

    public Grabber(GUI gui, Log log) {
        this.log = log;
        this.currentGUI = gui;
        this.paintInformation = gui.paintInformation;
        this.images = new ArrayList<>();
        this.converter = new Java2DFrameConverter();
        this.matConverter = new OpenCVFrameConverter.ToMat();
    }

    @Override
    public void run() {
        this.log.append("Encendiendo cámara web...");
        openCamera();
    }

    public void openCamera() {
        try {
            this.running = true;

            this.grabber = FrameGrabber.createDefault(0);
            this.grabber.start();

            Frame frame;

            while (this.running) {
                frame = this.grabber.grab();

                if (frame != null) {
                    BufferedImage buffer;
                    Mat mat = this.matConverter.convert(frame);
                    opencv_core.flip(mat, mat, 1);

                    ImageWithFaces result = paintInformation.paintGeneric(mat, currentGUI);

                    if (this.capturing) {
                        int currentSize = this.images.size();

                        if (currentSize < this.limitOfFrames) {
                            this.images.add(result);
                            this.log.append("Se han capturado " + this.images.size() + " imágenes.");
                        } else {
                            this.capturing = false;
                            this.log.append("COMPLETADO.");
                        }
                    }

                    buffer = this.converter.convert(this.matConverter.convert(mat));
                    Commons.paintImageInLabel(buffer, this.currentGUI.labelImage);
                }
            }

        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        try {
            this.log.append("Apagando cámara web...");
            this.running = false;
            this.grabber.release();
            this.currentGUI.labelImage.setIcon(null);
            interrupt();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    public void startCapture(int limit) {
        this.limitOfFrames = limit;
        this.capturing = true;

        images.clear();
    }

    public ImageWithFaces[] getFrames() {
        ImageWithFaces[] faces = new ImageWithFaces[this.limitOfFrames];

        for (int i = 0; i < this.limitOfFrames; i++) {
            faces[i] = this.images.get(i);
        }

        return faces;
    }
}
