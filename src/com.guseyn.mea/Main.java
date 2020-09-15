import java.io.IOException;
import jreader.DirectoryProcessor;

public class Main {
    public static void main(String[] args) throws IOException {
        DirectoryProcessor.updateFilesInDirectory("broken-xml", "java", oldContentContent -> {
            // System.out.println(oldContentContent);
            return oldContentContent;
        });
    }
}
