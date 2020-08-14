package git;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GitCommandTest {
    private static File localRepo = new File("resources/mea");

    @BeforeAll
    public static void init() throws GitAPIException {
        GitCommands.clone(localRepo, "https://github.com/Guseyn/Mea.git");
    }

    @Test
    public void cloneTest() {
        assertTrue(localRepo.exists());
    }

    @Test
    public void allCommitsInRepoTest() throws GitAPIException, IOException {
        List<RevCommit> commits = GitCommands.allCommitsInRepo(localRepo);
        assertTrue(commits.size() > 0);
    }

    @Test
    public void diffBetweenTwoCommitsTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCommands.allCommitsInRepo(localRepo);
        assertTrue(GitCommands.diffBetweenTwoCommits(localRepo, commits.get(0), commits.get(1)).size() > 0);
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        GitCommands.deleteLocalRepo(localRepo);
    }
}
