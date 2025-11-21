package com.example.unitconversion;

public class HistoryItem {
    private int id;
    private String type;
    private String inputValue;
    private String inputUnit;
    private String outputValue;
    private String outputUnit;
    private String date;

    // Constructor
    public HistoryItem(int id, String type, String inputValue, String inputUnit,
                       String outputValue, String outputUnit, String date) {
        this.id = id;
        this.type = type;
        this.inputValue = inputValue;
        this.inputUnit = inputUnit;
        this.outputValue = outputValue;
        this.outputUnit = outputUnit;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getInputValue() {
        return inputValue;
    }

    public String getInputUnit() {
        return inputUnit;
    }

    public String getOutputValue() {
        return outputValue;
    }

    public String getOutputUnit() {
        return outputUnit;
    }

    public String getDate() {
        return date;
    }

    // Setters (if needed for updates)
    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public void setInputUnit(String inputUnit) {
        this.inputUnit = inputUnit;
    }

    public void setOutputValue(String outputValue) {
        this.outputValue = outputValue;
    }

    public void setOutputUnit(String outputUnit) {
        this.outputUnit = outputUnit;
    }

    public void setDate(String date) {
        this.date = date;
    }
}