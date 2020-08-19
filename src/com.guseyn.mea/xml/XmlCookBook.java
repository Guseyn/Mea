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
            root.children().forEach(element -> {
                elementsByNamePushedToAllElements(element, name, allElements);
            });
        });
        return allElements;
    }

    private static void elementsByNamePushedToAllElements(Element element, String name, List<Element> allElements) {
        element.children().forEach(child -> {
            if (child.name().equals(name)) {
                allElements.add(child);
            }
            elementsByNamePushedToAllElements(child, name, allElements);
        });
    }
}
