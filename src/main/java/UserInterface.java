package org.example;

import java.util.List;

/*
 * Arnav Jadhav (dbm8fg)
 * Team Member A - Command Line Interface
 *
 * This class handles:
 * -reading command line arguments
 * -printing the file list
 * -printing a selected file's deciphered contents
 * -printing usage + errors for invalid input
 */
public class UserInterface {

    private static final String DEFAULT_KEY_PATH = "ciphers/key.txt";
    private final ProgramControl control;

    public UserInterface(ProgramControl control) {
        this.control = control;
    }

    public void run(String[] args) {
        if (args == null) {
            showError("Arguments cannot be null");
            return;
        }

        if(args.length == 1 && isHelp(args[0])) {
            printUsage();
            return;
        }

        if(args.length == 0) {
            printNumberedFiles();
            return;
        }

        if(args.length == 1) {
            displayFile(args[0], DEFAULT_KEY_PATH);
            return;
        }

        if(args.length == 2) {
            displayFile(args[0], args[1]);
            return;
        }

        showError("Too many arguments");
    }


    private void printNumberedFiles() {
        List<String> files;
        try {
            files = control.getFileList();
        } catch (Exception e) {
            showError(e.getMessage());
            return;
        }

        if(files == null || files.isEmpty()) {
            System.out.println("No files available.");
            return;
        }

        for(int i = 0; i < files.size(); i++) {
            System.out.printf("%02d %s%n", i + 1, files.get(i));
        }
    }

    private void displayFile(String fileCode, String keyPath) {
        if(!isTwoDigits(fileCode)) {
            showError("Invalid file number. Must be two digits like 01.");
            return;
        }

        if (isBlank(keyPath)) {
            keyPath = DEFAULT_KEY_PATH;
        }

        int fileNumber = Integer.parseInt(fileCode);

        String encrypted;
        try {
            encrypted = control.getFileContent(fileNumber, keyPath);
        } catch (Exception e) {
            showError(e.getMessage());
            return;
        }

        boolean ok = CipherDecrypter.loadKey(keyPath);
        if (!ok) {
            showError("Could not load key file: " + keyPath);
            return;
        }

        String plain = CipherDecrypter.decipher(encrypted);
        System.out.print(plain);
    }


    private static boolean isHelp(String s) {
        return s != null && (s.equals("-h") || s.equals("--help"));
    }

    private static boolean isTwoDigits(String s) {
        return s != null && s.matches("\\d{2}");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static void showError(String msg) {
        System.err.println("Error: " + msg);
        printUsage();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java topsecret");
        System.out.println("  java topsecret <NN>");
        System.out.println("  java topsecret <NN> <KEY_PATH>");
        System.out.println("  java topsecret --help");
        System.out.println("  java topsecret -h");
        System.out.println("");
        System.out.println("Notes:");
        System.out.println("  - <NN> must be a two-digit number like 01, 02, 10.");
        System.out.println("  - Default key path is " + DEFAULT_KEY_PATH + ".");
    }
}
