package mine;

import com.guseyn.broken_xml.ParsedXML;
import fs.FsCookBook;
import j.JavaCookBook;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MineCookBookTest {
    @Test
    void newPomDependenciesTest() throws IOException {
        String previousPomContent = FsCookBook.contentOfFile("test/mine/resources/previous-pom.xml");
        String currentPomContent = FsCookBook.contentOfFile("test/mine/resources/current-pom.xml");
        assertEquals(4, MineCookBook.newPomDependencies(
            new ParsedXML(previousPomContent).document(),
            new ParsedXML(currentPomContent).document()
        ).size());
    }

    @Test
    void newImportsInJavaCodeTest() throws IOException {
        String previousJavaCode = FsCookBook.contentOfFile("test/mine/resources/previous-imports");
        String currentJavaCode = FsCookBook.contentOfFile("test/mine/resources/current-imports");
        assertEquals(
            5,
            MineCookBook.newImportsInJavaCode(
                JavaCookBook.importsFromJavaCode(previousJavaCode),
                JavaCookBook.importsFromJavaCode(currentJavaCode)
            ).size()
        );
    }

    @Test
    void parsedWrappedJavaCodeTest() throws IOException {
        String wrappedIncompleteJavaCode = FsCookBook.contentOfFile("test/mine/resources/wrapped-incomplete-java-code");
        String justIncompleteJavaCode = FsCookBook.contentOfFile("test/mine/resources/incomplete-java-code");
        assertEquals(
            JavaCookBook.parsedJavaCode(wrappedIncompleteJavaCode).getPackageDeclaration().get().getName().asString(),
            JavaCookBook.parsedJavaCode(MineCookBook.wrappedJavaStatements(justIncompleteJavaCode)).getPackageDeclaration().get().getName().asString()
        );
    }

    @Test
    void parsedWrappedJavaCodeWithAddedImportsTest() throws IOException {
        String currentJavaCode = FsCookBook.contentOfFile("test/mine/resources/current-imports");
        String justIncompleteJavaCode = FsCookBook.contentOfFile("test/mine/resources/incomplete-java-code");
        assertEquals(
            6, JavaCookBook.parsedJavaCode(
                MineCookBook.wrappedJavaStatements(
                    justIncompleteJavaCode,
                    JavaCookBook.importsFromJavaCode(currentJavaCode)
                )
            ).getImports().size()
        );
    }
}
