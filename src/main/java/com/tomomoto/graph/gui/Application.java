package com.tomomoto.graph.gui;

import com.tomomoto.graph.util.Constants;

import javax.swing.*;
import java.text.NumberFormat;

public class Application extends JFrame {
    private JComboBox<String> matrixComboBox;
    private JButton chooseMatrixButton;
    private JTextField graphSizeTextField;
    private JLabel graphSizeLabel;
    private JButton readButton;

    public Application() {
        super("Application");
        setSize(300, 200);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initializeComponents();
        setLayout(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Application();
    }

    private void initializeComponents() {
        matrixComboBox = new JComboBox<>(Constants.getMATRIX_TYPES());
        matrixComboBox.setSize(200, 30);
        matrixComboBox.setLocation(50, 0);

        chooseMatrixButton = new JButton("Выбрать");
        chooseMatrixButton.setSize(100, 30);
        chooseMatrixButton.setLocation(100, 130);
        chooseMatrixButton.addActionListener(event -> {
            this.setVisible(false);
            new Constructor(this, Integer.parseInt(graphSizeTextField.getText()));
        });

        graphSizeLabel = new JLabel();
        graphSizeLabel.setText("Количество вершин графа:");
        graphSizeLabel.setSize(200, 20);
        graphSizeLabel.setLocation(30, 100);

        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        numberFormat.setMaximumIntegerDigits(2);
        graphSizeTextField = new JFormattedTextField(numberFormat);
        graphSizeTextField.setSize(40, 30);
        graphSizeTextField.setLocation(220, 95);
        graphSizeTextField.setText("3");

        readButton = new JButton("Считать");
        readButton.setSize(100, 30);
        readButton.setLocation(100, 40);
        readButton.addActionListener(event -> {
            this.setVisible(false);
            new Constructor(this, matrixComboBox.getSelectedItem());
        });

        this.add(matrixComboBox);
        this.add(chooseMatrixButton);
        this.add(graphSizeLabel);
        this.add(graphSizeTextField);
        this.add(readButton);
    }
}
