package git;

import com.github.javaparser.ast.CompilationUnit;
import com.guseyn.broken_xml.ParsedXML;
import com.guseyn.broken_xml.XmlDocument;
import io.reflectoring.diffparser.api.DiffParser;
import io.reflectoring.diffparser.api.UnifiedDiffParser;
import io.reflectoring.diffparser.api.model.Diff;
import j.JavaCookBook;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class GitCookBook {
    static File clone(File directory, String remoteLink) throws GitAPIException {
        Git.cloneRepository()
            .setURI(remoteLink)
            .setDirectory(directory)
            .setProgressMonitor(new SimpleProgressMonitor(remoteLink))
            .call();
        return directory;
    }

    static void deleteLocalRepo(File repo) throws IOException {
        FileUtils.deleteDirectory(repo);
    }

    static List<RevCommit> allCommitsInRepo(File repo) throws IOException, GitAPIException {
        try (Git git = Git.open(repo)) {
            Iterable<RevCommit> commits = git.log().all().call();
            List<RevCommit> commitsAsList = new ArrayList<>();
            commits.forEach(commitsAsList::add);
            return commitsAsList;
        }
    }

    static List<DiffEntry> changedFilesInCommit(File repoFolder, RevCommit commit) throws IOException, GitAPIException {
        try (Git git = Git.open(repoFolder)) {
            Repository repository = git.getRepository();
            commit.toObjectId().toObjectId();
            String commitName = commit.getName();
            String prevCommitName = commitName + "^";
            return git.diff()
                .setOldTree(commitIterator(repository, prevCommitName))
                .setNewTree(commitIterator(repository, commitName))
                .call();
        }
    }

    static Map<String, List<Diff>> changedContentOfJavaAndXmlFilesInCommit(File repoFolder, RevCommit commit) throws IOException, GitAPIException {
        Map<String, List<Diff>> contentDiffs = new HashMap<>();
        try (Git git = Git.open(repoFolder)) {
            Repository repository = git.getRepository();
            List<DiffEntry> diffs = changedFilesInCommit(repoFolder, commit);
            for (DiffEntry diff: diffs) {
                if (diff.getChangeType().equals(DiffEntry.ChangeType.MODIFY) && (diff.getNewPath().endsWith(".java") || diff.getNewPath().endsWith(".xml"))) {
                    String filePath = diff.getNewPath();
                    contentDiffs.put(filePath, new ArrayList<>());
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try (DiffFormatter formatter = new DiffFormatter(outputStream)) {
                        formatter.setRepository(repository);
                        formatter.format(diff);
                        DiffParser parser = new UnifiedDiffParser();
                        contentDiffs.get(filePath).addAll(
                            parser.parse(outputStream.toByteArray())
                        );
                    }
                }
            }
        }
        return contentDiffs;
    }

    static Pair<String, String> fileContentBeforeAndNowInCommit(File repoFolder, RevCommit commit, String filePath) throws IOException {
        try (Git git = Git.open(repoFolder)) {
            Repository repository = git.getRepository();
            String commitName = commit.getName();
            String prevCommitName = commitName + "^";
            String fileContentBefore = contentFromFileInCommit(repository, prevCommitName, filePath);
            String fileContentNow = contentFromFileInCommit(repository, commitName, filePath);
            return new Pair<>(fileContentBefore, fileContentNow);
        }
    }

    static Pair<XmlDocument, XmlDocument> parsedXmlDocumentBeforeAndNowInCommit(File repoFolder, RevCommit commit, String pomPath) throws IOException {
        Pair<String, String> pomContentBeforeAndNowInCommit = fileContentBeforeAndNowInCommit(repoFolder, commit, pomPath);
        return new Pair<>(
            new ParsedXML(pomContentBeforeAndNowInCommit.getKey()).document(),
            new ParsedXML(pomContentBeforeAndNowInCommit.getValue()).document()
        );
    }

    static Pair<CompilationUnit, CompilationUnit> parsedJavaCodeBeforeAndNowInCommit(File repoFolder, RevCommit commit, String javaFilePath) throws IOException {
        Pair<String, String> javaContentBeforeAndNowInCommit = fileContentBeforeAndNowInCommit(repoFolder, commit, javaFilePath);
        return new Pair<>(
            JavaCookBook.parsedJavaCode(javaContentBeforeAndNowInCommit.getKey()),
            JavaCookBook.parsedJavaCode(javaContentBeforeAndNowInCommit.getValue())
        );
    }

    static String contentFromFileInCommit(Repository repository, String commitName, String filePath) throws IOException {
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(commitIterator(repository, commitName));
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create(filePath));
        if (treeWalk.next()) {
            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            loader.copyTo(outputStream);
            return new String(outputStream.toByteArray());
        }
        return null;
    }

    private static AbstractTreeIterator commitIterator(Repository repository, String commitName) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(commitName));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();
            return treeParser;
        }
    }

    private static class SimpleProgressMonitor implements ProgressMonitor {
        private String clonePath;

        public SimpleProgressMonitor(final String clonePath) {
            this.clonePath = clonePath;
        }

        @Override
        public void start(int totalTasks) {
            System.out.println("\nStarting cloning " + clonePath);
        }

        @Override
        public void beginTask(String title, int totalWork) {
            System.out.println("Start " + title + ": " + totalWork);
        }

        @Override
        public void update(int completed) {
            System.out.print(completed + "-");
        }

        @Override
        public void endTask() {
            System.out.println("Done");
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }
}
