/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation;

import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jamie Joao
 */
public class Log {

    private final DefaultTableModel tableModel;

    public Log(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public void append(String nextMessage) {
        Object[] row = {nextMessage};
        this.tableModel.addRow(row);
    }

}
