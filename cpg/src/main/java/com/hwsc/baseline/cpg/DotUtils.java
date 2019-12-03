package com.hwsc.baseline.cpg;

import com.hwsc.baseline.cpg.models.DWGEdge;
import com.hwsc.baseline.cpg.models.DWGNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.File;

@Slf4j
public class DotUtils {

    private static final ComponentNameProvider<DWGNode> nodeIdProvider = (node) -> String.valueOf(node.uuid());

    private static final ComponentNameProvider<DWGEdge> edgeIdProvider = (edge) -> String.valueOf(edge.getWeight());

    private static final GraphExporter<DWGNode, DWGEdge> exporter = new DOTExporter<DWGNode, DWGEdge>(nodeIdProvider, null, edgeIdProvider);

    public static void exportGraph(Graph<DWGNode, DWGEdge> graph, File dotFile) {
        try {
            exporter.exportGraph(graph, dotFile);
        } catch (ExportException e) {
            log.error(e.toString());
        }
    }
}
