package git;

import io.reflectoring.diffparser.api.model.Diff;
import java.util.List;

public class FileChangesInCommit {
    public String newPath;
    public String oldPath;
    List<Diff> diffs;

    public FileChangesInCommit(final String newPath, final String oldPath,
                               final List<Diff> diffs) {
        this.newPath = newPath;
        this.oldPath = oldPath;
        this.diffs = diffs;
    }
}
