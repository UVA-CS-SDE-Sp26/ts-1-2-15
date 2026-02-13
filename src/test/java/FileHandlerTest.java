import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {

    @Test
    void testListFiles_ReturnsNotNull() {
        ArrayList<String> files = FileHandler.listFiles();
        assertNotNull(files, "File list should never be null, even if empty");
    }

    @Test
    void testReadKeyFile_ReadsContent(@TempDir Path tempDir) throws IOException {

        Path tempFile = tempDir.resolve("testkey.txt");
        String expectedContent = "A\nB\n";
        Files.writeString(tempFile, expectedContent);

        String actualContent = FileHandler.readKeyFile(tempFile.toString());

        assertNotNull(actualContent);
        assertEquals(expectedContent.trim(), actualContent.trim());
    }

    @Test
    void testReadKeyFile_ReturnsNullForMissingFile() {
        String content = FileHandler.readKeyFile("non_existent_ghost_file.txt");
        assertNull(content, "Should return null when the file does not exist");
    }
}