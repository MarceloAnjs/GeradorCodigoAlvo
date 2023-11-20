import parser.Parser;

public class Main {
    public static void main(String[] args) {
        Parser p = new Parser("src/projects/07/MemoryAccess/BasicTest/BasicTest.vm");
        while (p.hasMoreCommands()) {
            String[] command = p.command();
            for (String token : command) {
                System.out.print(token + " ");
            }
            System.out.println();

        }
    }
}