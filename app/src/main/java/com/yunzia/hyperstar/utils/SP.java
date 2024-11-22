package com.yunzia.hyperstar.utils;

public class SP {

    private String spType;
    private String key;
    private int type;
    private Object value;

    public static final int type_string = 0;
    public static final int type_boolean = 1;
    public static final int type_int = 2;
    public static final int type_float = 3;
    public static final int type_long = 4;

    
    public SP(
            String spType,
            String key,
            int type,
            Object value
    ){
        this.spType = spType;
        this.key = key;
        this.type = type;
        this.value = value;
        
    }

    public String getSpType() {
        return spType;
    }

    public void setSpType(String spType) {
        this.spType = spType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getValue() {
        return value;

    }

    public void setValue(Object value) {
        this.value = value;
    }


}
