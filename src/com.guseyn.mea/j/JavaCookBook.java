package j;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaCookBook {
    public static CompilationUnit parsedJavaCode(String javaCode) {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        // Parse some code
        return StaticJavaParser.parse(javaCode);
    }

    public static List<ImportDeclaration> importsFromJavaCode(String javaCode) {
        return parsedJavaCode(javaCode).getImports();
    }

    public static boolean areTwoImportDeclarationsEqual(ImportDeclaration firstImportDeclaration, ImportDeclaration secondImportDeclaration) {
        return firstImportDeclaration.getName().equals(secondImportDeclaration.getName()) && firstImportDeclaration.isStatic() == secondImportDeclaration.isStatic();
    }

    public static List<JavaStatement> statementsFromPieceOfJavaCodeWithImports(String javaCode) {
        List<JavaStatement> statements = new ArrayList<>();
        List<String> linesOfCode = Arrays.asList(javaCode.split("\n"));
        return null;
    }
}
