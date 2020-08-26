package git;

import io.reflectoring.diffparser.api.model.Range;

public class JavaCodeChange {
    public String filePath;
    public String line;
    public boolean isAdded;
    public boolean isRemoved;
    public Range range;
    public String codeBefore;
    public String codeAfter;

    public JavaCodeChange(final String filePath, final String line, final boolean isAdded,
                          final boolean isRemoved, final Range range,
                          final String codeBefore, final String codeAfter) {
        this.filePath = filePath;
        this.line = line;
        this.isAdded = isAdded;
        this.isRemoved = isRemoved;
        this.range = range;
        this.codeBefore = codeBefore;
        this.codeAfter = codeAfter;
    }
}
