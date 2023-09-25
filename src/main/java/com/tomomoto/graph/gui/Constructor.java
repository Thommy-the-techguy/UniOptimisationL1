package com.tomomoto.graph.gui;

import com.tomomoto.graph.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Constructor extends JFrame {
    private JLabel jLabel;
    private JTable jTable;
    private JScrollPane jScrollPane;
    private JComboBox<String> orientationComboBox;
    private JButton buildButton;
    private JButton saveButton;
    private Object SELECTED_MATRIX_OPTION;
    private int VERTEX_AMOUNT;
    private Map<String, List<String>> vertexList = new LinkedHashMap<>();

    public Constructor(JFrame parentWindow, int VERTEX_AMOUNT) {
        super("Конструктор графов");
        setSize(560, 350);
        setLayout(null);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                parentWindow.setVisible(true);
            }
        });

        this.VERTEX_AMOUNT = VERTEX_AMOUNT;
        initializeComponents();

        setVisible(true);

        randomizeMatrix();
    }

    public Constructor(JFrame parentWindow, Object SELECTED_MATRIX_OPTION) {
        super("Конструктор графов");
        setSize(560, 350);
        setLayout(null);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                parentWindow.setVisible(true);
            }
        });

        this.SELECTED_MATRIX_OPTION = SELECTED_MATRIX_OPTION;
        initializeComponents();
        setVisible(true);

        try {
            Path filePath = Path.of("src", "main", "resources", "matrix.txt");
            readFromFile(SELECTED_MATRIX_OPTION, filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile(Object SELECTED_MATRIX_OPTION, Path filePath) throws IOException {
        if (SELECTED_MATRIX_OPTION.equals("Матрица смежности")) {
            readAdjacencyMatrix(filePath);
        } else if (SELECTED_MATRIX_OPTION.equals("Структура смежности")) {
            readAdjacencyStructure(filePath);
        } else if (SELECTED_MATRIX_OPTION.equals("Матрица инцидентности")) {
            readIncidenceMatrix(filePath);
        }
    }

    public void readAdjacencyMatrix(Path filePath) throws IOException {
        List<String> matrices = List.of(Files.readString(filePath).split("_"));
        List<String> rawAdjacencyMatrix = new ArrayList<>(
                List.of(
                        matrices.get(0)
                                .replace("  ", ",")
                                .replace(" ", ":")
                                .replace(",", " ")
                                .split("\n")
                )
        );
        rawAdjacencyMatrix.remove(rawAdjacencyMatrix.get(0));
        System.out.println(getMap(rawAdjacencyMatrix));

        Map<String, List<String>> adjacencyMatrixMap = getMap(rawAdjacencyMatrix);
        buildTable(adjacencyMatrixMap.get("v1").size());
        for (int i = 0; i < jTable.getRowCount(); i++) {
            List<String> values = adjacencyMatrixMap.get(String.format("v%d", i + 1));
            for (int j = 1; j < jTable.getColumnCount(); j++) {
                jTable.setValueAt(values.get(j - 1), i, j);
            }
        }

    }

    public void readAdjacencyStructure(Path filePath) throws IOException {
        List<String> matrices = List.of(Files.readString(filePath).split("_"));
        List<String> rawAdjacencyStructure = new ArrayList<>(List.of(matrices.get(1).split("\n")));
        rawAdjacencyStructure.remove("");
        System.out.println(getMap(rawAdjacencyStructure));

        Map<String, List<String>> adjacencyStructureMap = getMap(rawAdjacencyStructure);
        buildTable(adjacencyStructureMap.size());
        for (int i = 0; i < jTable.getRowCount(); i++) {
            List<String> values = adjacencyStructureMap.get(String.format("v%d", i + 1));
            for (int j = 1; j < jTable.getColumnCount(); j++) {
                if (values.contains(String.format("v%d", j))) {
                    jTable.setValueAt(String.valueOf(1), i, j);
                } else {
                    jTable.setValueAt(String.valueOf(0), i, j);
                }
            }
        }
    }

    public void readIncidenceMatrix(Path filePath) throws IOException {
        List<String> matrices = List.of(Files.readString(filePath).split("_"));
        List<String> rawIncidenceMatrix = new ArrayList<>(List.of(matrices.get(2).split("\n")));
        rawIncidenceMatrix.remove("");
        System.out.println(getMap(rawIncidenceMatrix));

        Map<String, List<String>> incidenceMatrixMap = getMap(rawIncidenceMatrix);
        Map<String, List<String>> convertedToVertexMap = convertEdgesToVertexMap(incidenceMatrixMap);
        System.out.println(convertedToVertexMap);
        int matrixSize = getMaxVertexNumber(incidenceMatrixMap);
        buildTable(matrixSize);
        addRowsIfAbsent(convertedToVertexMap, matrixSize);
        System.out.println(convertedToVertexMap);
        for (int i = 0, k = 0; i < jTable.getRowCount(); i++) {
            String key = (String) convertedToVertexMap
                    .keySet()
                    .stream()
                    .sorted(
                            Comparator.comparingInt(firstVertex -> Integer.parseInt(firstVertex.replace("v", "")))
                    ).toArray()[i];
            List<String> values = convertedToVertexMap.get(key);
            for (int j = 1; j < jTable.getColumnCount(); j++) {
                if (!jTable.getValueAt(i, 0).equals(jTable.getColumnModel().getColumn(j).getHeaderValue())
                        && String.format("v%d", i + 1).equals(key)
                        && values != null
                        && values.contains(String.format("v%d", j))) {
                    jTable.setValueAt(String.valueOf(1), i, j);
                } else {
                    jTable.setValueAt(String.valueOf(0), i, j);
                }
            }
        }
    }

    private void addRowsIfAbsent(Map<String, List<String>> convertedToVertexMap, int matrixSize) {
        int convertedMapSize = convertedToVertexMap.size();
        for (int i = 0; i < convertedMapSize; i++) {
            int currentVertexNumber = Integer.parseInt(
                    convertedToVertexMap.keySet().toArray()[i].toString().replace("v", "")
            );
            for (int j = 0; j < matrixSize; j++) {
                if (currentVertexNumber - (j + 1) > 0) {
                    convertedToVertexMap.putIfAbsent(String.format("v%d", j + 1), new ArrayList<>());

                }
            }
        }

        for (int i = convertedMapSize; i < matrixSize; i++) {
            convertedToVertexMap.putIfAbsent(String.format("v%d", i + 1), new ArrayList<>());
        }
    }

    private Map<String, List<String>> convertEdgesToVertexMap(Map<String, List<String>> incidenceMatrix) {
        Map<String, List<String>> edgesToVertexMap = new LinkedHashMap<>();
        for (int i = 0; i < incidenceMatrix.size(); i++) {
            List<String> vertexes = incidenceMatrix.get(String.format("e%d", i + 1));
            String keyVertex = vertexes.get(0);
            List<String> vertexesToAdd = new ArrayList<>();
            for (int j = 0; j < incidenceMatrix.size(); j++) {
                List<String> vertexesToCheck = incidenceMatrix.get(String.format("e%d", j + 1));
                if (keyVertex.equals(vertexesToCheck.get(0))) {
                    vertexesToAdd.add(vertexesToCheck.get(1));
                }
            }
            edgesToVertexMap.put(keyVertex, vertexesToAdd);
        }
        return edgesToVertexMap;
    }

    private int getMaxVertexNumber(Map<String, List<String>> incidenceMatrixMap) {
        AtomicInteger maxVertexAmount = new AtomicInteger(0);
        incidenceMatrixMap.forEach((key, value) -> {
            value.forEach(vertex -> {
                int vertexNumber = Integer.parseInt(vertex.replace("v", ""));
                maxVertexAmount.set(Math.max(vertexNumber, maxVertexAmount.get()));
            });
        });
        return maxVertexAmount.get();
    }

    private Map<String, List<String>> getMap(List<String> matrix) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        matrix.forEach(str -> {
            List<String> keyAndValues = List.of(str.split(":"));
            List<String> values = keyAndValues.size() > 1 ? List.of(keyAndValues.get(1).trim().split(" ")) : new ArrayList<>();
            result.put(keyAndValues.get(0), values);
        });
        return result;
    }

    private void initializeComponents() {
        // Selected matrix type
        jLabel = new JLabel();
        jLabel.setText((String) SELECTED_MATRIX_OPTION);
        jLabel.setSize(150, 40);
        jLabel.setLocation(10, 0);

        //
        if (SELECTED_MATRIX_OPTION == null) {
            buildTable(VERTEX_AMOUNT);
        }

        // orientation combobox
        orientationComboBox = new JComboBox<>(Constants.getGRAPH_ORIENTATIONS());
        orientationComboBox.setSize(200, 30);
        orientationComboBox.setLocation(360, 36);

        // build button
        buildButton = new JButton("Построить");
        buildButton.setSize(100, 30);
        buildButton.setLocation(230, 290);
        buildButton.addActionListener(event -> {
            this.setVisible(false);
            new Graph(
                    this,
                    getVertexes(),
                    Objects.equals((String) orientationComboBox.getSelectedItem(), Constants.getGRAPH_ORIENTATIONS()[1])
            );
        });

        saveButton = new JButton("Сохранить");
        saveButton.setSize(100, 30);
        saveButton.setLocation(460, 290);
        saveButton.addActionListener(event -> {
            setVisible(false);
            new Matrixes(this, writeToFile());
        });

        this.add(saveButton);
        this.add(jLabel);
        this.add(orientationComboBox);
        this.add(buildButton);
    }

    private void buildTable(int numberOfVertexes) {
        // DataGrid configuration
        Vector<String> vector = new Vector<>();
        vector.add("");
        for (int i = 0; i < numberOfVertexes; i++) {
            vector.add(String.format("%s", i + 1));
        }
        TableModel tableModel = new DefaultTableModel(vector, numberOfVertexes);
        jTable = new JTable(tableModel);
        jTable.setGridColor(new Color(0, 0, 0));
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
        jScrollPane = new JScrollPane(jTable);
        jScrollPane.setSize(350, 200);
        jScrollPane.setLocation(10, 40);
        ((DefaultTableCellRenderer) jTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int j = 0; j < numberOfVertexes; j++) {
            jTable.setValueAt(String.format("%d", j + 1), j, 0);
        }
        this.add(jScrollPane);
    }

    public Map<String, List<String>> getVertexes() {
        List<String> edges;
        for (int i = 0; i < jTable.getRowCount(); i++) {
            edges = new ArrayList<>();
            for (int j = 1; j < jTable.getColumnCount(); j++) {
                if (Integer.parseInt(String.valueOf(jTable.getValueAt(i, j))) == 1) {
                    edges.add(String.format("v%d", j));
                }
            }
            vertexList.put(String.format("v%d", i + 1), edges);
        }
        removeDuplicates();
        return vertexList;
    }

    private void randomizeMatrix() {
        for (int i = 0; i < jTable.getRowCount(); i++) {
            for (int j = 1; j < jTable.getColumnCount(); j++) {
                if (!jTable.getValueAt(i, 0).equals(jTable.getColumnModel().getColumn(j).getHeaderValue())) {
                    jTable.setValueAt(String.valueOf((int) ((Math.random() * 2))), i, j);
                } else {
                    jTable.setValueAt(0, i, j);
                }
            }
        }
    }

    private void removeDuplicates() {
        if (Objects.equals(orientationComboBox.getSelectedItem(), Constants.getGRAPH_ORIENTATIONS()[1])) {
            for (int i = 0; i < jTable.getRowCount(); i++) {
                for (int j = 1; j < jTable.getColumnCount(); j++) {
                    if (vertexList.get(String.format("v%d", i + 1)).contains(String.format("v%d", j))) {
                        vertexList.get(String.format("v%d", j)).remove(String.format("v%d", i + 1));
                    }
                }
            }
        }
    }

    private StringBuilder writeToFile() {
        Path filePath = Path.of("src", "main", "resources", "matrix.txt");
        StringBuilder stringBuilder = new StringBuilder("   ");
        writeAdjacencyMatrix(stringBuilder);
        writeAdjacencyStructure(stringBuilder);
        writeIncidenceMatrix(stringBuilder);

        try {
            Files.writeString(filePath,
                    stringBuilder.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringBuilder;
    }

    private void writeAdjacencyMatrix(StringBuilder stringBuilder) {
        getVertexes().keySet().forEach(key -> stringBuilder.append(key).append(" "));
        for (int i = 0; i < jTable.getRowCount(); i++) {
            stringBuilder.append("\n").append(String.format("v%d", i + 1)).append(" ");
            for (int j = 1; j < jTable.getColumnCount(); j++) {
                stringBuilder.append(jTable.getValueAt(i, j)).append("  ");
            }
        }
    }

    private void writeAdjacencyStructure(StringBuilder stringBuilder) {
        // structure
        stringBuilder.append("\n_\n");
        getVertexes().forEach((key, value) -> {
            stringBuilder.append(key).append(":");
            value.forEach(item -> stringBuilder.append(item).append(" "));
            stringBuilder.append("\n");
        });
    }

    private void writeIncidenceMatrix(StringBuilder stringBuilder) {
        stringBuilder.append("_\n");
        Map<String, List<String>> matrix = new LinkedHashMap<>();
        AtomicInteger edge = new AtomicInteger(1);
        getVertexes().forEach((key, value) -> {
            value.forEach(item -> {
                if (!getVertexes().get(item).contains(key)) {
                    matrix.put(String.format("e%d", edge.getAndIncrement()), List.of(key, item));
                } else {
                    getVertexes().get(item).remove(key);
                    matrix.put(String.format("e%d", edge.getAndIncrement()), List.of(key, item));
                }
            });
        });
        removeDuplicates(matrix);
        stringBuilder.append(formatIncidenceMatrix(matrix));
    }

    private String formatIncidenceMatrix(Map<String, List<String>> matrix) {
        StringBuilder result = new StringBuilder();
        matrix.forEach((key, value) -> {
            result.append(key).append(":");
            value.forEach(item -> result.append(item).append(" "));
            result.append("\n");
        });
        return result.toString();
    }

    private void removeDuplicates(Map<String, List<String>> matrix) {
        for (int i = 0; i < matrix.size(); i++) {
            String key = String.format("e%d", i + 1);
            List<String> listToCompare = matrix.get(key);
            for (int j = 0; j < matrix.size(); j++) {
                String compareToListKey = String.format("e%d", j + 1);
                if (!key.equals(compareToListKey)) {
                    List<String> listToCompareTo = matrix.get(compareToListKey);
                    if (listToCompareTo != null
                            && listToCompare != null
                            && new HashSet<>(listToCompareTo).containsAll(listToCompare)) {
                        matrix.remove(compareToListKey);
                    }
                }
            }
        }
        fixEdgeNumeration(matrix);
    }

    private void fixEdgeNumeration(Map<String, List<String>> matrix) {
        int numberOfEdges = matrix.size();
        List<List<String>> mapValues = new ArrayList<>();
        matrix.forEach((key, value) -> mapValues.add(value));
        matrix.clear();
        for (int i = 0; i < numberOfEdges; i++) {
            matrix.put(String.format("e%d", i + 1), mapValues.get(i));
        }
    }
}
