/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation;

import javax.swing.JOptionPane;

/**
 *
 * @author Jamie Joao
 */
public class Sweet {

    public static void success(String successMessage) {
        JOptionPane.showMessageDialog(null, successMessage, "Exito", JOptionPane.OK_OPTION);
    }

    public static void warning(String warningMessage) {
        JOptionPane.showMessageDialog(null, warningMessage, "Alerta", JOptionPane.DEFAULT_OPTION);
    }

    public static void info(String infoMessage) {
        JOptionPane.showMessageDialog(null, infoMessage, "Exito", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Exito", JOptionPane.ERROR_MESSAGE);
    }
}
