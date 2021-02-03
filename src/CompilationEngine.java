import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class CompilationEngine {
    private JackTokenizer tokenizer;
    private File file;
    private FileWriter output;
    private String indentation;
    private VMWriter writer;
    private String className;
    private SymbolTable symbolTable;
    private int nLocals = 0;
    private int countIF = -1;
    private int countWhile = -1;
    public CompilationEngine(String filename)
            throws IOException {
        file = new File(filename);
        writer = new VMWriter(file);
        tokenizer = new JackTokenizer(filename.split("[.]")[0] + ".jack");
        symbolTable = new SymbolTable();
        indentation = "";
        className = "";
    }

    public void compileClass() throws IOException {
        tokenizer.advance();
        if (tokenizer.tokenType() == Constants.KEYWORD
            && tokenizer.keyword() == Constants.CLASS) {
            tokenizer.advance();
            if (tokenizer.tokenType() == Constants.IDENTIFIER) { // class name
                className = tokenizer.getToken();
                tokenizer.advance();
            } else {
                System.out.println("Error in class name");
                return;
            }
            if (tokenizer.tokenType() == Constants.SYMBOL) { // class bracket {
                tokenizer.advance();
            } else {
                System.out.println("Error in bracket of class");
                return;
            }

            compileClassVariables();
            compileMethods(); //heeeeeeeeeeeeeeeeeeeere
            tokenizer.advance();
        }else {
            System.out.println("class keyword not found\n");
            return ;
        }
    }
    private void compileClassVariables() throws IOException{
        
        while(tokenizer.keyword() == Constants.STATIC
                || tokenizer.keyword() == Constants.FIELD) {
            symbolTable.startSubroutine();
            String varName = "";
            String varType = "";
            int varKind = tokenizer.keyword();
            tokenizer.advance();
            if (tokenizer.keyword() == Constants.BOOLEAN 
                    || tokenizer.keyword() == Constants.INT 
                    || tokenizer.keyword() == Constants.CHAR
                    || tokenizer.tokenType() == Constants.IDENTIFIER) {
                varType = tokenizer.getToken();
                tokenizer.advance();
            } else {
                System.out.println("ERROR in data type");
                return;
            }

            if (tokenizer.tokenType() == Constants.IDENTIFIER) {
                varName = tokenizer.getToken();
                symbolTable.define(varName,varType,varKind);
                tokenizer.advance(); // handling ; and ,
            } else {
                System.out.println("ERROR in variable name");
                return;
            }

            while (tokenizer.tokenType() == Constants.SYMBOL) {
                if(tokenizer.getToken().equals(";")) {
                    tokenizer.advance();
                    break;
                } else if(tokenizer.getToken().equals(",")) {
                    tokenizer.advance();
                    if(tokenizer.tokenType() == Constants.IDENTIFIER) {
                        varName = tokenizer.getToken();
                        symbolTable.define(varName, varType, varKind);
                        tokenizer.advance();
                    } else {
                        System.out.println("ERROR in variable name");
                        return ;
                    }
                }
                else {
                    System.out.println("ERROR in semicolon");
                    return ;
                }
            }
        }
    }
    private void compileMethods() throws IOException{
        while(tokenizer.keyword() == Constants.FUNCTION
                || tokenizer.keyword() == Constants.METHOD
                || tokenizer.keyword() == Constants.CONSTRUCTOR) {
            nLocals = 0;
            symbolTable.startSubroutine();
            // header of function
            tokenizer.advance();
            String funName = "";
            if(tokenizer.tokenType() == Constants.IDENTIFIER
                    || tokenizer.tokenType() == Constants.KEYWORD){
                tokenizer.advance();
            }else{
                System.out.println("Error invalid function return type");
                return ;
            }
            if(tokenizer.tokenType() == Constants.IDENTIFIER){
                funName = tokenizer.getToken();
                tokenizer.advance();
            }else{
                System.out.println("Error invalid function name");
                return ;
            }

            if(tokenizer.tokenType() == Constants.SYMBOL){ // parameters bracket in the function
                tokenizer.advance();
            }else{
                System.out.println("Error ,there's no symbol (");
                return ;
            }
            // parameters of function
            while(tokenizer.tokenType() == Constants.IDENTIFIER
                || tokenizer.tokenType() == Constants.KEYWORD){
                String argType = tokenizer.getToken();
                String argName = "";
                tokenizer.advance();
                if(tokenizer.tokenType() == Constants.IDENTIFIER){
                    argName = tokenizer.getToken();
                    tokenizer.advance();
                }else{
                    System.out.println("Error invalid parameter");
                    return ;
                }
                if(tokenizer.tokenType() == Constants.SYMBOL){
                    if(tokenizer.symbol().equals(")")){
                        break;
                    }
                    tokenizer.advance();
                }else{
                    System.out.println("Error invalid parameter");
                    return ;
                }
                symbolTable.define(argName,argType,Kind.ARG);
            }

            if(tokenizer.tokenType() == Constants.SYMBOL){
                tokenizer.advance();
            }else{
                System.out.println("Error ,there's no symbol )");
                return ;
            }

            if(tokenizer.tokenType() == Constants.SYMBOL){
                tokenizer.advance();
            }else{
                System.out.println("Error ,there's no symbol {");
                return ;
            }
            // entered the function
            compileVarDecs();
            writer.writeFunctions(className + "." +funName,nLocals);
            compileStatements(); // heeeeeeeeeeeeeeeeeeeeeeeeeeeeeere
            if(tokenizer.tokenType() == Constants.SYMBOL){
                tokenizer.advance();
            }else{
                System.out.println("Error ,there's no symbol }");
                return ;
            }
        }
        System.out.println("there is no more functions" + tokenizer.keyword());
    }
    
    private void compileVarDecs() throws IOException {
        while(tokenizer.keyword() == Constants.VAR){
            nLocals ++;
            tokenizer.advance();
            String varType = "";
            String varName = "";
            if(tokenizer.keyword() == Constants.CHAR
            || tokenizer.keyword() == Constants.BOOLEAN
            || tokenizer.tokenType() == Constants.IDENTIFIER
            || tokenizer.keyword() == Constants.INT){
                varType = tokenizer.getToken();
                tokenizer.advance();
            }else{
                System.out.println("Error in the datatype inside function");
                return ;
            }

            if(tokenizer.tokenType() == Constants.IDENTIFIER){
                varName = tokenizer.getToken();
                tokenizer.advance();
            }else{
                System.out.println("Error in the name of a function");
                return ;
            }

            symbolTable.define(varName,varType,Kind.VAR);
            while (tokenizer.tokenType() == Constants.SYMBOL) {
                if(tokenizer.getToken().equals(";")){
                    tokenizer.advance();
                    break;
                }else if(tokenizer.getToken().equals(",")) {
                    tokenizer.advance();
                    if(tokenizer.tokenType() == Constants.IDENTIFIER) {
                        varName = tokenizer.getToken();
                        symbolTable.define(varName, varType, Kind.VAR);
                        tokenizer.advance();
                    }else{
                        System.out.println("ERROR in variable name");
                        return;
                    }
                    tokenizer.advance();
                }
                else {
                    System.out.println("ERROR in semicolon");
                    return;
                }

            }
        }
    }
    
    private void compileStatements() throws IOException{

        while(tokenizer.keyword() == Constants.RETURN
        || tokenizer.keyword() == Constants.DO
        || tokenizer.keyword() == Constants.LET
        || tokenizer.keyword() == Constants.IF
        || tokenizer.keyword() == Constants.WHILE) {

            if (tokenizer.keyword() == Constants.RETURN) { //done
                compileReturnStatement();
            } else if (tokenizer.keyword() == Constants.DO) { // heeeeeeeeeeeere
                compileDoStatement();
            } else if (tokenizer.keyword() == Constants.LET) { // let statement
                compileLetStatement();
            } else if (tokenizer.keyword() == Constants.IF) { // if statement
                compileIfStatement();
            } else if (tokenizer.keyword() == Constants.WHILE) { // while statement
                compileWhileStatement();
            } else {
                System.out.println("Invalid statement");
                return;
            }
        }
    }
    
    private void compileWhileStatement() throws IOException{
        tokenizer.advance();
        if(tokenizer.tokenType() == Constants.SYMBOL){
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol (");
            return ;
        }
        int WhileNum = ++countWhile;
        writer.writeLabel("WHILE_EXP" + WhileNum);
        compileExpression();
        writer.writeArithmetic(Command.NOT);
        writer.writeIf("WHILE_END" + WhileNum);
        if(tokenizer.tokenType() == Constants.SYMBOL){
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol )");
            return ;
        }
        if(tokenizer.tokenType() == Constants.SYMBOL){
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol {");
            return ;
        }
        compileStatements();
        writer.writeGoto("WHILE_EXP" + WhileNum);
        writer.writeLabel("WHILE_END" + WhileNum);
        if(tokenizer.tokenType() == Constants.SYMBOL){
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol }");
            return ;
        }
    }
    
    private void compileIfStatement() throws IOException{
        tokenizer.advance();
        
        if(tokenizer.tokenType() == Constants.SYMBOL){
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol (");
            return ;
        }
        compileExpression();
        int IFnum = ++ countIF;
        writer.writeIf("IF_TRUE" + IFnum);
        writer.writeGoto("IF_FALSE" + IFnum);
        if(tokenizer.tokenType() == Constants.SYMBOL){
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol )");
            return ;
        }
        if(tokenizer.tokenType() == Constants.SYMBOL){
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol {");
            return ;
        }
        writer.writeLabel("IF_TRUE" + IFnum);
        compileStatements();
        if(tokenizer.tokenType() == Constants.SYMBOL){
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol }");
            return ;
        }
        writer.writeGoto("IF_END" + IFnum);
        writer.writeLabel("IF_FALSE" + IFnum);
        // else statement
        if(tokenizer.keyword() == Constants.ELSE){
            compileElseStatement();
        }
        writer.writeLabel("IF_END" + IFnum);
    }
    
    private void compileElseStatement() throws IOException{
        tokenizer.advance();
        if(tokenizer.tokenType() == Constants.SYMBOL
            && tokenizer.symbol().equals("{")){
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol {");
            return ;
        }
        compileStatements();
        if(tokenizer.tokenType() == Constants.SYMBOL
            && tokenizer.symbol().equals("}")){
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol }");
            return ;
        }
    }
    
    private void compileDoStatement() throws IOException {
        String functionName = "";
        tokenizer.advance();
        if(tokenizer.tokenType() == Constants.IDENTIFIER) {
            functionName += tokenizer.getToken();
            tokenizer.advance();
        } else {
            System.out.println("it should be an identifier after do keywords");
            return ;
        }

        if(tokenizer.symbol().equals("[")) {
            //output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            compileExpression();
            if(tokenizer.tokenType() == Constants.SYMBOL
                && tokenizer.symbol().equals("]")) {
                //output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            } else {
                System.out.println("Error invalid parameter");
                return ;
            }
        }

        if(tokenizer.tokenType() == Constants.SYMBOL) {
            String symb = new String(tokenizer.symbol());
            tokenizer.advance();
            if(symb.equals(".")){
                functionName += '.';
                if(tokenizer.tokenType() == Constants.IDENTIFIER) {
                    functionName += tokenizer.getToken();
                    tokenizer.advance();
                }else{
                    System.out.println("Error in the name of a method");
                    return ;
                }
                if(tokenizer.tokenType() == Constants.SYMBOL){
                    tokenizer.advance();
                }else{
                    System.out.println("Expected: open bracket '(' ");
                    return ;
                }
            }else if(!symb.equals("(")) {
                System.out.println("Expected: open bracket '(' ");
                return ;
            }
        } else {
            System.out.println("Expected a symbol after the identifier '.' or '('"); // mouse.press() or press()
            return ;
        }
        
        int numParameters = compileExpressionList();
        writer.writeCall(functionName, numParameters);
        writer.writePop(Kind.TEMP, 0);
        if(tokenizer.tokenType() == Constants.SYMBOL) {
            tokenizer.advance();
        }else{
            System.out.println("Expected: the symbol )");
            return ;
        }

        if(tokenizer.tokenType() == Constants.SYMBOL) {
            tokenizer.advance();
        }else {
            System.out.println("Expected the symbol ;");
            return ;
        }
    }
    
    private void compileLetStatement() throws IOException{
        tokenizer.advance();
        String variableName;
        if(tokenizer.tokenType() == Constants.IDENTIFIER){
            variableName = tokenizer.getToken();
            tokenizer.advance();
        }else{
            System.out.println("the identifier is invalid");
            return ;
        }
        if(symbolTable.IndexOf(variableName) == -1){
            System.out.println("Error " + variableName + "is not defined");
            return;
        }
        if(tokenizer.symbol().equals("[")){
            tokenizer.advance();
            compileExpression();
            if(tokenizer.tokenType() == Constants.SYMBOL
            && tokenizer.symbol().equals("]")){
                tokenizer.advance();
            }else{
                System.out.println("Error invalid parameter");
                return ;
            }
        }
        if(tokenizer.tokenType() == Constants.SYMBOL
                && tokenizer.symbol().equals("=")){// the assignment symbol
            tokenizer.advance();
        }else{
            System.out.println("Error in the = inside function");
            return ;
        }
        compileExpression();
        writer.writePop(symbolTable.KindOf(variableName), symbolTable.IndexOf(variableName));
        if(tokenizer.tokenType() == Constants.SYMBOL
                && tokenizer.symbol().equals(";")) {
            tokenizer.advance();
        }else{
            System.out.println("Error in the semicolon inside function");
            return ;
        }
    }

    private void compileReturnStatement() throws IOException {
        tokenizer.advance();
        if(tokenizer.tokenType() == Constants.SYMBOL && tokenizer.symbol().equals(";")){
            writer.writePush(Constants.CONSTANT,0);
            writer.writeReturn();
            tokenizer.advance();
        }else if(tokenizer.tokenType() == Constants.IDENTIFIER
                || tokenizer.tokenType() == Constants.KEYWORD){
            compileExpression();
            if(tokenizer.tokenType() == Constants.SYMBOL && tokenizer.symbol().equals(";")){
                writer.writeReturn();
                tokenizer.advance();
            }else{
                System.out.println("Error in the semicolon inside return");
                return ;
            }
        }else{
            System.out.println("Error in the semicolon inside return");
            return ;
        }
    }

    private int compileExpressionList() throws IOException{

        int cnt = 0;
        while(tokenizer.tokenType() != Constants.SYMBOL
                || tokenizer.symbol().equals("-") || tokenizer.symbol().equals("~") || tokenizer.symbol().equals("(")) {

            compileExpression();
            cnt++;

            if(tokenizer.tokenType() == Constants.SYMBOL) {
                if(tokenizer.symbol().equals(")")){
                    break;
                }
                tokenizer.advance();
            }else{
                System.out.println("Expected: a symbol ',' ");
                return -1;
            }
        }
        return cnt;
    }

    private void compileArithmetic(String symbol) throws IOException {
        if(symbol.equals("+")){ writer.writeArithmetic(Command.ADD); }
        else if(symbol.equals("*")){ writer.writeArithmetic(Command.MULT);}
        else if(symbol.equals("-")){ writer.writeArithmetic(Command.SUB);}
        else if(symbol.equals("/")){ writer.writeArithmetic(Command.DIV);}
        else if(symbol.equals("|")){ writer.writeArithmetic(Command.OR);}
        else if(symbol.equals("&amp")){ writer.writeArithmetic(Command.AND);}
        else if(symbol.equals("&lt")){ writer.writeArithmetic(Command.LT);}
        else if(symbol.equals("&gt")){ writer.writeArithmetic(Command.GT);}
        else if(symbol.equals("=")){ writer.writeArithmetic(Command.EQ);}
    }

    private void compileExpression() throws IOException {
        compileTerm();
        while (tokenizer.symbol().equals("+") || tokenizer.symbol().equals("-") 
            || tokenizer.symbol().equals("*") || tokenizer.symbol().equals("/") ||
            tokenizer.symbol().equals("&amp;") || tokenizer.symbol().equals("|") || 
            tokenizer.symbol().equals("&lt;") || tokenizer.symbol().equals("&gt;") ||
            tokenizer.symbol().equals("=")) {
            String sym = tokenizer.symbol();
            tokenizer.advance();
            compileTerm();
            compileArithmetic(sym);
        }
    }

    private void compileTerm() throws IOException {
        if(tokenizer.tokenType() == Constants.IDENTIFIER
            || tokenizer.tokenType() == Constants.KEYWORD) {
            String functionName = tokenizer.getToken();
            tokenizer.advance();
            if(tokenizer.tokenType() == Constants.SYMBOL) {
                String sym = new String(tokenizer.symbol());
                if(sym.equals(".")) {
                    functionName += '.';
                    tokenizer.advance();
                    if(tokenizer.tokenType() == Constants.IDENTIFIER) {
                        functionName += tokenizer.getToken();
                        tokenizer.advance();
                    }else{
                        System.out.println("Error in the name of a fun");
                        return ;
                    }

                    if(tokenizer.tokenType() == Constants.SYMBOL){
                        tokenizer.advance();
                    }else{
                        System.out.println("Expected: symbol '(' ");
                        return ;
                    }

                    int numParameters = compileExpressionList();
                    writer.writeCall(functionName, numParameters);
                    if(tokenizer.tokenType() == Constants.SYMBOL){
                        tokenizer.advance();
                    }else{
                        System.out.println("Expected: symbol ')' ");
                        return ;
                    }
                } else if(sym.equals("[")){
                    tokenizer.advance();
                    compileExpression();
                    if(tokenizer.symbol().equals("]")){
                        tokenizer.advance();
                    }else{
                        System.out.println("Error in ] ");
                        return ;
                    }
                } else if(sym.equals("(")) {
                    int numParameters = compileExpressionList();
                    writer.writeCall(functionName, numParameters);
                    if(tokenizer.tokenType() == Constants.SYMBOL){
                        tokenizer.advance();
                    }else{
                        System.out.println("Expected: symbol ')' ");
                        return ;
                    }
                } else {
                    String variableName = functionName;
                    writer.writePush(symbolTable.KindOf(variableName), symbolTable.IndexOf(variableName));
                }
            }
        }else if(tokenizer.tokenType() == Constants.STRING_CONST){
            // output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else if(tokenizer.tokenType() == Constants.INT_CONST) {
            writer.writePush(Constants.CONSTANT, Integer.valueOf(tokenizer.getToken()));
            tokenizer.advance();
        }else if(tokenizer.tokenType() == Constants.SYMBOL
                && tokenizer.symbol().equals("(")) {
            tokenizer.advance();
            compileExpression();
            if(tokenizer.symbol().equals(")")){
                tokenizer.advance();
            }else{
                System.out.println("Error in )");
                return ;
            }
        }else if(tokenizer.tokenType() == Constants.SYMBOL
                && (tokenizer.symbol().equals("-") || tokenizer.symbol().equals("~"))) {
            String sym = tokenizer.symbol();
            tokenizer.advance();
            compileTerm();
            if(sym.equals("-"))
                writer.writeArithmetic(Command.NEG);
            else
                writer.writeArithmetic(Command.NOT);
        }
    }
    
    public void close() throws IOException{
        tokenizer.close();
        writer.close();
    }
}
