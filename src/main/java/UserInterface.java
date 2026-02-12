import java.util.List;

//Team Member A 
//Responsible for Command Line Interface argument validation

public class Userinterface {

    private static final String DEFAULT_KEY_PATH = "ciphers/key.txt";

    private final ProgramControl control;

    public Userinterface(ProgramControl control) {
        this.control = control;
    }

    public void run(String[] args) {
        if(args == null) {
            printError("Arguments cannot be null");
            return;
        }

        if(args.length == 1 && isHelpFlag(args[0])) {
            printUsage();
            return;
        }

        if(args.length == 0) {
            handleListFiles();
            return;
        }

        if(args.length == 1) {
            handleDisplayFile(args[0], DEFAULT_KEY_PATH);
            return;
        }

        if(args.length == 2) {
            handleDisplayFile(args[0], args[1]);
            return;
        }

        printError("Too many arguments");
    }

    private static boolean isHelpFlag(String s) {
        return s != null && (s.equals("-h") || s.equals("--help"));
    }

    private void handleListFiles() {
        List<String> files;
        try{
            files = control.getFileList();
        }catch(Exception e) {
            printError(e.getMessage());
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

    private void handleDisplayFile(String fileNum, String keyPath) {
        if(!isValidFileNumber(fileNum)) {
            printError("Invalid file number. Must be two digits like 01.");
            return;
        }

        if(isBlank(keyPath)) {
            printError("Key path cannot be empty.");
            return;
        }

        int index = Integer.parseInt(fileNum);

        String encrypted;
        try{
            encrypted = control.getFileContent(index, keyPath);
        }catch (Exception e) {
            printError(e.getMessage());
            return;
        }

        if(!CipherDecrypter.loadKey(keyPath)) {
            printError("Failed to load key file: " + keyPath);
            return;
        }

        String plain = CipherDecrypter.decipher(encrypted);
        System.out.print(plain);
    }

    public static boolean isValidFileNumber(String s) {
        return s != null && s.matches("\\d{2}");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static void printError(String message) {
        System.err.println("Error: " + message);
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
        System.out.println("  <NN> must be a two-digit file number like 01, 02, 10.");
        System.out.println("  <KEY_PATH> is optional; default is ciphers/key.txt.");
    }
}
