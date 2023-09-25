package com.tomomoto.graph.gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Matrixes extends JFrame {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private StringBuilder stringBuilder;
    public Matrixes(JFrame parentWindow, StringBuilder stringBuilder) {
        super("Матрицы");
        setSize(560, 350);
        setLayout(null);
        setResizable(true);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                parentWindow.setVisible(true);
            }
        });
        this.stringBuilder = stringBuilder;
        initializeComponent();
    }

    private void initializeComponent() {
        textArea = new JTextArea();
        textArea.setText(stringBuilder.toString());
        textArea.setSize(560, 350);
        textArea.setLocation(0, 0);
        scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setSize(560, 320);

        this.add(scrollPane);
    }
}
