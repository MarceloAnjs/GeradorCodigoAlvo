package codewriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private BufferedWriter writer;

    public CodeWriter(String outputFileName) {
        try {
            writer = new BufferedWriter(new FileWriter(outputFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeArithmetic(String command) {

    }

    public void writePush(String segment, int index) {

    }

    public void writePop(String segment, int index) {

    }

    private boolean isValidSegment(String segment) {

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