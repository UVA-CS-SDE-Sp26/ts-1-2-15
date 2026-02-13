
import java.util.HashMap;

public class CipherDecrypter {
    private static final HashMap<Character, Character> keyMap = new HashMap<>();

    private static boolean validateKey(String plainLine, String cipherLine) {

        if (plainLine == null || cipherLine == null) {
            return false;
        }
        if (plainLine.length() != cipherLine.length()) {
            return false;
        }
        return true;
    }
    public static boolean loadKey(String keyContent) {
        if (keyContent == null || keyContent.isEmpty()) {
            return false;
        }

        String[] lines = keyContent.split("\n");

        if (lines.length < 2) {
            return false;
        }

        String plainLine = lines[0];
        String cipherLine = lines[1];

        if (!validateKey(plainLine, cipherLine)) {
            return false;
        }

        int cipherLength = plainLine.length();

        for (int i = 0; i < cipherLength; i++) {

            char plain = plainLine.charAt(i);
            char cipher = cipherLine.charAt(i);
            keyMap.put(cipher, plain);
        }
        return true;
    }
    public static String decipher(String encryptedString) {

        StringBuilder result = new StringBuilder();

        for (int i =0; i < encryptedString.length(); i++) {

            char cipher = encryptedString.charAt(i);
            char plain = keyMap.getOrDefault(cipher,cipher);
            result.append(plain);
        }
        return result.toString();
    }
}

