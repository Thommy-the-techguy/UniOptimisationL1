package com.tomomoto.graph.gui;

import javax.swing.*;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.tomomoto.graph.util.MyWeightedEdge;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
import com.mxgraph.layout.mxCircleLayout;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Graph extends JFrame {
    private JGraphXAdapter<String, ?> jgxAdapter;
    private final Map<String, List<String>> vertexMap;
    private final boolean isOriented;

    public Graph(JFrame parentWindow, Map<String, List<String>> vertexMap, boolean isOriented) {
        super("Graph");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                parentWindow.setVisible(true);
            }
        });
        setVisible(true);

        // vertexes
        this.vertexMap = vertexMap;
        this.isOriented = isOriented;

        System.out.println(vertexMap);
        buildGraph();
    }

    private void buildGraph() {
        if (!isOriented) {
            buildUndirectedGraph();
            return;
        }
        buildOrientedGraph();
    }

    private void buildUndirectedGraph() {
        DefaultUndirectedGraph<String, DefaultEdge> graph =
                new DefaultUndirectedGraph<>(DefaultEdge.class);

        vertexMap.forEach((key, value) -> graph.addVertex(key));

        vertexMap.forEach((sourceVertex, value) -> value.forEach(targetVertex -> {
            graph.addEdge(sourceVertex, targetVertex);
        }));

        jgxAdapter = new JGraphXAdapter<>(graph);
        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        mxGraphModel graphModel = (mxGraphModel) graphComponent.getGraph().getModel();
        Collection<Object> cells = graphModel.getCells().values();
        mxUtils.setCellStyles(graphComponent.getGraph().getModel(),
                cells.toArray(), mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        getContentPane().add(graphComponent);

        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());
    }

    private void buildOrientedGraph() {
        SimpleDirectedWeightedGraph<String, MyWeightedEdge> graph =
                new SimpleDirectedWeightedGraph<>(MyWeightedEdge.class);

        vertexMap.forEach((key, value) -> graph.addVertex(key));

        vertexMap.forEach((sourceVertex, value) -> value.forEach(targetVertex -> {
            graph.addEdge(sourceVertex, targetVertex);
            graph.setEdgeWeight(graph.getEdge(sourceVertex, targetVertex), Math.round(Math.random() * 15) + 1);
            System.out.println(graph.getEdgeWeight(graph.getEdge(sourceVertex, targetVertex)));
        }));

        jgxAdapter = new JGraphXAdapter<>(graph);
        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        mxGraphModel graphModel = (mxGraphModel) graphComponent.getGraph().getModel();
        Collection<Object> cells = graphModel.getCells().values();
        mxUtils.setCellStyles(graphComponent.getGraph().getModel(),
                cells.toArray(), mxConstants.STYLE_DIRECTION, mxConstants.NONE);
        getContentPane().add(graphComponent);

        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());
    }
}
