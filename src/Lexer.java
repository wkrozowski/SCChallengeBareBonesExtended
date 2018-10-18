import java.util.*;
import java.io.*;
import java.lang.Object;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            case "printval":
                return new VariableOperation(3,arguments[1],localBB); //decrease
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
    //parses an expression on values into tree of Value objects
    public Value parseValue(String expressionToParse) {
        Value resultBlock = null;
        //remove all whitespaces
        expressionToParse=expressionToParse.replaceAll("\\s", "");

        String firstPattern = new String("(==)|(!=)|(>=)|(>)|(<=)|(<)|(Not)");
        String isFirstPattern = new String(".*(==).*|.*(!=).*|.*(>).*|.*(>=).*|.*(<).*|.*(<=).*|.*(Not).*");
        String secondPattern = new String("[*]|[/]");
        String isSecondPattern = new String(".*[*].*|.*[/].*");
        String thirdPattern = new String("[+]|[-]");
        String isThirdPattern = new String(".*[+].*|.*[-].*");

        if(expressionToParse.matches(isFirstPattern)) {
            String [] parts = expressionToParse.split(firstPattern);
            String leftHandSide = parts[0];
            String rightHandSide = parts[1];

            //evaluate both sides
            Value leftValue = parseValue(leftHandSide);
            Value rightValue = parseValue(rightHandSide);

            //check the type of operation
            String operation = expressionToParse.substring(leftHandSide.length());
            operation = operation.substring(0,operation.length()-rightHandSide.length());
            int operationType=-1;
            switch(operation){
                case "==":
                    operationType=0;
                    break;
                case ">":
                    operationType=1;
                    break;
                case ">=":
                    operationType=2;
                    break;
                case "<":
                    operationType=3;
                    break;
                case "<=":
                    operationType=4;
                    break;
                case "!=":
                    operationType=9;
                    break;
                case "Not":
                    operationType=9;
                    break;
                default:
                    localBB.panic("ERROR");
            }
            resultBlock = new OperationValue(operationType, localBB);
            resultBlock.assignLeft(leftValue);
            resultBlock.assignRight(rightValue);
            return resultBlock;

        } else {
            if(expressionToParse.matches(isThirdPattern)){
                String [] parts = expressionToParse.split(thirdPattern);
                String leftHandSide = parts[0];
                String rightHandSide = expressionToParse.substring(leftHandSide.length(), expressionToParse.length());

                //extract operation
                String operation = rightHandSide.substring(0,1);
                int operationType=-1;
                switch(operation) {
                    case "+":
                        operationType=5;
                        break;
                    case "-":
                        operationType=6;
                        break;
                    default:
                        localBB.panic("ERROR!");
                        break;
                }
                //System.out.println(operationType);
                rightHandSide=rightHandSide.substring(1,rightHandSide.length());
                Value leftValue = parseValue(leftHandSide);
                Value rightValue = parseValue(rightHandSide);
                resultBlock = new OperationValue(operationType, localBB);
                resultBlock.assignLeft(leftValue);
                resultBlock.assignRight(rightValue);
                return resultBlock;
            }
            else {
                if(expressionToParse.matches(isSecondPattern)) {
                    String [] parts = expressionToParse.split(secondPattern);
                    String leftHandSide = parts[0];
                    String rightHandSide = expressionToParse.substring(leftHandSide.length(), expressionToParse.length());

                    //extract operation
                    String operation = rightHandSide.substring(0,1);
                    int operationType=-1;
                    switch(operation) {
                        case "*":
                            operationType=7;
                            break;
                        case "/":
                            operationType=8;
                            break;
                        default:
                            localBB.panic("ERROR!");
                            break;
                    }
                    rightHandSide=rightHandSide.substring(1, rightHandSide.length());
                    Value leftValue = parseValue(leftHandSide);
                    Value rightValue = parseValue(rightHandSide);
                    resultBlock = new OperationValue(operationType, localBB);
                    resultBlock.assignLeft(leftValue);
                    resultBlock.assignRight(rightValue);
                    return resultBlock;


                }
                else {
                    if(expressionToParse.matches("\\d*")) {
                        int value = Integer.parseInt(expressionToParse);
                        resultBlock = new ConstantValue(value, localBB);
                        return resultBlock;
                    }
                    else {
                        resultBlock = new VariableValue(expressionToParse, localBB);
                        return resultBlock;
                    }
                }

            }
        }
    }
}
