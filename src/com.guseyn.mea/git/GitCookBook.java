package git;

import com.github.javaparser.ast.CompilationUnit;
import com.google.common.collect.Lists;
import com.guseyn.broken_xml.ParsedXML;
import com.guseyn.broken_xml.XmlDocument;
import io.reflectoring.diffparser.api.DiffParser;
import io.reflectoring.diffparser.api.UnifiedDiffParser;
import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Hunk;
import io.reflectoring.diffparser.api.model.Line;
import j.JavaCookBook;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import mine.MineCookBook;
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

    static List<FileChangesInCommit> changedContentOfFilesInCommit(File repoFolder, RevCommit commit, List<String> formats) throws IOException, GitAPIException {
        List<FileChangesInCommit> filesChanges = new ArrayList<>();
        try (Git git = Git.open(repoFolder)) {
            Repository repository = git.getRepository();
            List<DiffEntry> diffs = changedFilesInCommit(repoFolder, commit);
            for (DiffEntry diff: diffs) {
                if (diff.getChangeType().equals(DiffEntry.ChangeType.MODIFY) && formats.stream().anyMatch(format -> diff.getNewPath().endsWith("." + format))) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try (DiffFormatter formatter = new DiffFormatter(outputStream)) {
                        formatter.setRepository(repository);
                        formatter.format(diff);
                        DiffParser parser = new UnifiedDiffParser();
                        filesChanges.add(
                            new FileChangesInCommit(
                                diff.getNewPath(),
                                diff.getOldPath(),
                                parser.parse(outputStream.toByteArray())
                            )
                        );
                    }
                }
            }
        }
        return filesChanges;
    }

    static List<JavaCodeChange> changedLinesOfCodeOfJavaFilesInCommit(File repoFolder, RevCommit commit) throws IOException, GitAPIException {
        List<JavaCodeChange> changedLinesInFile = new ArrayList<>();
        List<FileChangesInCommit> changedContentOfFilesInCommit = changedContentOfFilesInCommit(
            repoFolder, commit, Lists.newArrayList("java")
        );

        for (FileChangesInCommit changedContentOfFileInCommit : changedContentOfFilesInCommit) {
            Pair<String, String> javaContentBeforeAndNowInCommit = fileContentBeforeAndNowInCommit(
                repoFolder, commit, changedContentOfFileInCommit.oldPath, changedContentOfFileInCommit.newPath
            );
            for (Diff diff: changedContentOfFileInCommit.diffs) {
                for (Hunk hunk: diff.getHunks()) {
                    for (Line line: hunk.getLines()) {
                        String codeBefore = MineCookBook.contentFromLineToLine(
                            javaContentBeforeAndNowInCommit.getKey(),
                            hunk.getFromFileRange().getLineStart(),
                            hunk.getFromFileRange().getLineCount()
                        );
                        String codeAfter = MineCookBook.contentFromLineToLine(
                            javaContentBeforeAndNowInCommit.getValue(),
                            hunk.getToFileRange().getLineStart(),
                            hunk.getToFileRange().getLineCount()
                        );
                        if (line.getLineType().equals(Line.LineType.TO)) {
                            changedLinesInFile.add(
                                new JavaCodeChange(
                                    changedContentOfFileInCommit.newPath,
                                    line.getContent(),
                                    true,
                                    false,
                                    hunk.getToFileRange(),
                                    codeBefore,
                                    codeAfter
                                )
                            );
                        } else if (line.getLineType().equals(Line.LineType.FROM)) {
                            changedLinesInFile.add(
                                new JavaCodeChange(
                                    changedContentOfFileInCommit.oldPath,
                                    line.getContent(),
                                    false,
                                    true,
                                    hunk.getToFileRange(),
                                    codeBefore,
                                    codeAfter
                                )
                            );
                        }
                    }
                }
            }
        }
        return changedLinesInFile;
    }

    static Pair<String, String> fileContentBeforeAndNowInCommit(File repoFolder, RevCommit commit, String oldFilePath, String newFilePath) throws IOException {
        try (Git git = Git.open(repoFolder)) {
            Repository repository = git.getRepository();
            String commitName = commit.getName();
            String prevCommitName = commitName + "^";
            String fileContentBefore = contentFromFileInCommit(repository, prevCommitName, oldFilePath);
            String fileContentNow = contentFromFileInCommit(repository, commitName, newFilePath);
            return new Pair<>(fileContentBefore, fileContentNow);
        }
    }

    static Pair<XmlDocument, XmlDocument> parsedXmlDocumentBeforeAndNowInCommit(File repoFolder, RevCommit commit, String oldPomPath, String newPomPath) throws IOException {
        Pair<String, String> pomContentBeforeAndNowInCommit = fileContentBeforeAndNowInCommit(repoFolder, commit, oldPomPath, newPomPath);
        return new Pair<>(
            new ParsedXML(pomContentBeforeAndNowInCommit.getKey()).document(),
            new ParsedXML(pomContentBeforeAndNowInCommit.getValue()).document()
        );
    }

    static Pair<CompilationUnit, CompilationUnit> parsedJavaCodeBeforeAndNowInCommit(File repoFolder, RevCommit commit, String oldJavaFilePath, String newJavaFilePath) throws IOException {
        Pair<String, String> javaContentBeforeAndNowInCommit = fileContentBeforeAndNowInCommit(repoFolder, commit, oldJavaFilePath, newJavaFilePath);
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
