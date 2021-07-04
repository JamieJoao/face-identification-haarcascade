/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import training.EigenTraining;
import training.FisherTraining;

/**
 *
 * @author Jamie Joao
 */
public class GUI extends JFrame implements ActionListener {

    private final int[] dimentions;
    private final int w, h;
    private final String[] itemsBasic = {"Rostros", "Edad", "Emociones", "Género"};
    public boolean[] activeItems = new boolean[itemsBasic.length + 1];
    private int lastMaximun = 0;
    private boolean useCamera = false;
    public String currentType;

    public JLabel labelImage;
    public Grabber camera;
    public Log log;
    public Mat currentOpenedImage;

    private final Java2DFrameConverter converter;
    private final OpenCVFrameConverter.ToMat matConverter;

    public final PaintInformation paintInformation;
    public final FisherTraining fisherTraining;
    public final EigenTraining eigenTraining;

    JToggleButton btnToggleCamera;
    JMenu menuItemRegister;
    JMenuItem menuItemStartIdenfity;
    public JSlider slider;
    public JMenuItem menuItemPrefection;

    public GUI() {
        this.converter = new Java2DFrameConverter();
        this.matConverter = new OpenCVFrameConverter.ToMat();
        this.paintInformation = new PaintInformation();

        this.fisherTraining = new FisherTraining(this);
        this.eigenTraining = new EigenTraining(this);

        dimentions = Commons.getDimentions();

        this.w = dimentions[0];
        this.h = dimentions[1];
        this.activeItems[4] = false;

        initComponents();
        config();
    }

    public final void initComponents() {
        JMenuBar navbar = new JMenuBar();
        navbar.setBounds(0, 0, w, 30);

        JMenu menuFile = new JMenu("Archivo");

        JMenuItem menuItemOpen = new JMenuItem("Abrir");
        menuItemOpen.addActionListener((ActionEvent e) -> {
            openFile();
        });
        menuFile.add(menuItemOpen);

        JMenuItem menuItemExit = new JMenuItem("Salir");
        menuItemExit.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        menuFile.add(menuItemExit);

        navbar.add(menuFile);

        JMenu menuRegister = new JMenu("Básico");
        for (int i = 0; i < itemsBasic.length; i++) {
            String item = itemsBasic[i];
            JMenuItem menuItem = new JMenuItem(item);
            menuItem.addActionListener((ActionEvent e) -> {
                applyBasicDetection(item);
            });

            activeItems[i] = false;
            menuRegister.add(menuItem);
        }
        navbar.add(menuRegister);

        JMenu menuAdvanced = new JMenu("Avanzado");

        menuItemRegister = new JMenu("Registro de personas");
        menuItemRegister.setEnabled(false);
        menuAdvanced.add(menuItemRegister);

        JMenuItem menuItemCaptureFrames = new JMenuItem("Capturar fotos");
        menuItemCaptureFrames.addActionListener((ActionEvent e) -> {
            captureFrames();
        });
        menuItemRegister.add(menuItemCaptureFrames);
        JMenuItem menuItemSaveFrames = new JMenuItem("Guardar datos");
        menuItemSaveFrames.addActionListener((ActionEvent e) -> {
            saveFrames();
        });
        menuItemRegister.add(menuItemSaveFrames);

        JMenuItem menuItemTraining = new JMenuItem("Entrenamiento de máquina");
        menuItemTraining.addActionListener((ActionEvent e) -> {
            train();
        });
        menuAdvanced.add(menuItemTraining);

        JMenu menuItemIdentify = new JMenu("Identificación");
        /*
        menuItemIdentify.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                File f = new File(Commons.RESOURCES_PATH + "models/fisherface_recognizer_model.xml");
                if (f.exists()) {
                    
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
         */
        menuAdvanced.add(menuItemIdentify);

        JMenuItem menuItemLoadXML = new JMenuItem("Cargar archivo de entrenamiento XML");
        menuItemLoadXML.addActionListener((ActionEvent e) -> {
            currentType = selectType();
            if (currentType != null) {
                loadXML();
            }
        });
        menuItemIdentify.add(menuItemLoadXML);

        menuItemStartIdenfity = new JMenuItem("Identificar persona");
        menuItemStartIdenfity.setEnabled(false);
        menuItemStartIdenfity.addActionListener((ActionEvent e) -> {
            currentType = selectType();
            if (currentType != null) {
                applyBasicDetection("Identification");
            }
        });
        menuItemIdentify.add(menuItemStartIdenfity);

        navbar.add(menuAdvanced);

        JMenu menuPresition = new JMenu("Presición");
        
        menuItemPrefection = new JMenuItem("Perfeccionar rango");
        menuItemPrefection.setEnabled(false);
        menuItemPrefection.addActionListener((ActionEvent e) -> {
            openSlider();
        });
        menuPresition.add(menuItemPrefection);
        
        navbar.add(menuPresition);

        this.setJMenuBar(navbar);

        btnToggleCamera = new JToggleButton("ACTIVAR CÁMARA WEB");
        btnToggleCamera.addActionListener(this);
        btnToggleCamera.setBounds(10, 10, 200, 30);
        this.add(btnToggleCamera);

        double percentLeft = .7;
        double percentRigth = .3;

        labelImage = new JLabel();
        labelImage.setOpaque(true);
        labelImage.setBackground(Color.LIGHT_GRAY);
        labelImage.setHorizontalAlignment(JLabel.CENTER);
        labelImage.setVerticalAlignment(JLabel.CENTER);

        JScrollPane scrollImage = new JScrollPane(labelImage);
        scrollImage.setBounds(10, 50, (int) (w * percentLeft), h - 115 - 20);
        this.add(scrollImage);

        DefaultTableModel modelLog = new DefaultTableModel();

        JButton btnCleanLog = new JButton("LIMPIAR CONSOLA");
        btnCleanLog.setBounds(scrollImage.getWidth() + 20, 10, 150, 30);
        btnCleanLog.addActionListener((ActionEvent e) -> {
            for (int i = 0; i < modelLog.getRowCount(); i++) {
                modelLog.removeRow(i);
            }
        });
        this.add(btnCleanLog);

        JTable tableLog = new JTable(modelLog);
        JScrollPane scrollLog = new JScrollPane(tableLog);
        scrollLog.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            if (e.getAdjustable().getMaximum() != lastMaximun) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }

