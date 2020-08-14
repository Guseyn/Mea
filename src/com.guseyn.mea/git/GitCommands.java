package git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class GitCommands {
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
        Git git = Git.open(repo);
        Iterable<RevCommit> commits = git.log().all().call();
        List<RevCommit> commitsAsList = new ArrayList<>();
        commits.forEach(commitsAsList::add);
        return commitsAsList;
    }

    static List<DiffEntry> diffBetweenTwoCommits(File repoFolder, RevCommit firstRevCommit, RevCommit secondRevCommit) throws IOException, GitAPIException {
        Git git = Git.open(repoFolder);
        Repository repository = git.getRepository();
        ObjectId firstCommit = firstRevCommit.getTree();
        ObjectId secondCommit = secondRevCommit.getTree();
        try (ObjectReader reader = repository.newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, firstCommit);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, secondCommit);

            List<DiffEntry> diffs= git.diff()
                .setNewTree(newTreeIter)
                .setOldTree(oldTreeIter)
                .call();
            for (DiffEntry entry : diffs) {
                System.out.println("Entry: " + entry);
            }
            return diffs;
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
