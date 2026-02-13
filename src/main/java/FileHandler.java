import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class FileHandler {

    public static ArrayList<String> listFiles()  {

        ArrayList<String> files = new ArrayList<>();

        File file = new File("data");

        if(!file.exists() || !file.isDirectory()) {
            return files;
        }

        File[] fileList = file.listFiles();

        if (fileList != null) {
            for (File f : fileList) {
                if(f.isFile() && f.getName().endsWith(".txt")) {
                    files.add(f.getName());
                }
            }
        }

        return files;

    }

    public static String readKeyFile(String filePath) {
        File file = new File(filePath);

        // Check if file exists
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        try (Scanner scanner = new Scanner(file)) {
            StringBuilder fileContent = new StringBuilder();
            while (scanner.hasNextLine()) {
                fileContent.append(scanner.nextLine()).append("\n");
            }
            return fileContent.toString();
        } catch (FileNotFoundException e) {
            return null;
        }
    }
  public static String getFile(String fileName) throws FileNotFoundException {

        File file = new File("data/" + fileName);

        if (!file.exists() || !file.isFile()) {
            return "File not found!";
        }

        String fileContent = "";
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            fileContent = fileContent + scanner.nextLine() + "\n";
        }

        scanner.close();
        return fileContent;

    }

}
