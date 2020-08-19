package git;

import com.guseyn.broken_xml.XmlDocument;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.util.Pair;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GitCookBookTest {
    private static File localRepo = new File("resources/broken-xml");

    @BeforeAll
    static void init() throws GitAPIException {
        GitCookBook.clone(localRepo, "https://github.com/Guseyn/broken-xml.git");
    }

    @Test
    void cloneTest() {
        assertTrue(localRepo.exists());
    }

    @Test
    void allCommitsInRepoTest() throws GitAPIException, IOException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        assertTrue(commits.size() > 0);
    }

    @Test
    void changedFilesInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        assertTrue(GitCookBook.changedFilesInCommit(localRepo, commits.get(1)).size() > 0);
    }

    @Test
    void changedContentOfFilesInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        assertTrue(GitCookBook.changedContentOfJavaAndXmlFilesInCommit(localRepo, commits.get(1)).size() > 0);
    }

    @Test
    void pomContentBeforeAndNowInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        String pathOfChangedPomFile = GitCookBook.changedFilesInCommit(localRepo, commits.get(1)).get(0).getNewPath();
        Pair<String, String> pomContentBeforeAndNow = GitCookBook.pomContentBeforeAndNowInCommit(
            localRepo,
            commits.get(1),
            pathOfChangedPomFile
        );
        assertNotNull(pomContentBeforeAndNow.getKey());
        assertNotNull(pomContentBeforeAndNow.getValue());
    }

    @Test
    void pomContentAsParsedXmlDocumentBeforeAndNowInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        String pathOfChangedPomFile = GitCookBook.changedFilesInCommit(localRepo, commits.get(1)).get(0).getNewPath();
        Pair<XmlDocument, XmlDocument> pomContentBeforeAndNow = GitCookBook.pomContentAsParsedXmlDocumentBeforeAndNowInCommit(
            localRepo,
            commits.get(1),
            pathOfChangedPomFile
        );
        assertTrue(pomContentBeforeAndNow.getKey().roots().size() > 0);
        assertTrue(pomContentBeforeAndNow.getValue().roots().size() > 0);
    }

    @AfterAll
     static void cleanUp() throws IOException {
        GitCookBook.deleteLocalRepo(localRepo);
    }
}
