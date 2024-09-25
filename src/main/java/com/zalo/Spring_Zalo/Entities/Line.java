package com.zalo.Spring_Zalo.Entities;
import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Setter;
import lombok.Getter;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Line {
    @JsonProperty("LineText")
    private String lineText;

    @JsonProperty("Words")
    private List<Word> words;

    @JsonProperty("MaxHeight")
    private double maxHeight;

    @JsonProperty("MinTop")
    private double minTop;

    public static Line lineFromJson(JSONObject jsonLine) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(jsonLine, Line.class);
    }

}