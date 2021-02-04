import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class JackAnalyzer {
    static String filename;
    static File file;
    static HashMap<String, String> map;

    public static void run() {
        file = new File(filename);
        String[] fileName = null;
        map = new HashMap<>();
        if (file.isFile()) {
            if (filename.contains(".jack")) {
                fileName = new String[1];
                fileName[0] = filename;
            } else {
                System.out.println("Not a jack file");
                return;
            }
        } else if (file.isDirectory()) {
            fileName = file.list();
            for (int i = 0; i < fileName.length; i++) {
                if (!fileName[i].contains(".jack")) {
                    fileName[i] = "";
                } else {
                    fileName[i] = filename + "/" + fileName[i];
                }
            }
        } else {
            System.out.println("Not a jack file");
            return;
        }
        try {

            for (int i = 0; i < fileName.length; i++) {
                if (fileName[i].equals("")) continue;
                String outputName = fileName[i].split("[.]")[0] + ".xml";
                CompilationEngine engine = new CompilationEngine(outputName);
                engine.compileClass();
                engine.close();
            }
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    public static void main(String[] args) {
        filename = "";
        for (int i = 0; i < args.length; i++) {
            if (i == 0)
                filename += args[i];
            else
                filename += " " + args[i];
        }
        run();
    }
}
