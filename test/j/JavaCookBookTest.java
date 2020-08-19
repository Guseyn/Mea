package j;

import fs.FsCookBook;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class JavaCookBookTest {
    @Test
    void newPomDependenciesTest() throws IOException {
        String importsFromJavaFile = FsCookBook.contentOfFile("test/j/resources/imports");
        assertEquals(6, JavaCookBook.importsFromJavaCode(importsFromJavaFile).size());
    }
}
