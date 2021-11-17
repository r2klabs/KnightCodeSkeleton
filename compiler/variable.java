package compiler;

public class variable{
    private String variableType;
    private Object variableSet;

    public variable(){
        variableType = "";
        variableSet = null;
    }

    public variable(String variableType, Object variableSet){
        this.variableType = variableType;
        this.variableSet = variableSet;
    }

    public String getVariableType(){

        return variableType;

    }

    public void setVariableType(String variableType){

        this.variableType = variableType;

    }

    public Object getVariableSet(){

        return variableSet;

    }

    public void setVariableSet(Object variableSet){

        this.variableSet = variableSet;

    }




}
