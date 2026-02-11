import java.io.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class CipherDecrypter {
    private static final HashMap<Character, Character> keyMap = new HashMap<>();

    private static final String DEFAULT_PATH = "ciphers/key.txt";

    private static boolean validateKey(String plainLine, String cipherLine) {

        if (plainLine == null || cipherLine == null) {
            return false;
        }
        if (plainLine.length() != cipherLine.length()) {
            return false;
        }
        return true;
    }
    public static boolean loadKey(String filePath) {

        if (filePath.isEmpty()) {
            filePath = DEFAULT_PATH;
        }

        String plainLine;
        String cipherLine;

        try (BufferedReader br
                     = new BufferedReader(new FileReader(filePath))) {

            plainLine = br.readLine();

            cipherLine = br.readLine();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return false;
        } catch (IOException e) {
            System.out.println("Error reading file");
            return false;
        }

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

