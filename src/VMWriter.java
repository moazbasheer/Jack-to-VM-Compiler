package codegeneration;
import syntaxanalyzer.Constants;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class VMWriter {
    private FileWriter output;
    public VMWriter(File file) throws IOException {
        System.out.println(file.getName());
        output = new FileWriter(file);
    }
    private String getSegment(int segment){
        if(segment == Kind.ARG){
            return "argument";
        }else if(segment == Kind.VAR){
            return "local";
        }else if(segment == Constants.CONSTANT){
            return "constant";
        }
        return "";
    }
    public void writePush(int segment,int index) throws IOException {
        output.write("push " + getSegment(segment) + " " + index + "\n");
    }

    public void writePop(int segment,int index) throws IOException {
        output.write("pop " + getSegment(segment) + " " + index + "\n");
    }

    public void writeArithmetic(int command) throws IOException {
        if(command == Command.ADD){
            output.write("add\n");
        }else if(command == Command.MULT){
            output.write("call Math.multiply 2\n");
        }else if(command == Command.SUB){
            output.write("sub\n");
        }else if(command == Command.DIV){
            output.write("call Math.divide 2\n");
        }else if(command == Command.NEG){
            output.write("neg\n");
        }else if(command == Command.OR){
            output.write("or\n");
        }else if(command == Command.NOT){
            output.write("not\n");
        }else if(command == Command.EQ){
            output.write("eq\n");
        }
    }

    public void writeLabel(String label) throws IOException{
        output.write("label " + label + "\n");
    }

    public void writeGoto(String label) throws IOException{
        output.write("goto " + label + "\n");
    }

    public void writeIf(String label) throws IOException{
        output.write("if-goto " + label + "\n");
    }

    public void writeCall(String name,int nArgs){

    }

    public void writeFunctions(String name,int nLocals) throws IOException {
        output.write("function " + name + " " + nLocals +"\n");
    }

    public void writeReturn() throws IOException {
        output.write("return\n");
    }

    public void close() throws IOException {
        output.close();
    }
}
