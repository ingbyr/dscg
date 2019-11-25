package com.ingbyr.hwsc.dataset.util;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Iterator;
import java.util.function.Consumer;

@Slf4j
public class XMLFileUtils {

    public static Element loadRootElement(String xmlFilePath) throws DocumentException {
        File xmlFile = new File(xmlFilePath);
        log.debug("Load xml file {}", xmlFile.getAbsolutePath());
        return loadRootElement(xmlFile);
    }

    public static Element loadRootElement(File xmlFile) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(xmlFile);
        return document.getRootElement();
    }

    public static void walkOnChild(Element parentElement, Consumer<Element> consumer) {
        for (Iterator itr = parentElement.elementIterator(); itr.hasNext(); ) {
            Element element = (Element) itr.next();
            consumer.accept(element);
        }
    }
}
