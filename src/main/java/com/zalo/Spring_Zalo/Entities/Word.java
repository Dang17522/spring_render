package com.zalo.Spring_Zalo.Entities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Word {
    @JsonProperty("WordText")
    private String wordText;

    @JsonProperty("Left")
    private double left;

    @JsonProperty("Top")
    private double top;

    @JsonProperty("Height")
    private double height;

    @JsonProperty("Width")
    private double width;

    public String getWordText() {
        return wordText;
    }

    public void setWordText(String wordText) {
        this.wordText = wordText;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }   
    public static Word[] wordFromJson(JSONArray jsonWords) {
       ObjectMapper objectMapper = new ObjectMapper();
    
        List<Word> wordList = new ArrayList<>();
        
        for (int i = 0; i < jsonWords.length(); i++) {
            JSONObject jsonWord = jsonWords.getJSONObject(i);
            Word word = objectMapper.convertValue(jsonWord, Word.class);
            wordList.add(word);
        }
        
        return wordList.toArray(new Word[0]);
    }
    
}