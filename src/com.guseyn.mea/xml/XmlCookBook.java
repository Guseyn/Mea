package xml;

import com.guseyn.broken_xml.Element;
import com.guseyn.broken_xml.XmlDocument;
import java.util.ArrayList;
import java.util.List;

public class XmlCookBook {
    public static List<Element> elementsByName(XmlDocument document, String name) {
        List<Element> allElements = new ArrayList<>();
        document.roots().forEach(root -> {
            if (root.name().equals(name)) {
                allElements.add(root);
            }
            elementsByNamePushedToAllElements(root.children(), name, allElements);
        });
        return allElements;
    }

    private static void elementsByNamePushedToAllElements(List<Element> elements, String name, List<Element> allElements) {
        elements.forEach(element -> {
            if (element.name().equals(name)) {
                allElements.add(element);
            }
            elementsByNamePushedToAllElements(element.children(), name, allElements);
        });
    }
}
