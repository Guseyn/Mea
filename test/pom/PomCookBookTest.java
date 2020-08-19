package pom;

import com.guseyn.broken_xml.ParsedXML;
import fs.FsCookBook;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PomCookBookTest {
    @Test
    void pomDependenciesTest() throws IOException {
        String pomContent = FsCookBook.contentOfFile("test/pom/resources/test.xml");
        assertEquals(7, PomCookBook.dependencies(new ParsedXML(pomContent).document()).size());
    }
}