            lastMaximun = e.getAdjustable().getMaximum();
        });

        String[] header = {"Descripción"};
        modelLog.setColumnIdentifiers(header);

        scrollLog.setBounds(scrollImage.getWidth() + 20, scrollImage.getY(), (int) (w * percentRigth) - 35, scrollImage.getHeight());
        this.add(scrollLog);

        log = new Log(modelLog);
        this.fisherTraining.setLog(log);
        this.eigenTraining.setLog(log);
    }

    public final void config() {
        this.setLayout(null);
        this.setUndecorated(false);
        this.setTitle("Identificación de rostros con detección de edad, género y emociones.");
        this.setSize(w, h);

        if (dimentions[2] == 0) {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            this.setLocationRelativeTo(null);
            this.setResizable(false);
        }

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void toggleCamera(boolean status) {
        if (status) {
            camera = new Grabber(this, log);
            camera.start();
        } else {
            camera.closeCamera();
            menuItemPrefection.setEnabled(false);
        }

        useCamera = status;
    }

    public void openFile() {
        JFileChooser fileChooser = new JFileChooser("C:\\Users\\Jamie Joao\\Pictures");
        FileNameExtensionFilter filterTypeImage = new FileNameExtensionFilter("JPG, PNG & GIF & BMP", "jpg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filterTypeImage);
        fileChooser.showOpenDialog(this);

        File fileSelected = fileChooser.getSelectedFile();

        if (fileSelected != null) {
            menuItemStartIdenfity.setEnabled(true);
            currentOpenedImage = opencv_imgcodecs.imread(fileSelected.getAbsolutePath());
            Commons.paintImageInLabel(converter.convert(matConverter.convert(currentOpenedImage)), labelImage);
        }
    }

    public void captureFrames() {
        String quantityFrames = JOptionPane.showInputDialog("¿Cuántos fotogramas quieres registrar?");

        if (quantityFrames != null && !quantityFrames.equals("")) {
            int quantity = Integer.parseInt(quantityFrames);
            camera.startCapture(quantity);
        }
    }

    public void applyBasicDetection(String detection) {
        for (int i = 0; i < activeItems.length; i++) {
            activeItems[i] = false;
        }

        switch (detection) {
            case "Rostros":
                activeItems[0] = true;
                log.append("Detectando rostros...");
                break;
            case "Edad":
                activeItems[1] = true;
                log.append("Detectando edades...");
                break;
            case "Emociones":
                activeItems[2] = true;
                log.append("Detectando emociones...");
                break;
            case "Género":
                activeItems[3] = true;
                log.append("Detectando géneros...");
                break;
            case "Identification":
                activeItems[4] = true;
                log.append("identificando...");
                break;
        }

        if (!useCamera) {
            Mat clonedImage = currentOpenedImage.clone();
            paintInformation.paintGeneric(clonedImage, this);

            BufferedImage buffer = converter.convert(matConverter.convert(clonedImage));
            Commons.paintImageInLabel(buffer, labelImage);
        }
    }

    public void saveFrames() {
        String namePerson = JOptionPane.showInputDialog("¿Quién es esta persona?");

        if (namePerson != null && !namePerson.equals("")) {
            ImageWithFaces[] images = this.camera.getFrames();

            for (int i = 0; i < images.length; i++) {
                ImageWithFaces img = images[i];
                String fileName = img.writeFaces(namePerson, i);

                if (fileName != null) {
                    log.append("Guardando rostro, " + fileName);
                }
            }
        }
    }

    public void train() {
        String searchType = selectType();

        if (searchType != null) {
            if (searchType.equals("Fisherface")) {
                fisherTraining.train();
            } else {
                eigenTraining.train();
            }
        }
    }

    public String selectType() {
        String[] options = {"Fisherface", "Eigenface"};
        Object searchType = JOptionPane.showInputDialog(
                null,
                null,
                "Escoja un tipo de entrenamiento",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (searchType != null) {
            return searchType.toString();
        }

        return null;
    }

    public void loadXML() {
        if (currentType != null) {
            if (currentType.equals("Fisherface")) {
                this.fisherTraining.load();
            } else {
                this.eigenTraining.load();
            }
        }
    }

    public void openSlider() {
        JOptionPane optionPane = new JOptionPane();

        slider = new JSlider();
        slider.setMaximum(5000);
        slider.setMajorTickSpacing(10);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider currentSlider = (JSlider) e.getSource();
                updateLimitRecognition(currentSlider.getValue());
            }
        });

        optionPane.setMessage(new Object[]{"Seleccione un valor: ", slider});
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);

        JDialog dialog = optionPane.createDialog(this, "Escojer valor");
        dialog.setVisible(true);
    }

    public void updateLimitRecognition(int limitRecognition) {
        fisherTraining.setLimitRecognition(limitRecognition);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnToggleCamera)) {
            boolean cameraStatus = btnToggleCamera.isSelected();

            toggleCamera(cameraStatus);
            menuItemRegister.setEnabled(cameraStatus);
            menuItemStartIdenfity.setEnabled(cameraStatus);
        }
    }

}
