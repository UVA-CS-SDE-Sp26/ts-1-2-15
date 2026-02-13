//Eliza Tan tpj4cd

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ProgramControl {
    //gets list of files from filehandler and returns the names
    public List<String> getFileList() throws Exception {
        ArrayList<String> files = FileHandler.listFiles();
        //exception if none
        if (files == null) {
            throw new Exception("Unable to access file list");
        }
        return files;
    }
    //getting the content of the file according to the fileNumber
    //keypath is the cipher key
    public String getFileContent(int fileNumber, String keyPath) throws Exception {
        //getting files
        List<String> files = getFileList();
        //exception
        if (files.isEmpty()) {
            throw new Exception("No files available in data directory");
        }
        // make sure the fileNumber is valid
        if (fileNumber < 1 || fileNumber > files.size()) {
            throw new Exception("File number " + String.format("%02d", fileNumber) + " not found");
        }
        //get the correct file based on the number
        String fileName = files.get(fileNumber - 1);
        // read it -> use file handler
        try {
            String content = FileHandler.getFile(fileName);
            //checking -> throw exception if no
            if (content.equals("File not found!")) {
                throw new Exception("File not found: " + fileName);
            }
            return content;
        } catch (FileNotFoundException e) {
            throw new Exception("File not found: " + fileName);
        }
    }
}

//create testing -> Mockito
