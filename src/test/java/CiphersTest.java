import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.Path;

public class CiphersTest {
    @TempDir
    Path tempDir; // clean temporary folder for the file below
    private File createTestFile(String name, String line1, String line2) throws IOException {
        File file = tempDir.resolve(name).toFile();
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(line1);
            out.println(line2);
        }
        return file;
    }
    @Test // ensures that validateKey throws true for equal length lines 1 and 2
    void test01_validateKey_HappyPath()throws IOException {
        File file = createTestFile("happy.txt", "ABC", "BCD");
        assertTrue(CipherDecrypter.loadKey(file.getAbsolutePath()));
    }

    @Test // ensures that validateKey throws false for unequal length lines 1 and 2
    void test02_validateKey_LengthMismatch()throws IOException {
        File file = createTestFile("mismatch.txt", "ABC", "BC");
        assertFalse(CipherDecrypter.loadKey(file.getAbsolutePath()));
    }
    @Test // ensures that validateKey throws false for empty length line 1
    void test03_validateKey_EmptyLine1()throws IOException {
        File file = createTestFile("emptyline1.txt", "", "BCD");
        assertFalse(CipherDecrypter.loadKey(file.getAbsolutePath()));
    }
    @Test // ensures that validateKey throws false for empty length line 2
    void test04_validateKey_EmptyLine2()throws IOException {
        File file = createTestFile("emptyline2.txt", "ABC", "");
        assertFalse(CipherDecrypter.loadKey(file.getAbsolutePath()));
    }
    @Test // ensures that decipher correctly deciphers expected characters
    void test05_decipher_Standard() throws IOException {
        File file = createTestFile("standard.txt", "ABC", "BCD");
        CipherDecrypter.loadKey(file.getAbsolutePath());
        String result = CipherDecrypter.decipher("BCD");
        assertEquals("ABC", result);
    }
    @Test // ensures that decipher correctly deciphers unspecified characters (space, punctuation, etc)
    void test06_decipher_SpecialChars() throws IOException {
        File file = createTestFile("special.txt", "ABC", "BCD");
        CipherDecrypter.loadKey(file.getAbsolutePath());
        String result = CipherDecrypter.decipher("B C D!");
        assertEquals("A B C!", result);
    }
    @Test // ensures that a missing file successfully throws FileNotFound exception
    void test07_loadKey_FileNotFound() {
        boolean result = CipherDecrypter.loadKey("ghost_file.txt");
        assertFalse(result);
    }
}
