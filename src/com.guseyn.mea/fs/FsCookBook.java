package fs;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class FsCookBook {
    public static String contentOfFile(String path) throws IOException {
        return FileUtils.readFileToString(
            new File(path), "UTF-8"
        );
    }

    public static void writeFile(String path, String content) throws IOException {
        FileUtils.write(new File(path), content, "UTF8");
    }
}
