import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class JackCompiler {
    private static String filename;
    private static File file;
    private void run(){
        file = new File(filename);
        String[] fileName = null;
        if (file.isFile()) {

            if (filename.contains(".jack")) {
                fileName = new String[1];
                fileName[0] = filename;
            } else {
                System.out.println("Not a jack file");
                return ;
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
                if(fileName[i].length() == 0) continue;
                String outputName = fileName[i].split("[.]")[0] + ".vm";
                CompilationEngine engine = new CompilationEngine(outputName);
                engine.compileClass();
                engine.close();
            }
        } catch (IOException e) {
            System.out.println("IOException");
        }
        System.out.println("Compilation completed!");
    }
    public static void main(String[] args) {
        filename = "";
        for (int i = 0; i < args.length; i++) {
            if (i == 0)
                filename += args[i];
            else
                filename += " " + args[i];
        }

        JackCompiler compiler = new JackCompiler();
        compiler.run();
    }
}
