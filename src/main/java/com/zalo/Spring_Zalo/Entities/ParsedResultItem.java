package com.zalo.Spring_Zalo.Entities;



import com.fasterxml.jackson.annotation.JsonProperty;

public class ParsedResultItem {
    @JsonProperty("TextOverlay")
    private TextOverlay textOverlay;

    @JsonProperty("TextOrientation")
    private String textOrientation;

    @JsonProperty("FileParseExitCode")
    private int fileParseExitCode;

    @JsonProperty("ParsedText")
    private String parsedText;

    @JsonProperty("ErrorMessage")
    private String errorMessage;

    @JsonProperty("ErrorDetails")
    private String errorDetails;

    public TextOverlay getTextOverlay() {
        return textOverlay;
    }

    public void setTextOverlay(TextOverlay textOverlay) {
        this.textOverlay = textOverlay;
    }

    public String getTextOrientation() {
        return textOrientation;
    }

    public void setTextOrientation(String textOrientation) {
        this.textOrientation = textOrientation;
    }

    public int getFileParseExitCode() {
        return fileParseExitCode;
    }

    public void setFileParseExitCode(int fileParseExitCode) {
        this.fileParseExitCode = fileParseExitCode;
    }

    public String getParsedText() {
        return parsedText;
    }

    public void setParsedText(String parsedText) {
        this.parsedText = parsedText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    // Getter và Setter
}
