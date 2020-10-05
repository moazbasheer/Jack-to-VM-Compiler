import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class JackTokenizer {
	private int cursor;
	private boolean insideComment;
	private String fileContent;
	private String currentToken;
	private HashMap<String, String> map; // the token and its type.

	private File file;
	private Scanner scan;
	JackTokenizer(String filename)
			throws IOException {
		insideComment = false;
		file = new File(filename);
		scan = new Scanner(file);
		currentToken = "";
		fileContent = "";
		cursor = 0;
		map = new HashMap<>();
		/*writer = new FileWriter(new File(filename.split("[.]")[0] + "T.xml"));
		writer.write("<tokens>\n");*/
		while (scan.hasNext()) {
			String temp = scan.nextLine();
			if(temp.length() > 2 && temp.substring(0,2).equals("//")){
				continue;
			}else if(temp.contains("//")){
				temp = temp.substring(0,temp.indexOf("//"));
			}
			fileContent = fileContent.concat(temp);
		}
		scan = new Scanner(new File(Constants.TABLE_SRC));
		while (scan.hasNext()) {
			String token = scan.next();
			String type = scan.next();
			map.put(token, type);
		}
		
	}

	public boolean hasMoreTokens() throws IOException {
		return currentToken.trim().length() > 0;
	}

	public String getToken() { return currentToken.trim(); }

	public void advance() throws IOException {
		currentToken = "";
		boolean insideString = false;
		while (cursor < fileContent.length()) {
			
			if (!insideComment && fileContent.charAt(cursor) == '/'
					&& fileContent.charAt(cursor + 1) == '*') {
				insideComment = true;
				cursor += 2;
				continue;
			} else if (insideComment && fileContent.charAt(cursor) == '*'
					&& fileContent.charAt(cursor + 1) == '/') {
				insideComment = false;
				cursor += 2;
				continue;
			} else if (insideComment) {
				cursor++;
				continue;
			}
			if (!insideString && fileContent.charAt(cursor) == '"') {
				if(!currentToken.equals("")) break;
				insideString = true;
				currentToken += fileContent.charAt(cursor);
				cursor ++;
				continue;
			}else if (insideString && fileContent.charAt(cursor) == '"') {
				insideString = false;
				currentToken += fileContent.charAt(cursor);
				cursor ++;
				break;
			}else if (insideString) {
				currentToken += fileContent.charAt(cursor);
				cursor++;
				continue;
			}
			if (fileContent.charAt(cursor) == ' '
					|| fileContent.charAt(cursor) == '\t') {
				cursor++;
				continue;
			}
			char tmp = fileContent.charAt(cursor);
			if (map.containsKey(String.valueOf(tmp))) { // symbol
				if (map.get(String.valueOf(tmp)).equals("symbol")
						&& !currentToken.equals("")) {
					break;
				} else if (map.get(String.valueOf(tmp)).equals("symbol")) {
					currentToken = currentToken + String.valueOf(tmp);
					cursor++;
					break;
				}
			}
			currentToken = currentToken + String.valueOf(tmp);
			if (map.containsKey(currentToken)) {
				if (map.get(currentToken).equals("keyword")) {
					cursor++;
					break;
				}
			}
			cursor++;
			if(cursor < fileContent.length() && fileContent.charAt(cursor) == ' '){
				break;
			}
		}
		/*if(currentToken.trim().length() == 0){
			writer.write("</tokens>\n");
		}else{
			writer.write("  " + getTag() + "\n");
		}*/
	}

	private boolean isNumeric(String token) {
		token = token.trim();
		for (int i = 0; i < token.length(); i++) {
			if (token.charAt(i) < '0' || token.charAt(i) > '9') {
				return false;
			}
		}
		return true;
	}

	public int tokenType() {
		currentToken = currentToken.trim();
		int sz = currentToken.length();
		if (map.containsKey(currentToken)) {
			if (map.get(currentToken).equals("keyword")) {
				return Constants.KEYWORD;
			} else if (map.get(currentToken).equals("symbol")) {
				return Constants.SYMBOL;
			}
		} else if (isNumeric(currentToken)) {
			return Constants.INT_CONST;
		} else if (currentToken.charAt(0) == '"'
				&& currentToken.charAt(sz - 1) == '"') {
			return Constants.STRING_CONST;
		}
		
		return Constants.IDENTIFIER;
	}
	public int keyword(){
        if (currentToken.equals("class")) { return Constants.CLASS; }
        else if (currentToken.equals("method")) { return Constants.METHOD; }
        else if (currentToken.equals("function")) { return Constants.FUNCTION; }
        else if (currentToken.equals("constructor")) { return Constants.CONSTRUCTOR; }
        else if (currentToken.equals("int")) { return Constants.INT; }
        else if (currentToken.equals("boolean")) { return Constants.BOOLEAN; }
        else if (currentToken.equals("char")) { return Constants.CHAR; }
        else if (currentToken.equals("void")) { return Constants.VOID; }
        else if (currentToken.equals("var")) { return Constants.VAR; }
        else if (currentToken.equals("static")) { return Constants.STATIC; }
        else if (currentToken.equals("field")) { return Constants.FIELD; }
        else if (currentToken.equals("let")) { return Constants.LET; }
        else if (currentToken.equals("do")) { return Constants.DO; }
        else if (currentToken.equals("if")) { return Constants.IF; }
        else if (currentToken.equals("else")) { return Constants.ELSE; }
        else if (currentToken.equals("while")) { return Constants.WHILE; }
        else if (currentToken.equals("return")) { return Constants.RETURN; }
        else if (currentToken.equals("true")) { return Constants.TRUE; }
        else if (currentToken.equals("false")) { return Constants.FALSE; }
        else if (currentToken.equals("null")) { return Constants.NULL; }
        else if (currentToken.equals("this")) { return Constants.THIS; }
        else { return -1; }
	}
	public String symbol(){
		if (tokenType() != Constants.SYMBOL) { return "Error"; }
        if (currentToken.equals("<")) { return "&lt;"; }
        else if (currentToken.equals(">")) { return "&gt;"; }
        else if (currentToken.equals("&")) { return "&amp;"; }
        else { return new String(currentToken); }
	}
	public String stringVal(){
		if (tokenType() != Constants.STRING_CONST) { return "ERROR"; }
        return currentToken.replace("\"", "");
	}
	public String identifier(){
		if (tokenType() != Constants.IDENTIFIER) { return "ERROR"; }
        return currentToken;
	}
	private String getTypeText(){
		if(tokenType() == Constants.KEYWORD) return "keyword";
		else if(tokenType() == Constants.IDENTIFIER) return "identifier";
		else if(tokenType() == Constants.SYMBOL) return "symbol";
		else if(tokenType() == Constants.INT_CONST) return "integerConstant";
		else if(tokenType() == Constants.STRING_CONST) return "stringConstant";
		return "Error";
	}
	public String getTag(){
		String temp = currentToken;
		if(getTypeText().equals("stringConstant")){
			temp = currentToken.substring(1,currentToken.length() - 1);
		}else if(getTypeText().equals("symbol")){
			temp = this.symbol();
		}
		String text = ("<" + getTypeText() + "> " + temp + " </" + getTypeText() + ">");
		return text;
	}
	public void close() throws IOException{
		scan.close();
	}
}
