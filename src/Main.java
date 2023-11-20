import java.io.File;

import codewriter.CodeWriter;
import parser.Parser;

public class Main {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Uso: java Main <arquivo VM>");
            System.exit(1);
        }

        String inputFileName = args[0];

        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            System.err.println("Erro: O arquivo de entrada não existe.");
            System.exit(1);
        }

        String outputFileName = inputFileName.replace(".vm", ".asm");

        Parser p = new Parser(inputFileName);
        CodeWriter codeWriter = new CodeWriter(outputFileName);

        while (p.hasMoreCommands()) {
            String[] command = p.command();
            String commandType = command[0];

            if (commandType.equals("push")) {
                codeWriter.writePush(command[1], Integer.parseInt(command[2]));
            } else if (commandType.equals("pop")) {
                codeWriter.writePop(command[1], Integer.parseInt(command[2]));
            } else if (commandType.equals("add") || commandType.equals("sub")) {
                codeWriter.writeArithmetic(commandType);
            }
        }

        codeWriter.close();
    }
}