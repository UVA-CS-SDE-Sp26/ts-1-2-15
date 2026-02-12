import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for Team Member A's Userinterface
 * Uses a hand-written fake ProgramControl
 */
public class UserinterfaceTest {

    private FakeProgramControl fakeControl;
    private Userinterface ui;

    @BeforeEach
    void setUp() {
        fakeControl = new FakeProgramControl();
        ui = new Userinterface(fakeControl);
    }

    @Test
    void testInvalidFileNumberDoesNothing() {
        ui.run(new String[]{"999"});
        assertFalse(fakeControl.getFileListCalled);
        assertFalse(fakeControl.getFileContentCalled);
    }

    @Test
    void testNoArgsListsFiles() {
        fakeControl.files = Arrays.asList("filea.txt", "fileb.txt");
        ui.run(new String[]{});

        assertTrue(fakeControl.getFileListCalled);
    }

    @Test
    void testSingleArgUsesDefaultKey() {
        ui.run(new String[]{"01"});
        assertTrue(fakeControl.getFileContentCalled);
        assertEquals(1, fakeControl.lastIndex);
        assertEquals("ciphers/key.txt", fakeControl.lastKeyPath);
    }

  
    @Test
    void testControllerErrorDoesNotCrash() {
        fakeControl.throwError = true;
        assertDoesNotThrow(() -> ui.run(new String[]{"01"}));
        assertTrue(fakeControl.getFileContentCalled);
    }

    
    private static class FakeProgramControl extends ProgramControl {

        boolean getFileListCalled = false;
        boolean getFileContentCalled = false;
        boolean throwError = false;

        int lastIndex = -1;
        String lastKeyPath = null;

        List<String> files = Arrays.asList();

        @Override
        public List<String> getFileList() {
            getFileListCalled = true;
            return files;
        }

        @Override
        public String getFileContent(int index, String keyPath) {
            getFileContentCalled = true;
            lastIndex = index;
            lastKeyPath = keyPath;
            if(throwError) {
                throw new RuntimeException("File missing");
            }

            return "ABC";
        }
    }
}
