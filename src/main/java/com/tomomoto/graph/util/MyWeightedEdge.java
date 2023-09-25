package com.tomomoto.graph.util;

import org.jgrapht.graph.DefaultWeightedEdge;

public class MyWeightedEdge extends DefaultWeightedEdge {
    @Override
    public String toString() {
        return Integer.toString((int) getWeight());
    }
}
