import java.util.*;
import java.io.*;

public class Lexer {
    BB localBB;
    public Lexer (BB initialBB)
    {
        localBB = initialBB;
    }

    public ExecutableBlock parse() {
        ExecutableBlock parsedBlock = new ExecutableBlock(localBB); //create new empty executable block
        Expr currentBlock; //create block structure
        currentBlock = analiseLine(localBB.getSourceLine()); //set block structure to block parsed from current source line

        while((currentBlock !=null)) //while block is not null(null, when end of program or if statement)
        {
            parsedBlock.addNewBlock(currentBlock); //add new block to master executable block
            currentBlock = analiseLine(localBB.getSourceLine()); //get new block
        }

        return parsedBlock; //return master executable block
    }

    public Expr analiseLine(String line) {
        Expr analisedExpression;

        //if line is empty (end of program) return null block
        if(line.isEmpty())
        {
            return null;
        }

        //check semicolon
        if(line.charAt(line.length()-1)!=';') {
            localBB.panic("Missing semicolon");
        }

        line = line.substring(0, line.length()-1); //remove semicolon
        String[] arguments = line.split(" ", 5); //split into arrays of string

        //create apropriate block for current line
        switch(arguments[0]) {
            case "clear":
                return new VariableOperation(0,arguments[1],localBB); //clear
            case "incr":
                return new VariableOperation(1,arguments[1],localBB); //increase
            case "decr":
                return new VariableOperation(2,arguments[1],localBB); //decrease
            case "while":
                //in case of while, create new executable block from all parsed lines to the end block
                ExecutableBlock loopExecutionFlow = parse();
                Integer valueNotEqual = Integer.parseInt(arguments[3]);
                //assign loop execution flow, to the loop block and return block
                return new LoopExpression(arguments[1], valueNotEqual, loopExecutionFlow, localBB);
            case "end":
                return null;
        }
        localBB.panic("Not recognized statement");
        return null;
    }
}
