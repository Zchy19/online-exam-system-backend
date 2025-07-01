package cn.org.alan.exam.utils.excel;


import java.util.LinkedHashMap;


public class ExcelClassField {

    
    private String fieldName;

    
    private String name;

    
    private LinkedHashMap<String, String> kvMap;

    
    private Object example;

    
    private int sort;

    
    private int hasAnnotation;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, String> getKvMap() {
        return kvMap;
    }

    public void setKvMap(LinkedHashMap<String, String> kvMap) {
        this.kvMap = kvMap;
    }

    public Object getExample() {
        return example;
    }

    public void setExample(Object example) {
        this.example = example;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getHasAnnotation() {
        return hasAnnotation;
    }

    public void setHasAnnotation(int hasAnnotation) {
        this.hasAnnotation = hasAnnotation;
    }

}
