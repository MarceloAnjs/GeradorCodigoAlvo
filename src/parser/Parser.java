package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private BufferedReader reader;
    private List<String[]> tokens;

    public Parser(String filename) {
        try {
            reader = new BufferedReader(new FileReader(filename));
            tokens = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("//") && !line.trim().equals("")) {
                    tokens.add(line.split("\\s+"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] command() {
        return tokens.remove(0);
    }

    public boolean hasMoreCommands() {
        return !tokens.isEmpty();
    }
}