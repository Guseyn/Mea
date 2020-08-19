package mine;

import com.guseyn.broken_xml.ParsedXML;
import fs.FsCookBook;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MineCookBookTest {
    @Test
    void newPomDependenciesTest() throws IOException {
        String previousPomContent = FsCookBook.contentOfFile("test/mine/resources/previous.xml");
        String currentPomContent = FsCookBook.contentOfFile("test/mine/resources/current.xml");
        assertEquals(4, MineCookBook.newPomDependencies(
            new ParsedXML(previousPomContent).document(),
            new ParsedXML(currentPomContent).document()
        ).size());
    }
}
