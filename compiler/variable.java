package compiler;

public class variable{
    private String variableType;
    private Object variableValue;

    public variable(){
        variableType = "";
        variableValue = null;
    }

    public variable(String variableType, Object variableSet){
        this.variableType = variableType;
        this.variableValue = variableSet;
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




}
