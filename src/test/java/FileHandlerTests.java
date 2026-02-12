import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<String> files = FileHandler.listFiles();

        for (String file : files) {
            System.out.println(file);
        }

        String file = FileHandler.getFile("filea.txt");
        System.out.println(file);

        String file1 = FileHandler.getFile("fileb.txt");
        System.out.println(file1);

        String file2 = FileHandler.getFile("filec.txt");
        System.out.println(file2);

    }
}
