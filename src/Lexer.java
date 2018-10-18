import java.util.*;
import java.io.*;
import java.lang.Object;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    BB localBB;
    ValueParser localValueParser;
    public Lexer (BB initialBB, ValueParser initialValueParser) {
        localBB = initialBB;
        localValueParser = initialValueParser;
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
        line=line.replaceAll("\\s?[//].*", "");
        if(line.isEmpty()) {
            return analiseLine(localBB.getSourceLine());
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
            case "printval":
                return new VariableOperation(3,arguments[1],localBB); //decrease
            case "while":
                //in case of while, create new executable block from all parsed lines to the end block
                line = line.replaceAll("[while]\\s*", "");
                line = line.replaceAll("do\\s?", "");
                Value parsedValue = localValueParser.parseValue(line);
                ExecutableBlock loopExecutionFlow = parse();

                //assign loop execution flow, to the loop block and return block
                return new LoopExpression(parsedValue, loopExecutionFlow, localBB);
            case "set":
                line = line.replaceAll("set","");

                String variableIdentifier = line.replaceAll("^\\s?","");
                variableIdentifier = line.replaceAll("to\\s?.*$","");
                variableIdentifier = variableIdentifier.replaceAll("\\s?","");

                line = line.substring(variableIdentifier.length()+1, line.length());

                line = line.replaceAll("to","");

                Value parsedValueToAssign = localValueParser.parseValue(line);
                return new AssignExpression(variableIdentifier, parsedValueToAssign, localBB);
            case "if":
                line = line.replaceAll("if","");
                line = line.replaceAll("do","");
                Value parsedConditionValue = localValueParser.parseValue(line);
                ExecutableBlock conditionFlow = parse();
                return new IfExpression(parsedConditionValue, conditionFlow, localBB);

            case "end":
                return null;
        }
        localBB.panic("Not recognized statement");
        return null;
    }
}
