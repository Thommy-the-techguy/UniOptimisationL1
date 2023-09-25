package com.tomomoto.graph.util;

import lombok.Getter;

public final class Constants {
    @Getter
    private static final String[] MATRIX_TYPES = new String[] {
            "Матрица смежности",
            "Структура смежности",
            "Матрица инцидентности",
            "Матрица весов"
    };

    @Getter
    private static final String[] GRAPH_ORIENTATIONS = new String[] {
            "Неориентированный",
            "Ориентированный",
    };

    private Constants() {

    }
}
