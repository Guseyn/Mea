package mine;

import com.guseyn.broken_xml.XmlDocument;
import java.util.ArrayList;
import java.util.List;
import pom.PomCookBook;
import pom.PomDependency;

public class MineCookBook {
    public static List<PomDependency> newPomDependencies(XmlDocument previousPom, XmlDocument currentPom) {
        List<PomDependency> newPomDependencies = new ArrayList<>();
        List<PomDependency> previousPomDependencies = PomCookBook.dependencies(previousPom);
        List<PomDependency> currentPomDependencies = PomCookBook.dependencies(currentPom);
        for (PomDependency currentPomDependency : currentPomDependencies) {
            boolean dependencyIsAlsoInPreviousPom = false;
            for (PomDependency previousPomDependency : previousPomDependencies) {
                if (PomCookBook.areTwoPomDependenciesEqual(
                    previousPomDependency, currentPomDependency)) {
                    dependencyIsAlsoInPreviousPom = true;
                    break;
                }
            }
            if (!dependencyIsAlsoInPreviousPom) {
                newPomDependencies.add(currentPomDependency);
            }
        }
        return newPomDependencies;
    }
}
