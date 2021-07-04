/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation;

import java.io.File;
import java.util.ArrayList;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;

/**
 *
 * @author Jamie Joao
 */
public class ImageWithFaces {

    private Mat source;
    private Mat imagePainted;
    private final ArrayList<Mat> faces;
    private final ArrayList<Rect> rects;

    public ImageWithFaces(Mat source) {
        this.source = source;
        this.faces = new ArrayList<>();
        this.rects = new ArrayList<>();
    }

    public void addFace(Mat face, Rect rect) {
        faces.add(face);
        rects.add(rect);
    }

    public void setImage(Mat image) {
        source = image;
    }

    public void setImagePainted(Mat imagePainted) {
        this.imagePainted = imagePainted;
    }

    public Mat[] getFaces() {
        Mat[] allfaces = new Mat[faces.size()];

        for (int i = 0; i < allfaces.length; i++) {
            allfaces[i] = faces.get(i);
        }

        return allfaces;
    }

    public Rect[] getRects() {
        Rect[] allRects = new Rect[rects.size()];

        for (int i = 0; i < allRects.length; i++) {
            allRects[i] = rects.get(i);
        }

        return allRects;
    }

    public Mat getImage() {
        return source;
    }

    public Mat getImagePainted() {
        return imagePainted;
    }

    public String writeFaces(String nameUser, int index) {
        if (faces.size() > 0) {
            try {

                String userPath = Commons.RESOURCES_PATH + "storage/" + nameUser;
                File userFolder = new File(userPath);

                if (!userFolder.exists()) {
                    userFolder.mkdirs();
                }

                String fileName = userPath + "/face_" + index + ".jpg";
                imwrite(fileName, faces.get(0));

                return fileName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
}
