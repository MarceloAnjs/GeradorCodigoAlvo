package writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CodeWriter {
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

    public void writeArithmetic(String command) {
        writeLine("@SP");
        writeLine("AM=M-1");
        writeLine("D=M");
        writeLine("A=A-1");

        if (command.equals("add")) {
            writeLine("M=M+D");
        } else if (command.equals("sub")) {
            writeLine("M=M-D");
        } else {
            throw new IllegalArgumentException("Unsupported arithmetic command: " + command);
        }
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
