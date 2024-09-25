package com.zalo.Spring_Zalo.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.Setter;
import lombok.Getter;
@Getter
@Setter
public class TextOverlay {
    @JsonProperty("Lines")
    private List<Line> lines;

    @JsonProperty("HasOverlay")
    private boolean hasOverlay;

    // Getters and setters
}