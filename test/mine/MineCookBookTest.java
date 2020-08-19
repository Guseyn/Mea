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
}
