package com.ingbyr.hwsc.reader;

import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WSDLDataSetReaderTest {

    private WSDLDataSetReader reader = new WSDLDataSetReader("2009", "01");

    @Test
    void parseTaxonomyDocument() throws DocumentException {
        reader.parseTaxonomyDocument();
        assertEquals(1578, reader.getConceptMap().size());
    }

    @Test
    void parseServicesDocument() throws DocumentException {
        reader.parseTaxonomyDocument();
        reader.parseServicesDocument();
        assertEquals(572, reader.getServiceMap().size());
    }

    @Test
    void parseChallengeDocument() throws DocumentException {
        reader.parseTaxonomyDocument();
        reader.parseServicesDocument();
        reader.parseProblemDocument();
        assertEquals(38, reader.getProblem().getLeft().size());
        assertEquals(4, reader.getProblem().getRight().size());
    }
}