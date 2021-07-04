/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package training;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;
import presentation.Commons;
import presentation.GUI;
import presentation.Log;
import presentation.Sweet;

/**
 *
 * @author Jamie Joao
 */
public class FisherTraining {

    String name = "fisherface";
    FisherFaceRecognizer fisherRecognizer;
    Log log;
    String[] names;
    int limitRecognition = 0;
    GUI gui;

    public FisherTraining(GUI gui) {
        this.gui = gui;
        this.fisherRecognizer = FisherFaceRecognizer.create();
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public void setLimitRecognition(int limitRecognition) {
        this.limitRecognition = limitRecognition;
    }

    public void train() {
        File[] usersFolder = loadUserNames();

        if (usersFolder.length >= 2) {
            ArrayList<Mat> imagesList = new ArrayList<>();
            ArrayList<Integer> labelsList = new ArrayList<>();

            for (int i = 0; i < usersFolder.length; i++) {
                File user = usersFolder[i];
                File[] userPhotos = user.listFiles();

                for (int j = 0; j < userPhotos.length; j++) {
                    File photo = userPhotos[j];
                    log.append("Leendo imagen " + photo.getAbsolutePath() + "");
                    Mat photoMat = opencv_imgcodecs.imread(photo.getAbsolutePath(), 0);

                    imagesList.add(photoMat);
                    labelsList.add(i);
                }
            }

            MatVector images = new MatVector(imagesList.size());
            Mat labels = new Mat(imagesList.size(), 1, CV_32SC1);
            IntBuffer labelsBuf = labels.createBuffer();

            for (int i = 0; i < imagesList.size(); i++) {
                images.put(i, imagesList.get(i));
                labelsBuf.put(i, labelsList.get(i));
            }

            log.append("Entrenando máquina con " + name + " Recognizer...");
            this.fisherRecognizer.train(images, labels);

            log.append("Creando el archivo de entrenamiento...");
            this.fisherRecognizer.save(Commons.RESOURCES_PATH + "models/" + name + "_recognizer_model.xml");

            log.append("Entrenamiento finalizado.");
            System.out.println(Arrays.toString(names));
        } else {
            Sweet.warning("Fisher necesita 2 o más objetos de entrenamiento");
        }
    }

    public File[] loadUserNames() {
        File storage = new File(Commons.RESOURCES_PATH + "storage/");
        File[] usersFolder = storage.listFiles();

        names = new String[usersFolder.length];
        for (int i = 0; i < usersFolder.length; i++) {
            names[i] = usersFolder[i].getName();
        }

        return usersFolder;
    }

    public void load() {
        try {
            log.append("Leendo modelo de entrenamiendo para " + name + "...");
            loadUserNames();
            this.fisherRecognizer.read(Commons.RESOURCES_PATH + "models/" + name + "_recognizer_model.xml");
            log.append("Modelo XML cargado, puede proceder con la predicción.");

        } catch (Exception e) {
            Sweet.error("No se encontró el modelo para " + name + " Recognizer");
        }
    }

    public String predict(Mat matOpen) {
        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);

        this.fisherRecognizer.predict(matOpen, label, confidence);
        this.gui.menuItemPrefection.setEnabled(true);
        
        System.out.println(confidence.get(0));

        if ((int) confidence.get(0) < limitRecognition) {
            return names[label.get(0)];
        } else {
            return "Desconocido";
        }
    }

}
