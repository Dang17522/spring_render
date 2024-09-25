package com.zalo.Spring_Zalo.Entities;



import java.util.List;


public class ProcessedData {
    private String lineText;
    private List<ProcessedWord> words;
    public String getLineText() {
        return lineText;
    }
    public void setLineText(String lineText) {
        this.lineText = lineText;
    }
    public List<ProcessedWord> getWords() {
        return words;
    }
    public void setWords(List<ProcessedWord> words) {
        this.words = words;
    }

  
}