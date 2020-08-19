package pom;

import com.guseyn.broken_xml.Element;
import com.guseyn.broken_xml.Text;
import com.guseyn.broken_xml.XmlDocument;
import java.util.List;
import java.util.stream.Collectors;
import xml.XmlCookBook;

public class PomCookBook {
    static List<PomDependency> dependencies(XmlDocument pomXml) {
        return XmlCookBook.elementsByName(pomXml, "dependency").stream().map(
            PomCookBook::dependencyByCorrespondingXmlElement
        ).collect(Collectors.toList());
    }

    private static PomDependency dependencyByCorrespondingXmlElement(Element element) {
        String groupId = null;
        String artifactId = null;
        String version = null;
        for (int childIndex = 0; childIndex < element.children().size(); childIndex++) {
            Element currentChild = element.children().get(childIndex);
            String currentChildValue = currentChild.texts().stream().map(Text::value).collect(
                Collectors.joining("")).trim();
            if (currentChild.name().trim().equals("groupId")) {
                groupId = currentChildValue;
            }
            if (currentChild.name().trim().equals("artifactId")) {
                artifactId = currentChildValue;
            }
            if (currentChild.name().trim().equals("version")) {
                version = currentChildValue;
            }
        }
        return new PomDependency(groupId, artifactId, version);
    }
}
