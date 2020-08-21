package j;

import fs.FsCookBook;
import java.io.IOException;
import mine.MineCookBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class JavaCookBookTest {
    @Test
    void newPomDependenciesTest() throws IOException {
        String importsFromJavaFile = FsCookBook.contentOfFile("test/j/resources/imports");
        assertEquals(6, JavaCookBook.importsFromJavaCode(importsFromJavaFile).size());
    }

    @Test
    void parsedWrappedJavaCodeTest() throws IOException {
        String wrappedIncompleteJavaCode = FsCookBook.contentOfFile("test/j/resources/wrapped-incomplete-java-code");
        String justIncompleteJavaCode = FsCookBook.contentOfFile("test/j/resources/incomplete-java-code");
        assertEquals(
            JavaCookBook.parsedJavaCode(wrappedIncompleteJavaCode).getPackageDeclaration().get().getName().asString(),
            JavaCookBook.parsedJavaCode(MineCookBook.wrappedJavaStatements(justIncompleteJavaCode)).getPackageDeclaration().get().getName().asString()
        );
    }
}
