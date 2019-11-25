package com.ingbyr.hwsc.dataset.reader;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Iterator;
import java.util.function.Consumer;

public class XMLFileUtils {

    public static Element loadRootElement(String xmlFileUrl) throws DocumentException {
        return loadRootElement(FileUtils.getFile(xmlFileUrl));
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
