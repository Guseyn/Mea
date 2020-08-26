package mine;

import com.github.javaparser.ast.ImportDeclaration;
import com.guseyn.broken_xml.XmlDocument;
import j.JavaCookBook;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    public static String wrappedJavaStatements(String javaStatements) {
        return String.format("package com.wrap;\nclass WrapClass { void wrapMethod() { %s } }", javaStatements);
    }

    public static String wrappedJavaStatements(String javaStatements, List<ImportDeclaration> importDeclarations) {
        return String.format(
            "package wrapper;\n\n%s\nclass WrapClass { void wrapMethod() { %s } }",
            importDeclarations.stream().map(
                importDeclaration -> (importDeclaration.isStatic() ? "import " : "import static ").concat(importDeclaration.getName().asString())
            ).collect(Collectors.joining(";\n")).concat(";\n"), javaStatements
        );
    }

    public static String contentFromLineToLine(String content, int start, int count) {
        String[] lines = content.split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        for (int lineNumber = 0; lineNumber < lines.length; lineNumber++) {
            if (lineNumber > start - 1 && lineNumber < start + count - 1) {
                stringBuilder.append(lines[lineNumber]).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
