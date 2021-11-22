package compiler;

public class variable{
    private String variableType;
    private Object variableValue;
    private int memoryLocation;

    public variable(){
        variableType = "";
        variableValue = null;
    }

    public variable(String variableType, Object variableSet, int memoryLocation){
        this.variableType = variableType;
        this.variableValue = variableSet;
        this.memoryLocation = memoryLocation;
    }

    public String getVariableType(){

        return variableType;

    }

    public void setVariableType(String variableType){

        this.variableType = variableType;

    }

    public Object getVariableValue(){

        return variableValue;

    }

    public void setVariableValue(Object variableSet){

        this.variableValue = variableSet;

    }

    public int getMemoryLocation(){
        return memoryLocation;
    }

    public void setMemoryLocation(int memoryLocation){

        this.memoryLocation = memoryLocation;

    }




}
