import java.util.*;
import java.io.*;

//null block, parent type
public class Expr {
    public BB localBB;
    public int type = 0;
    public void execute() {
        localBB.panic("executing null block");
    } //parent execute method
    public void addNewBlock(Expr expressionToAdd) {

    }
}
//possible values of type variable
//0 - empty block
//1 - executable block
//2 - operation on variable
//3 - loops operation
//4 - if operation
//5 - assign operation

//executable block, a list of blocks
class ExecutableBlock extends Expr {
    public List<Expr> blocks;
    public ExecutableBlock(BB usedBB)
    {
        type = 1;
        localBB = usedBB;
        blocks = new Vector<Expr>();
    }
    public void execute() {
        Iterator<Expr> iterator = blocks.iterator();
        while(iterator.hasNext()) {
            Expr nextBlock = iterator.next(); //iterate through list of blocks and execute them
            nextBlock.execute();
        }
    }
    public void addNewBlock(Expr expressionToAdd) {
        blocks.add(expressionToAdd); //add new block to block list
    }

}

class VariableOperation extends Expr {
    public int operationType; //Possible values of operation type: 0 - clear 1 - increase 2- decrease 3-print value
    public String operationVariableIdentifier; //Variable identifier

    //constructor method
    public VariableOperation(int initialType, String initialVariableIdentifier, BB usedBB) {
        localBB = usedBB;
        type = 2;
        operationType = initialType;
        operationVariableIdentifier = initialVariableIdentifier;
    }
    public void execute(){
        if (operationType == 0) {
            //if clearing variable no need to check whether it exists
            localBB.variablesList.put(operationVariableIdentifier, new Integer(0));
        }
        else {
            //in other cases, first check the variable list (HashMap), whether variable exists
            localBB.checkVariable(operationVariableIdentifier);
            int currentValue = localBB.variablesList.get(operationVariableIdentifier);
            switch(operationType) {
                case 1:
                    currentValue++;
                    localBB.variablesList.put(operationVariableIdentifier, new Integer(currentValue));
                    break;
                case 2:
                    //same case as with increase
                    currentValue--;
                    localBB.variablesList.put(operationVariableIdentifier, new Integer(currentValue));
                    break;
                case 3:
                    System.out.println(currentValue);
                    break;

            }

        }
    }
}
class LoopExpression extends Expr {

    public ExecutableBlock operationList; //loop operation flow
    public String valueToParse;    public Value myValue;
    public Value loopValue;
    //constructor method for loop expression

    public LoopExpression(Value initialLoopValue,ExecutableBlock initialOperationList, BB usedBB) {
        localBB = usedBB;
        type = 3;
        loopValue = initialLoopValue;
        operationList = initialOperationList;
    }

    public void execute() {

        int conditionVariableValue = loopValue.resolveValue();
        while(conditionVariableValue!=0) {
            //if condition is met, run the execution flow
            operationList.execute();
            conditionVariableValue =  loopValue.resolveValue();
        }
    }
}
class AssignExpression extends Expr {
    Value valueToAssign;
    String variableIdentifier;
    public AssignExpression (String initialVariableIdentifier, Value initialValueToAssign, BB usedBB) {
        type = 5;
        valueToAssign = initialValueToAssign;
        variableIdentifier = initialVariableIdentifier;
        localBB = usedBB;
    }
    public void execute() {
        localBB.checkVariable(variableIdentifier);
        int valueOfBlock = valueToAssign.resolveValue();
        localBB.variablesList.put(variableIdentifier, new Integer(valueOfBlock));
    }
}
