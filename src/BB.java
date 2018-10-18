import java.util.*;
import java.io.*;

public class BB {
    //variables list
    public HashMap<String, Integer> variablesList = new HashMap<String,Integer>();
    public File inputFile;
    public Scanner fileInput;

    //methods to handle file
    public void openFile(String fileName) throws FileNotFoundException {
        inputFile = new File(fileName);
        fileInput = new Scanner(inputFile);
    }
    //get next source code line
    public String getSourceLine() {
        if (fileInput.hasNext()) {
            String sourceLine = fileInput.nextLine();
            return sourceLine.trim();
        }
        return ""; //if no source line, return empty string, to be recognized by lexer as end of program
    }

    public void closeFile() {
        fileInput.close();
    }

    public void checkVariable(String variableIdentifier) {
        if (!variablesList.containsKey(variableIdentifier)) {
            panic("Variable " + variableIdentifier + " used but not declared");
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        BB myBB = new BB();
        ValueParser myValueParser = new ValueParser(myBB);
        Lexer myLexer = new Lexer(myBB, myValueParser);

        System.out.println("Welcome to Bare Bones interpreter, enter the path to source code file:");
        Scanner textInput = new Scanner(System.in);
        String path = textInput.nextLine();
        myBB.openFile(path);

        ExecutableBlock root = myLexer.parse(); //create executable node from parsed source code
        root.execute(); //execute the master node
        myBB.closeFile();

    }

    public void panic(String errorMessage) {
        System.out.println("PANIC: " + errorMessage);
        //closeFile();
        System.exit(0);
    }

}
