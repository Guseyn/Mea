package pom;

public class PomDependency {
    public String groupId;
    public String artifactId;
    public String version;

    public PomDependency(final String groupId, final String artifactId, final String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }
}
