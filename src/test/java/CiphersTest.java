import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CiphersTest {

    @Test // ensures that validateKey throws true for equal length lines 1 and 2
    void test01_validateKey_HappyPath() {
        String keyContent = "ABC\nBCD";
        assertTrue(CipherDecrypter.loadKey(keyContent));
    }

    @Test // ensures that validateKey throws false for unequal length lines 1 and 2
    void test02_validateKey_LengthMismatch() {
        String keyContent = "ABC\nBC";
        assertFalse(CipherDecrypter.loadKey(keyContent));
    }
    @Test // ensures that validateKey throws false for empty length line 1
    void test03_validateKey_EmptyLine1() {
        String keyContent = "\nBCD";
        assertFalse(CipherDecrypter.loadKey(keyContent));
    }
    @Test // ensures that validateKey throws false for empty length line 2
    void test04_validateKey_EmptyLine2() {
        String keyContent = "ABC\n";
        assertFalse(CipherDecrypter.loadKey(keyContent));
    }
    @Test // ensures that decipher correctly deciphers expected characters
    void test05_decipher_Standard() {
        CipherDecrypter.loadKey("ABC\nBCD");
        String result = CipherDecrypter.decipher("BCD");
        assertEquals("ABC", result);
    }
    @Test // ensures that decipher correctly deciphers unspecified characters (space, punctuation, etc)
    void test06_decipher_SpecialChars() {
        CipherDecrypter.loadKey("ABC\nBCD");
        String result = CipherDecrypter.decipher("B C D!");
        assertEquals("A B C!", result);
    }
}
