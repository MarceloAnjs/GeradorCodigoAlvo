package writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CodeWriter {
    private String fileName;
    private BufferedWriter writer;
    private FileWriter fileWriter;
    private Integer counter = 0;
    private static final Set<String> VALID_SEGMENTS = new HashSet<>();
    private static final Map<String, String> SEGMENT_MAP = new HashMap<>();

    static {
        SEGMENT_MAP.put("local", "LCL");
        SEGMENT_MAP.put("argument", "ARG");
        SEGMENT_MAP.put("this", "THIS");
        SEGMENT_MAP.put("that", "THAT");
        SEGMENT_MAP.put("temp", "5");
        SEGMENT_MAP.put("pointer", "3");
    }

    static {
        VALID_SEGMENTS.add("local");
        VALID_SEGMENTS.add("constant");
        VALID_SEGMENTS.add("temp");
        VALID_SEGMENTS.add("this");
        VALID_SEGMENTS.add("that");
        VALID_SEGMENTS.add("static");
        VALID_SEGMENTS.add("pointer");
        VALID_SEGMENTS.add("argument");
    }

    public void setFileName(String fileName) {
        String tempFilePath = fileName.replace("\\", "/");
        String[] fileNameTemp = tempFilePath.split("/");
        fileNameTemp[fileNameTemp.length - 1] = fileNameTemp[fileNameTemp.length - 1].replace(" ", "_");
        this.fileName = fileNameTemp[fileNameTemp.length - 1];
    }

    private void writeTofile(String commandString) {
        try {
            fileWriter.write(commandString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInit() {
        String commandString = "@256\r\nD=A\r\n@SP\r\nM=D\r\n";
        writeTofile(commandString);
        writeCall("Sys.init", "0");
    }

    public void writeCall(String functionName, String numArgs) {
        String returnLabel = functionName + "$ret." + counter++;
        String string = "//call\n@" + returnLabel.toUpperCase() + "\r\nD=A\r\n@SP\r\nA=M\r\nM=D\r\n@SP\r\nM=M+1\r\n";
        string += "@LCL\r\nD=M\n@SP\nA=M\nM=D\n@SP\r\nM=M+1\r\n";
        string += "@ARG\nD=M\n@SP\nA=M\nM=D\n@SP\r\nM=M+1\r\n";
        string += "@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\r\nM=M+1\r\n";
        string += "@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\r\nM=M+1\r\n";
        string += "@SP\r\nD=M\r\n@5\r\nD=D-A\r\n@" + numArgs + "\r\nD=D-A\r\n@ARG\r\nM=D\r\n";
        string += "@SP\r\nD=M\r\n@LCL\r\nM=D\r\n";
        writeTofile(string);
        functionName = functionName.replace(".vm", String.valueOf(counter++));
        writeTofile("//goto\n");

    }

    public CodeWriter(String outputFileName) {
        try {
            writer = new BufferedWriter(new FileWriter(outputFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String binaryArithmetic1(String operation) {
        return "@SP\r\nAM=M-1\r\nD=M\r\n@SP\r\nA=M-1\r\nM=M" + operation + "D\r\n";
    }

    private String binaryArithmetic2(String operation) {
        return "@SP\r\nAM=M-1\r\nD=M\r\n@SP\r\nA=M-1\r\nM=D" + operation + "M\r\n";
    }

    private String unaryArithmetic(String operation) {
        return "@SP\r\nA=M-1\r\nM=" + operation + "M\r\n";
    }

    private String compareArithmetic(String comp) {
        return "@SP\r\nAM=M-1\r\nD=M\r\n@SP\r\nAM=M-1\r\nD=M-D\r\n@IS_GT_OR_LT_" + counter + "\r\nD;"
                + comp + "\r\n@SP\r\nA=M\r\nM=0\r\n@FINISH_" + counter + "\r\n0;JMP\r\n(IS_GT_OR_LT_" + counter
                + ")\r\n@SP\r\n" +
                "A=M\r\nM=-1\r\n(FINISH_" + counter++ + ")\r\n@SP\r\nM=M+1\r\n";
    }

    public void WriteArithmetic(String command) {
        String commandString;
        switch (command) {
            case "sub":
                commandString = binaryArithmetic1("-");
                break;
            case "add":
                commandString = binaryArithmetic2("+");
                break;
            case "neg":
                commandString = unaryArithmetic("-");
                break;
            case "eq":
                commandString = compareArithmetic("JEQ");
                break;
            case "gt":
                commandString = compareArithmetic("JGT");
                break;
            case "lt":
                commandString = compareArithmetic("JLT");
                break;
            case "and":
                commandString = binaryArithmetic2("&");
                break;
            case "or":
                commandString = binaryArithmetic2("|");
                break;
            case "not":
                commandString = unaryArithmetic("!");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + command);
        }
        writeTofile(commandString);
    }

    public void writeEquality() {
        writeComparison("JEQ");
    }

    public void writeLessThan() {
        writeComparison("JLT");
    }

    public void writeGreaterThan() {
        writeComparison("JGT");
    }

    public void writeAnd() {
        writeLine(" @SP");
        writeLine(" AM=M-1");
        writeLine(" D=M");
        writeLine(" A=A-1");
        writeLine(" M=M&D");
    }

    public void writeOr() {
        writeLine(" @SP");
        writeLine(" AM=M-1");
        writeLine(" D=M");
        writeLine(" A=A-1");
        writeLine(" M=M|D");
    }

    public void writeNot() {
        writeLine(" @SP");
        writeLine(" A=M-1");
        writeLine(" M=!M");
    }

    private void writeComparison(String jumpType) {
        int jumpIndex = getJumpIndex();
        writeLine(" @SP");
        writeLine(" AM=M-1");
        writeLine(" D=M");
        writeLine(" A=A-1");
        writeLine(" D=M-D");
        writeLine(" @TRUE" + jumpIndex);
        writeLine(" D;" + jumpType);
        writeLine(" @SP");
        writeLine(" A=M-1");
        writeLine(" M=0");
        writeLine(" @CONTINUE" + jumpIndex);
        writeLine(" 0;JMP");
        writeLine("(TRUE" + jumpIndex + ")");
        writeLine(" @SP");
        writeLine(" A=M-1");
        writeLine(" M=-1");
        writeLine("(CONTINUE" + jumpIndex + ")");
    }

    private int jumpIndex = 0;

    private int getJumpIndex() {
        return jumpIndex++;
    }

    public void writePush(String segment, int index) {
        if (!isValidSegment(segment)) {
            throw new IllegalArgumentException("Unsupported pop segment: " + segment);
        }
        if (segment.equals("constant")) {
            writeLine("@" + index);
            writeLine("D=A");
            writeLine("@SP");
            writeLine("A=M");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("M=M+1");
        } else if (segment.equals("local")) {
            writeLine("@LCL");
            writeLine("D=M");
            writeLine("@" + index);
            writeLine("A=D+A");
            writeLine("D=M");
            writeLine("@SP");
            writeLine("A=M");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("M=M+1");
        } else if (segment.equals("argument")) {
            writeLine("@ARG");
            writeLine("D=M");
            writeLine("@" + index);
            writeLine("A=D+A");
            writeLine("D=M");
            writeLine("@SP");
            writeLine("A=M");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("M=M+1");
        } else if (segment.equals("this")) {
            writeLine("@THIS");
            writeLine("D=M");
            writeLine("@" + index);
            writeLine("A=D+A");
            writeLine("D=M");
            writeLine("@SP");
            writeLine("A=M");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("M=M+1");
        } else if (segment.equals("that")) {
            writeLine("@THAT");
            writeLine("D=M");
            writeLine("@" + index);
            writeLine("A=D+A");
            writeLine("D=M");
            writeLine("@SP");
            writeLine("A=M");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("M=M+1");
        } else if (segment.equals("temp")) {
            writeLine("@5");
            writeLine("D=A");
            writeLine("@" + index);
            writeLine("A=D+A");
            writeLine("D=M");
            writeLine("@SP");
            writeLine("A=M");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("M=M+1");
        } else if (segment.equals("pointer")) {
            writeLine("@3");
            writeLine("D=A");
            writeLine("@" + index);
            writeLine("A=D+A");
            writeLine("D=M");
            writeLine("@SP");
            writeLine("A=M");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("M=M+1");
        } else if (segment.equals("static")) {
            writeLine("@" + index);
            writeLine("D=M");
            writeLine("@SP");
            writeLine("A=M");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("M=M+1");
        }
    }

    public void writePop(String segment, int index) {
        if (!isValidSegment(segment)) {
            throw new IllegalArgumentException("Unsupported pop segment: " + segment);
        }
        if (segment.equals("local")) {
            writeLine("@LCL");
            writeLine("D=M");
            writeLine("@" + index);
            writeLine("D=D+A");
            writeLine("@R13");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("AM=M-1");
            writeLine("D=M");
            writeLine("@R13");
            writeLine("A=M");
            writeLine("M=D");
        } else if (segment.equals("argument")) {
            writeLine("@ARG");
            writeLine("D=M");
            writeLine("@" + index);
            writeLine("D=D+A");
            writeLine("@R13");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("AM=M-1");
            writeLine("D=M");
            writeLine("@R13");
            writeLine("A=M");
            writeLine("M=D");
        } else if (segment.equals("this")) {
            writeLine("@THIS");
            writeLine("D=M");
            writeLine("@" + index);
            writeLine("D=D+A");
            writeLine("@R13");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("AM=M-1");
            writeLine("D=M");
            writeLine("@R13");
            writeLine("A=M");
            writeLine("M=D");
        } else if (segment.equals("that")) {
            writeLine("@THAT");
            writeLine("D=M");
            writeLine("@" + index);
            writeLine("D=D+A");
            writeLine("@R13");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("AM=M-1");
            writeLine("D=M");
            writeLine("@R13");
            writeLine("A=M");
            writeLine("M=D");
        } else if (segment.equals("temp")) {
            writeLine("@5");
            writeLine("D=A");
            writeLine("@" + index);
            writeLine("D=D+A");
            writeLine("@R13");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("AM=M-1");
            writeLine("D=M");
            writeLine("@R13");
            writeLine("A=M");
            writeLine("M=D");
        } else if (segment.equals("pointer")) {
            writeLine("@3");
            writeLine("D=A");
            writeLine("@" + index);
            writeLine("D=D+A");
            writeLine("@R13");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("AM=M-1");
            writeLine("D=M");
            writeLine("@R13");
            writeLine("A=M");
            writeLine("M=D");
        } else if (segment.equals("static")) {
            writeLine("@" + index);
            writeLine("D=A");
            writeLine("@R13");
            writeLine("M=D");
            writeLine("@SP");
            writeLine("AM=M-1");
            writeLine("D=M");
            writeLine("@R13");
            writeLine("A=M");
            writeLine("M=D");
        }
    }

    private boolean isValidSegment(String segment) {
        return VALID_SEGMENTS.contains(segment);
    }

    private void writeLine(String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
