package codewriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CodeWriter {
    private BufferedWriter writer;
    private static final Set<String> VALID_SEGMENTS = new HashSet<>();

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