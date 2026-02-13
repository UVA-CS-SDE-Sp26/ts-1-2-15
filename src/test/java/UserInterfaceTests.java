package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;


public class UserInterfaceTests {

    private FakeProgramControl fakeControl;
    private UserInterface ui;

    @BeforeEach
    void setUp() {
        fakeControl = new FakeProgramControl();
        ui = new UserInterface(fakeControl);
    }

    @Test
    void testNoArgsCallsGetFileList() {
        fakeControl.filesToReturn = Arrays.asList("a.txt", "b.txt");

        ui.run(new String[]{});

        assertTrue(fakeControl.getFileListCalled);
    }

    @Test
    void testOneArgUsesDefaultKeyPathAndFileNumber() {
        // avoid needing a real key file: stop after org.example.ProgramControl call
        fakeControl.throwOnGetFileContent = true;

        assertDoesNotThrow(() -> ui.run(new String[]{"01"}));

        assertTrue(fakeControl.getFileContentCalled);
        assertEquals(1, fakeControl.lastFileNumber);
        assertEquals("ciphers/key.txt", fakeControl.lastKeyPath);
    }

    @Test
    void testTwoArgsPassesCustomKeyPath() {
        fakeControl.throwOnGetFileContent = true;

        assertDoesNotThrow(() -> ui.run(new String[]{"05", "mykey.txt"}));

        assertTrue(fakeControl.getFileContentCalled);
        assertEquals(5, fakeControl.lastFileNumber);
        assertEquals("mykey.txt", fakeControl.lastKeyPath);
    }

    @Test
    void testInvalidFileNumberDoesNotCallControl() {
        ui.run(new String[]{"999"}); // not two digits

        assertFalse(fakeControl.getFileListCalled);
        assertFalse(fakeControl.getFileContentCalled);
    }

    @Test
    void testTooManyArgsDoesNotCallControl() {
        ui.run(new String[]{"01", "k.txt", "extra"});

        assertFalse(fakeControl.getFileListCalled);
        assertFalse(fakeControl.getFileContentCalled);
    }

    @Test
    void testHelpDoesNotCallControl() {
        ui.run(new String[]{"--help"});

        assertFalse(fakeControl.getFileListCalled);
        assertFalse(fakeControl.getFileContentCalled);
    }

    @Test
    void testControlExceptionDoesNotCrash() {
        fakeControl.throwOnGetFileContent = true;

        assertDoesNotThrow(() -> ui.run(new String[]{"01"}));
        assertTrue(fakeControl.getFileContentCalled);
    }

   
    private static class FakeProgramControl extends ProgramControl {

        boolean getFileListCalled = false;
        boolean getFileContentCalled = false;

        int lastFileNumber = -1;
        String lastKeyPath = null;

        boolean throwOnGetFileContent = false;

        List<String> filesToReturn = Arrays.asList();

        @Override
        public List<String> getFileList() throws Exception {
            getFileListCalled = true;
            return filesToReturn;
        }

        @Override
        public String getFileContent(int fileNumber, String keyPath) throws Exception {
            getFileContentCalled = true;
            lastFileNumber = fileNumber;
            lastKeyPath = keyPath;

            if (throwOnGetFileContent) {
                throw new Exception("Forced error for test");
            }

            return "ABC";
        }
    }
}
