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
        //mock FileHandler class
        mockedFileHandler = Mockito.mockStatic(FileHandler.class);
    }

    @AfterEach
    void tearDown() {
        //close after each
        if (mockedFileHandler != null) {
            mockedFileHandler.close();
        }
    }

    //getFileList good
    @Test
    void testGetFileList_ReturnsFileList() throws Exception {
        //Mock FileHandler.listFiles() to return test files
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList(
            "filea.txt",
            "fileb.txt",
            "filec.txt"
        ));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        //execute
        List<String> result = programControl.getFileList();
        //verify
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("filea.txt", result.get(0));
        assertEquals("fileb.txt", result.get(1));
        assertEquals("filec.txt", result.get(2));
    }

    // getFileList() - empty directory
    @Test
    void testGetFileList_EmptyDirectory() throws Exception {
        // Mock empty list
        ArrayList<String> emptyList = new ArrayList<>();
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(emptyList);
        List<String> result = programControl.getFileList();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    //getFileList() - Null Return
    @Test
    void testGetFileList_NullReturn_ThrowsException() {
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(null);
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileList();
        });
        assertTrue(exception.getMessage().contains("Unable to access file list"));
    }

    //getFileContent() - Valid File Number
    @Test
    void testGetFileContent_ValidFileNumber() throws Exception {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList(
            "filea.txt",
            "fileb.txt"
        ));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("filea.txt"))
                        .thenReturn("This is file A content\n");
        String content = programControl.getFileContent(1, "key.txt");
        assertNotNull(content);
        assertEquals("This is file A content\n", content);
    }

    @Test
    void testGetFileContent_SecondFile() throws Exception {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList(
            "filea.txt",
            "fileb.txt",
            "filec.txt"
        ));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("fileb.txt"))
                        .thenReturn("This is file B content\n");
        String content = programControl.getFileContent(2, "key.txt");
        assertNotNull(content);
        assertEquals("This is file B content\n", content);
    }

    @Test
    void testGetFileContent_FileNumberTooHigh_ThrowsException() {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList(
            "filea.txt",
            "fileb.txt"
        ));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(5, "key.txt");
        });
        assertTrue(exception.getMessage().contains("File number 05 not found"));
    }

    @Test
    void testGetFileContent_FileNumberZero_ThrowsException() {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("filea.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(0, "key.txt");
        });
        assertTrue(exception.getMessage().contains("File number 00 not found"));
    }

    @Test
    void testGetFileContent_NegativeFileNumber_ThrowsException() {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("filea.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(-1, "key.txt");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testGetFileContent_EmptyDirectory_ThrowsException() {
        ArrayList<String> emptyList = new ArrayList<>();
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(emptyList);
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(1, "key.txt");
        });
        assertTrue(exception.getMessage().contains("No files available"));
    }

    @Test
    void testGetFileContent_FileHandlerReturnsNotFound_ThrowsException() {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("filea.txt"));
        
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("filea.txt"))
                        .thenReturn("File not found!");
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(1, "key.txt");
        });
        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    void testGetFileContent_FileHandlerThrowsException() throws FileNotFoundException {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("filea.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("filea.txt"))
                        .thenThrow(new FileNotFoundException("File not accessible"));
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(1, "key.txt");
        });
        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    void testGetFileContent_MultipleFiles_AccessEach() throws Exception {
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
        assertEquals("Content 1", programControl.getFileContent(1, "key.txt"));
        assertEquals("Content 2", programControl.getFileContent(2, "key.txt"));
        assertEquals("Content 3", programControl.getFileContent(3, "key.txt"));
    }

    //workflow
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
        List<String> files = programControl.getFileList();
        assertEquals(2, files.size());
        String content = programControl.getFileContent(2, "key.txt");
        assertEquals("Content of B", content);
    }

    // multiline
    @Test
    void testGetFileContent_MultilineContent() throws Exception {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("multiline.txt"));
        String multilineContent = "Line 1\nLine 2\nLine 3\n";
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("multiline.txt"))
                        .thenReturn(multilineContent);
        String content = programControl.getFileContent(1, "key.txt");
        assertEquals(multilineContent, content);
        assertTrue(content.contains("Line 1"));
        assertTrue(content.contains("Line 2"));
        assertTrue(content.contains("Line 3"));
    }

    //keypath
    @Test
    void testGetFileContent_DifferentKeyPaths() throws Exception {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("file.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("file.txt"))
                        .thenReturn("Content");
        String content1 = programControl.getFileContent(1, "key.txt");
        assertNotNull(content1);
        String content2 = programControl.getFileContent(1, "custom.txt");
        assertNotNull(content2);
        // should return same
        assertEquals(content1, content2);
    }

    //signle file
    @Test
    void testGetFileContent_SingleFile() throws Exception {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("only.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        mockedFileHandler.when(() -> FileHandler.getFile("only.txt"))
                        .thenReturn("Only content");
        String content = programControl.getFileContent(1, "key.txt");
        assertEquals("Only content", content);
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(2, "key.txt");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    // large file num
    @Test
    void testGetFileContent_LargeFileNumber() {
        ArrayList<String> testFiles = new ArrayList<>(Arrays.asList("file.txt"));
        mockedFileHandler.when(FileHandler::listFiles).thenReturn(testFiles);
        Exception exception = assertThrows(Exception.class, () -> {
            programControl.getFileContent(99, "key.txt");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }
}