import java.io.File;

import writer.CodeWriter;
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
            } else if (commandType.equals("eq")) {
                codeWriter.writeEquality();
            } else if (commandType.equals("lt")) {
                codeWriter.writeLessThan();
            } else if (commandType.equals("gt")) {
                codeWriter.writeGreaterThan();
            } else if (commandType.equals("and")) {
                codeWriter.writeAnd();
            } else if (commandType.equals("or")) {
                codeWriter.writeOr();
            } else if (commandType.equals("not")) {
                codeWriter.writeNot();
            }
        }

        codeWriter.close();
    }
}
