import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class ProgramControlTest {

    private ProgramControl programControl;
    private MockedStatic<FileHandler> mockedFileHandler;

    @BeforeEach
    void setUp() {
        programControl = new ProgramControl();
        // Mock the static FileHandler methods
        mockedFileHandler = Mockito.mockStatic(FileHandler.class);
    }

    @AfterEach
    void tearDown() {
        // Close the static mock
        if (mockedFileHandler != null) {
            mockedFileHandler.close();
        }
    }

    // ===== TEST 1: getFileList() - Happy Path =====
    @Test
    void testGetFileList_ReturnsFileList() throws Exception {
        // Setup: Mock FileHandler.listFiles() to return test files
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList(
            "filea.txt",
            "fileb.txt",
            "filec.txt"
        ));
        
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        
        // Execute
        List<String> result = programControl.getFileList();
        
        // Verify
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("filea.txt", result.get(0));
        assertEquals("fileb.txt", result.get(1));
        assertEquals("filec.txt", result.get(2));
    }

    // ===== TEST 2: getFileList() - Empty Directory =====
    @Test
    void testGetFileList_EmptyDirectory() throws Exception {
        // Setup: Mock empty list
        ArrayList<String> emptyList = new ArrayList<>();
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(emptyList);
        
        // Execute
        List<String> result = programControl.getFileList();
        
        // Verify
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ===== TEST 3: getFileList() - Null Return =====
    @Test
    void testGetFileList_NullReturn_ThrowsException() {
        // Setup: Mock null return
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(null);
        
        // Execute & Verify
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileList();
        });
        
        assertTrue(exception.getMessage().contains("Unable to access file list"));
    }

    // ===== TEST 4: getFileContent() - Valid File Number =====
    @Test
    void testGetFileContent_ValidFileNumber() throws Exception {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList(
            "filea.txt",
            "fileb.txt"
        ));
        
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("filea.txt"))
                        .thenReturn("This is file A content\n");
        
        // Execute
        String content = programControl.getFileContent(1, "key.txt");
        
        // Verify
        assertNotNull(content);
        assertEquals("This is file A content\n", content);
    }

    // ===== TEST 5: getFileContent() - Second File =====
    @Test
    void testGetFileContent_SecondFile() throws Exception {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList(
            "filea.txt",
            "fileb.txt",
            "filec.txt"
        ));
        
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("fileb.txt"))
                        .thenReturn("This is file B content\n");
        
        // Execute
        String content = programControl.getFileContent(2, "key.txt");
        
        // Verify
        assertNotNull(content);
        assertEquals("This is file B content\n", content);
    }

    // ===== TEST 6: getFileContent() - File Number Out of Bounds (Too High) =====
    @Test
    void testGetFileContent_FileNumberTooHigh_ThrowsException() {
        // Setup: Only 2 files exist
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList(
            "filea.txt",
            "fileb.txt"
        ));
        
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        
        // Execute & Verify: Request file 5 when only 2 exist
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(5, "key.txt");
        });
        
        assertTrue(exception.getMessage().contains("File number 05 not found"));
    }

    // ===== TEST 7: getFileContent() - File Number Zero =====
    @Test
    void testGetFileContent_FileNumberZero_ThrowsException() {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("filea.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        
        // Execute & Verify: File number 0 should be invalid
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(0, "key.txt");
        });
        
        assertTrue(exception.getMessage().contains("File number 00 not found"));
    }

    // ===== TEST 8: getFileContent() - Negative File Number =====
    @Test
    void testGetFileContent_NegativeFileNumber_ThrowsException() {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("filea.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        
        // Execute & Verify
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(-1, "key.txt");
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    // ===== TEST 9: getFileContent() - Empty Directory =====
    @Test
    void testGetFileContent_EmptyDirectory_ThrowsException() {
        // Setup: No files
        ArrayList<String> emptyList = new ArrayList<>();
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(emptyList);
        
        // Execute & Verify
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(1, "key.txt");
        });
        
        assertTrue(exception.getMessage().contains("No files available"));
    }

    // ===== TEST 10: getFileContent() - FileHandler Returns "File not found!" =====
    @Test
    void testGetFileContent_FileHandlerReturnsNotFound_ThrowsException() {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("filea.txt"));
        
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("filea.txt"))
                        .thenReturn("File not found!");
        
        // Execute & Verify
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(1, "key.txt");
        });
        
        assertTrue(exception.getMessage().contains("File not found"));
    }

    // ===== TEST 11: getFileContent() - FileHandler Throws FileNotFoundException =====
    @Test
    void testGetFileContent_FileHandlerThrowsException() throws FileNotFoundException {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("filea.txt"));
        
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("filea.txt"))
                        .thenThrow(new FileNotFoundException("File not accessible"));
        
        // Execute & Verify
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(1, "key.txt");
        });
        
        assertTrue(exception.getMessage().contains("File not found"));
    }

    // ===== TEST 12: getFileContent() - Multiple Files, Access Each =====
    @Test
    void testGetFileContent_MultipleFiles_AccessEach() throws Exception {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList(
            "file1.txt",
            "file2.txt",
            "file3.txt"
        ));
        
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("file1.txt"))
                        .thenReturn("Content 1");
        mockedFileHandler.when(() -> FileHandler.getFile("file2.txt"))
                        .thenReturn("Content 2");
        mockedFileHandler.when(() -> FileHandler.getFile("file3.txt"))
                        .thenReturn("Content 3");
        
        // Execute & Verify each file
        assertEquals("Content 1", programControl.getFileContent(1, "key.txt"));
        assertEquals("Content 2", programControl.getFileContent(2, "key.txt"));
        assertEquals("Content 3", programControl.getFileContent(3, "key.txt"));
    }

    // ===== TEST 13: Integration Test - Workflow =====
    @Test
    void testWorkflow_ListThenReadFile() throws Exception {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList(
            "filea.txt",
            "fileb.txt"
        ));
        
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("fileb.txt"))
                        .thenReturn("Content of B");
        
        // Execute: First get list, then read a file
        List<String> files = programControl.getFileList();
        assertEquals(2, files.size());
        
        String content = programControl.getFileContent(2, "key.txt");
        assertEquals("Content of B", content);
    }

    // ===== TEST 14: getFileContent() - File With Multiline Content =====
    @Test
    void testGetFileContent_MultilineContent() throws Exception {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("multiline.txt"));
        String multilineContent = "Line 1\nLine 2\nLine 3\n";
        
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("multiline.txt"))
                        .thenReturn(multilineContent);
        
        // Execute
        String content = programControl.getFileContent(1, "key.txt");
        
        // Verify
        assertEquals(multilineContent, content);
        assertTrue(content.contains("Line 1"));
        assertTrue(content.contains("Line 2"));
        assertTrue(content.contains("Line 3"));
    }

    // ===== TEST 15: getFileContent() - Verify Key Path Parameter =====
    @Test
    void testGetFileContent_DifferentKeyPaths() throws Exception {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("file.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("file.txt"))
                        .thenReturn("Content");
        
        // Execute with default key
        String content1 = programControl.getFileContent(1, "key.txt");
        assertNotNull(content1);
        
        // Execute with custom key
        String content2 = programControl.getFileContent(1, "custom.txt");
        assertNotNull(content2);
        
        // Both should return same content (cipher logic is in UserInterface)
        assertEquals(content1, content2);
    }

    // ===== TEST 16: Edge Case - Single File =====
    @Test
    void testGetFileContent_SingleFile() throws Exception {
        // Setup: Only one file
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("only.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("only.txt"))
                        .thenReturn("Only content");
        
        // Execute
        String content = programControl.getFileContent(1, "key.txt");
        
        // Verify
        assertEquals("Only content", content);
        
        // Trying to access file 2 should fail
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(2, "key.txt");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    // ===== TEST 17: Edge Case - Large File Number =====
    @Test
    void testGetFileContent_LargeFileNumber() {
        // Setup
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("file.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        
        // Execute & Verify: File number 99 when only 1 file exists
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(99, "key.txt");
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }
}