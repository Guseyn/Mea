package git;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.google.common.collect.Lists;
import com.guseyn.broken_xml.XmlDocument;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.util.Pair;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtClass;

class GitCookBookTest {
    private static File localRepo = new File("tpm-resources/broken-xml");

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
        assertTrue(GitCookBook.changedContentOfFilesInCommit(localRepo, commits.get(1),
            Lists.newArrayList("java", "xml")
        ).size() > 0);
    }

    @Test
    void changedLinesOfCodeOfJavaFilesInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        assertTrue(GitCookBook.changedLinesOfCodeOfJavaFilesInCommit(localRepo, commits.get(2)).size() > 0);
    }

    @Test
    void pomContentBeforeAndNowInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        DiffEntry changedPomFile = GitCookBook.changedFilesInCommit(localRepo, commits.get(1)).get(0);
        Pair<String, String> pomContentBeforeAndNow = GitCookBook.fileContentBeforeAndNowInCommit(
            localRepo,
            commits.get(1),
            changedPomFile.getOldPath(),
            changedPomFile.getNewPath()
        );
        assertNotNull(pomContentBeforeAndNow.getKey());
        assertNotNull(pomContentBeforeAndNow.getValue());
    }

    @Test
    void javaContentBeforeAndNowInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        RevCommit commit = commits.get(2);
        DiffEntry changedPomFile = GitCookBook.changedFilesInCommit(localRepo, commit).get(0);
        Pair<String, String> javaContentBeforeAndNow = GitCookBook.fileContentBeforeAndNowInCommit(
            localRepo,
            commit,
            changedPomFile.getOldPath(),
            changedPomFile.getNewPath()
        );
        assertNotNull(javaContentBeforeAndNow.getKey());
        assertNotNull(javaContentBeforeAndNow.getValue());
    }

    @Test
    void parsedXmlDocumentBeforeAndNowInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        DiffEntry changedPomFile = GitCookBook.changedFilesInCommit(localRepo, commits.get(1)).get(0);
        Pair<XmlDocument, XmlDocument> parsedXmlDocumentBeforeAndNowInCommit = GitCookBook.parsedXmlDocumentBeforeAndNowInCommit(
            localRepo,
            commits.get(1),
            changedPomFile.getOldPath(),
            changedPomFile.getNewPath()
        );
        assertTrue(parsedXmlDocumentBeforeAndNowInCommit.getKey().roots().size() > 0);
        assertTrue(parsedXmlDocumentBeforeAndNowInCommit.getValue().roots().size() > 0);
    }

    @Test
    void parsedJavaCodeBeforeAndNowInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        RevCommit commit = commits.get(5);
        DiffEntry changedJavaFile = GitCookBook.changedFilesInCommit(localRepo, commit).get(1);
        Pair<CompilationUnit, CompilationUnit> parsedJavaCodeBeforeAndNowInCommit = GitCookBook.parsedJavaCodeBeforeAndNowInCommit(
            localRepo,
            commit,
            changedJavaFile.getOldPath(),
            changedJavaFile.getNewPath()
        );
        assertTrue(parsedJavaCodeBeforeAndNowInCommit.getKey().getChildNodes().get(4) instanceof ClassOrInterfaceDeclaration);
        assertTrue(parsedJavaCodeBeforeAndNowInCommit.getValue().getChildNodes().get(4) instanceof ClassOrInterfaceDeclaration);
    }

    @Test
    void spoonedJavaCodeBeforeAndNowInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        RevCommit commit = commits.get(5);
        DiffEntry changedJavaFile = GitCookBook.changedFilesInCommit(localRepo, commit).get(1);
        Pair<CtClass, CtClass> parsedJavaCodeBeforeAndNowInCommit = GitCookBook.spoonedJavaCodeBeforeAndNowInCommit(
            localRepo,
            commit,
            changedJavaFile.getOldPath(),
            changedJavaFile.getNewPath()
        );
        assertNotNull(parsedJavaCodeBeforeAndNowInCommit.getKey());
        assertNotNull(parsedJavaCodeBeforeAndNowInCommit.getValue());
    }

    @Test
    void spoonedDiffForJavaCodeBeforeAndNowInCommitTest() throws IOException, GitAPIException {
        List<RevCommit> commits = GitCookBook.allCommitsInRepo(localRepo);
        RevCommit commit = commits.get(5);
        DiffEntry changedJavaFile = GitCookBook.changedFilesInCommit(localRepo, commit).get(1);
        Pair<CtClass, CtClass> parsedJavaCodeBeforeAndNowInCommit = GitCookBook.spoonedJavaCodeBeforeAndNowInCommit(
            localRepo,
            commit,
            changedJavaFile.getOldPath(),
            changedJavaFile.getNewPath()
        );
        GitCookBook.spoonedDiffForCodeBeforeAndAfterInCommit(parsedJavaCodeBeforeAndNowInCommit);
    }

    @AfterAll
     static void cleanUp() throws IOException {
        GitCookBook.deleteLocalRepo(localRepo);
    }
}
