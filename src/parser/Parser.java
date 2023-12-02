package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    
    private Scanner scanner;
    private String[] currentCommand;
    private final String C_PUSH = "C_PUSH";
    private final String C_POP = "C_POP";
    private final String C_ARITHMETIC = "C_ARITHMETIC";
    private final String C_GOTO = "C_GOTO";
    private final String C_LABEL = "C_LABEL";
    private final String C_IF = "C_IF";
    private final String C_RETURN = "C_RETURN";
    private final String C_FUNCTION = "C_FUNCTION";
    private final String C_CALL = "C_CALL";

    public Parser(String filePath) {
        File fileToParse = new File(filePath);
        try {
            this.scanner = new Scanner(fileToParse);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean hasMoreCommands() {
        if (scanner.hasNextLine()) {
            return true;
        }
        scanner.close();
        return false;
    }
}