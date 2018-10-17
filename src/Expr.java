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
        System.out.println("Running executable block");
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
    public int operationType; //Possible values of operation type: 0 - clear 1 - increase 2- decrease
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
            System.out.println("Clearing variable: " + operationVariableIdentifier);
            localBB.variablesList.put(operationVariableIdentifier, new Integer(0));
            System.out.println(localBB.variablesList.get(operationVariableIdentifier));
        }
        else {
            //in other cases, first check the variable list (HashMap), whether variable exists
            System.out.println("Checking whether variable exists... ");
            if (!localBB.variablesList.containsKey(operationVariableIdentifier)) {
                localBB.panic("Variable " + operationVariableIdentifier + " used but not declared");
            }
            //given identifier, increase the value of V from hashmap where variable identifier is a Key
            else if (operationType == 1) {
                System.out.println("Increasing variable: " + operationVariableIdentifier);
                int currentValue = localBB.variablesList.get(operationVariableIdentifier);
                currentValue++;
                localBB.variablesList.put(operationVariableIdentifier, new Integer(currentValue));
                System.out.println("Value after incrementation: " + currentValue);
            }
            else {
                //same case as with increase
                System.out.println("Decreasing variable: " + operationVariableIdentifier);
                int currentValue = localBB.variablesList.get(operationVariableIdentifier);
                currentValue--;
                localBB.variablesList.put(operationVariableIdentifier, new Integer(currentValue));
                System.out.println("Value after decrementation: " + currentValue);
            }

        }
    }
}
class LoopExpression extends Expr {
    public String conditionVariableIdentifier; //checked variable identifier
    public int valueNotEqual; //value compared to variable of given identifier
    public ExecutableBlock operationList; //loop operation flow

    //constructor method for loop expression
    public LoopExpression(String initialConditionVariableIdentifier, int initialValueNotEqual, ExecutableBlock initialOperationList, BB usedBB) {
        localBB = usedBB;
        type = 3;
        valueNotEqual= initialValueNotEqual;
        conditionVariableIdentifier = initialConditionVariableIdentifier;
        operationList = initialOperationList;
    }

    public void execute() {
        //first check whether condition variable exists
        System.out.println("Checking whether variable exists... ");
        if (!localBB.variablesList.containsKey(conditionVariableIdentifier)) {
            localBB.panic("Variable " + conditionVariableIdentifier + " used but not declared");
        }
        //compare condition variable to checked variable
        int conditionVariableValue = localBB.variablesList.get(conditionVariableIdentifier);
        while(conditionVariableValue!=valueNotEqual) {
            //if condition is met, run the execution flow
            System.out.println("Executing loop. Control variable value is " + conditionVariableValue);
            operationList.execute();
            conditionVariableValue = localBB.variablesList.get(conditionVariableIdentifier);
        }
    }
}
