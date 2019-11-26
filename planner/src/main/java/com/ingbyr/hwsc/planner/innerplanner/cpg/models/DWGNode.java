package com.ingbyr.hwsc.planner.innerplanner.cpg.models;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.models.Concept;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Directed weighted graph node
 *
 * @author ingbyr
 */
@Builder
@EqualsAndHashCode
public class DWGNode {
    @Getter
    public Set<LeveledService> services;

    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    @Builder.Default
    double distance = -1;

    @Getter
    @EqualsAndHashCode.Exclude
    public Set<Concept> inputConcepts;

    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    public Set<Concept> aStarConcepts;

    @Getter
    @EqualsAndHashCode.Exclude
    public Set<Concept> outputConcepts;

    @EqualsAndHashCode.Exclude
    private int uuid;

    private static int globalID = 0;

    public static DWGNode from(Set<LeveledService> leveledServices) {
        Set<Concept> inputConceptSet = Sets.newHashSet();
        Set<Concept> outputConceptSet = Sets.newHashSet();
        Set<LeveledService> services = Sets.newHashSet(leveledServices);

        for (LeveledService leveledService : leveledServices) {
            inputConceptSet.addAll(leveledService.getInputConceptSet());
            outputConceptSet.addAll(leveledService.getOutputConceptSet());
        }

        return DWGNode.builder()
                .services(services)
                .inputConcepts(inputConceptSet)
                .outputConcepts(outputConceptSet)
                .uuid(globalID++)
                .build();
    }

    public static DWGNode from(DWGNode node) {
        return DWGNode.builder()
                .services(Sets.newHashSet(node.services))
                .inputConcepts(node.inputConcepts)
                .outputConcepts(node.outputConcepts)
                .uuid(globalID++)
                .build();
    }

    @Override
    public String toString() {
        return services.toString();
    }

    public int uuid() {
        return uuid;
    }
}