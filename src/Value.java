
//parent type
public class Value {
    public BB localBB;
    int resolveValue() {

        return 0;
    }
    public void assignLeft(Value initialValue1) {

    }

    public void assignRight(Value initialValue2) {

    }
}

//holds a single variable
class VariableValue extends Value {
    private String identifier;
    public VariableValue(String variableIdentifier, BB initialBB) {
        localBB = initialBB;
        identifier = variableIdentifier;
    }
    int resolveValue() {
        localBB.checkVariable(identifier);
        return  localBB.variablesList.get(identifier);
    }
}

//holds a constant
class ConstantValue extends Value {
    private int resolvedValue;
    public ConstantValue(int initialValue, BB initialBB) {
        localBB = initialBB;
        resolvedValue = initialValue;
    }

    public int resolveValue() {
        return resolvedValue;
    }
}

//holds an operation
class OperationValue extends Value {
    private Value value1;
    private Value value2;
    private int type; //0 -equals  1-bigger 2-bigger or equal 3-less 4-less or equal 5-add 6-substract 7-multiply 8-divide 9-not equal
    public OperationValue(int initialType, BB initialBB) {
        localBB = initialBB;
        type = initialType;
    }

    //assign a value block for the left side of operation
    public void assignLeft(Value initialValue1) {
        value1 = initialValue1;
    }

    //assign a value block for the right side of operation
    public void assignRight(Value initialValue2) {
        value2 = initialValue2;
    }

    int resolveValue(){
        //get the values of both sides
        int resolvedValue1 = value1.resolveValue();
        int resolvedValue2 = value2.resolveValue();
        //do the operation on resolved values
        switch(type){
            case 0:
                return (resolvedValue1==resolvedValue2)? 1 : 0;
            case 1:
                return (resolvedValue1>resolvedValue2)? 1 : 0;
            case 2:
                return (resolvedValue1>=resolvedValue2)? 1 : 0;
            case 3:
                return (resolvedValue1<resolvedValue2)? 1 : 0;
            case 4:
                return (resolvedValue1<=resolvedValue2)? 1 : 0;
            case 5:
                return (resolvedValue1+resolvedValue2);
            case 6:
                return (resolvedValue1-resolvedValue2);
            case 7:
                return (resolvedValue1*resolvedValue2);
            case 8:
                if(resolvedValue2==0) {
                    localBB.panic("Division by zero!");
                }
                return (resolvedValue1/resolvedValue2);
            case 9:
                return (resolvedValue1!=resolvedValue2)? 1 : 0;
            default:
                localBB.panic("Unknown operation");
                return -1;

        }
    }
}


