public class ValueParser {
    public BB localBB;
    public ValueParser(BB initialBB) {
        localBB = initialBB;
    }
    public Value parseValue(String expressionToParse) {
        Value resultBlock = null;
        //remove all whitespaces
        expressionToParse=expressionToParse.replaceAll("\\s", "");

        String firstPattern = new String("(==)|(!=)|(>=)|(>)|(<=)|(<)|(not)");
        String isFirstPattern = new String(".*(==).*|.*(!=).*|.*(>).*|.*(>=).*|.*(<).*|.*(<=).*|.*(not).*");
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
                case "not":
                    operationType=9;
                    break;
                default:
                    localBB.panic("ERROR");
            }
            resultBlock = new OperationValue(operationType, localBB);
            resultBlock.assignLeft(leftValue);
            resultBlock.assignRight(rightValue);
            return resultBlock;
            //case for addition and substraction
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
                //case for multiplication and division
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
