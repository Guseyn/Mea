package j;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.util.List;

public class JavaCookBook {
    public static List<ImportDeclaration> importsFromJavaCode(String javaCode) {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        // Parse some code
        CompilationUnit cu = StaticJavaParser.parse(javaCode);
        return cu.getImports();
    }

    public static boolean areTwoImportDeclarationsEqual(ImportDeclaration firstImportDeclaration, ImportDeclaration secondImportdeclaration) {
        return firstImportDeclaration.getName().equals(secondImportdeclaration.getName()) && firstImportDeclaration.isStatic() == secondImportdeclaration.isStatic();
    }
}
