package com.ingbyr.hwsc.dataset.reader;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import org.apache.commons.collections4.SetUtils;
import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XMLDataSetReaderTest {

    private XMLDataSetReader reader = new XMLDataSetReader(Dataset.wsc2009_01);

    @Test
    void parseServicesDocument() throws DocumentException {
        reader.parseTaxonomyDocument();
        reader.parseServicesDocument();
        assertEquals(572, reader.getServiceMap().size());

        Concept con144 = reader.getConceptMap().get("con1448945150");
        // Used service
        Set<Service> usedByServices = con144.getUsedByServices();
        for (Service usedByService : usedByServices) {
            assertTrue(usedByService.getInputConceptSet().contains(con144));
        }

        Set<Service> usedByServicesCopy = new HashSet<>();
        reader.getServiceMap().forEach((name, service) -> {
            if (service.getInputConceptSet().contains(con144))
                usedByServicesCopy.add(service);
        });

        assertTrue(SetUtils.isEqualSet(usedByServices, usedByServicesCopy));

        // Produced service
        Set<Service> producedByServices = con144.getProducedByServices();
        for (Service producedByService : producedByServices) {
            assertTrue(producedByService.getOutputConceptSet().contains(con144));
        }

        Set<Service> producedByServicesCopy = new HashSet<>();
        reader.getServiceMap().forEach((name, service) -> {
            if (service.getOutputConceptSet().contains(con144))
                producedByServicesCopy.add(service);
        });

        assertTrue(SetUtils.isEqualSet(producedByServices, producedByServicesCopy));
    }

    @Test
    void parseProblemDocument() throws DocumentException {
        reader.parseTaxonomyDocument();
        reader.parseServicesDocument();
        reader.parseProblemDocument();
        assertEquals(38, reader.getInputSet().size());
        assertEquals(4, reader.getGoalSet().size());
    }

    @Test
    void process() {
        reader.process();
    }

    @Test
    void loadAllDataset() {
        for (Dataset dataset : Dataset.values()) {
            new XMLDataSetReader(dataset);
            System.out.println();
        }
    }
}