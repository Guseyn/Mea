package mine;

import com.github.javaparser.ast.ImportDeclaration;
import com.guseyn.broken_xml.XmlDocument;
import j.JavaCookBook;
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

    public static List<ImportDeclaration> newImportsInJavaCode(List<ImportDeclaration> previousImportsInJavaCode, List<ImportDeclaration> currentImportsInJavaCode) {
        List<ImportDeclaration> newImportsInJavaCode = new ArrayList<>();
        for (ImportDeclaration currentImportInJavaCode : currentImportsInJavaCode) {
            boolean importIsAlsoInPreviousJavCode = false;
            for (ImportDeclaration previousImportInJavaCode : previousImportsInJavaCode) {
                if (JavaCookBook.areTwoImportDeclarationsEqual(previousImportInJavaCode, currentImportInJavaCode)) {
                    importIsAlsoInPreviousJavCode = true;
                    break;
                }
            }
            if (!importIsAlsoInPreviousJavCode) {
                newImportsInJavaCode.add(currentImportInJavaCode);
            }
        }
        return newImportsInJavaCode;
    }
}