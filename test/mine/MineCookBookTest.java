package mine;

import com.guseyn.broken_xml.ParsedXML;
import fs.FsCookBook;
import git.GitCookBook;
import j.JavaCookBook;
import java.io.IOException;
import java.util.List;
import javafx.util.Pair;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

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

    @Test
    void spoonedDiffForJavaCodeBeforeAndAfterMigration() throws IOException, GitAPIException {
        String fromLibCode = FsCookBook.contentOfFile("test/mine/resources/from-lib");
        String toLibCode = FsCookBook.contentOfFile("test/mine/resources/to-lib");
        GitCookBook.spoonedDiffForCodeBeforeAndAfterInCommit(
            new Pair<>(
                Launcher.parseClass(fromLibCode),
                Launcher.parseClass(toLibCode)
            )
        );
    }
}
