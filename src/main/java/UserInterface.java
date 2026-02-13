import java.util.List;

//Team Member A 
//Responsible for Command Line Interface argument validation

public class UserInterface {

    private static final String DEFAULT_KEY = "ciphers/key.txt";
    private final ProgramControl control;

    public Userinterface(ProgramControl control) {
        this.control = control;
    }

    public void run(String[] args) {
        if (args == null) {
            reportError("Arguments cannot be null");
            return;
        }

        if (args.length == 1 && isHelpRequest(args[0])) {
            showUsage();
            return;
        }

        if (args.length == 0) {
            displayFileList();
            return;
        }

        if (args.length == 1) {
            displaySingleFile(args[0], DEFAULT_KEY);
            return;
        }

        if (args.length == 2) {
            displaySingleFile(args[0], args[1]);
            return;
        }

        reportError("Too many arguments");
    }

    private void displayFileList() {
        List<String> files;
        try {
            files = control.getFileList();
        } catch (Exception e) {
            reportError(e.getMessage());
            return;
        }

        if (files == null || files.isEmpty()) {
            System.out.println("No files available.");
            return;
        }

        for (int i = 0; i < files.size(); i++) {
            System.out.printf("%02d %s%n", i + 1, files.get(i));
        }
    }

    private void displaySingleFile(String fileCode, String keyPath) {
        if (!isTwoDigitCode(fileCode)) {
            reportError("Invalid file number. Must be two digits like 01.");
            return;
        }

        if (isBlank(keyPath)) {
            reportError("Key path cannot be empty.");
            return;
        }

        int index = Integer.parseInt(fileCode);

        String encrypted;
        try {
            encrypted = control.getFileContent(index, keyPath);
        } catch (Exception e) {
            reportError(e.getMessage());
            return;
        }

        if (!CipherDecrypter.loadKey(keyPath)) {
            reportError("Failed to load key file: " + keyPath);
            return;
        }

        System.out.print(CipherDecrypter.decipher(encrypted));
    }

    private static boolean isHelpRequest(String arg) {
        return arg != null && (arg.equals("-h") || arg.equals("--help"));
    }

    public static boolean isTwoDigitCode(String s) {
        return s != null && s.matches("\\d{2}");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static void reportError(String message) {
        System.err.println("Error: " + message);
        showUsage();
    }

    private static void showUsage() {
        System.out.println("Usage:");
        System.out.println("  java topsecret");
        System.out.println("  java topsecret <NN>");
        System.out.println("  java topsecret <NN> <KEY_PATH>");
        System.out.println("  java topsecret --help");
        System.out.println("  java topsecret -h");
    }
}

