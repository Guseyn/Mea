package jreader;

import fs.FsCookBook;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;

public class DirectoryProcessor {
    public static void updateFilesInDirectory(String dirPath, String fileFormat, ContentModifier contentModifier) throws IOException {
        Files.find(
            Paths.get(dirPath),
            Integer.MAX_VALUE,
            (filePath, fileAttr) -> fileAttr.isRegularFile() && FilenameUtils.getExtension(filePath.toString()).equals(fileFormat)
        ).forEach((filePath) -> {
            try {
                FsCookBook.writeFile(
                    filePath.toString(),
                    contentModifier.newContent(FsCookBook.contentOfFile(filePath.toString()))
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
