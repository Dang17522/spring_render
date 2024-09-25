package com.zalo.Spring_Zalo.Entities;



import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ParsedResult {
    @JsonProperty("ParsedResults")
    private List<ParsedResultItem> parsedResults;

    @JsonProperty("OCRExitCode")
    private int ocrExitCode;

    @JsonProperty("IsErroredOnProcessing")
    private boolean isErroredOnProcessing;

    @JsonProperty("ProcessingTimeInMilliseconds")
    private String processingTimeInMilliseconds;

    @JsonProperty("ErrorMessage")
    private List<String> ErrorMessage;

    @JsonProperty("ErrorDetails")
    private String ErrorDetails;

    @JsonProperty("SearchablePDFURL")
    private String searchablePDFURL;

    public List<ParsedResultItem> getParsedResults() {
        return parsedResults;
    }

    public void setParsedResults(List<ParsedResultItem> parsedResults) {
        this.parsedResults = parsedResults;
    }

    public int getOcrExitCode() {
        return ocrExitCode;
    }

    public void setOcrExitCode(int ocrExitCode) {
        this.ocrExitCode = ocrExitCode;
    }

    public boolean isErroredOnProcessing() {
        return isErroredOnProcessing;
    }

    public List<String> getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(List<String> errorMessage) {
        ErrorMessage = errorMessage;
    }

    public void setErroredOnProcessing(boolean isErroredOnProcessing) {
        this.isErroredOnProcessing = isErroredOnProcessing;
    }

    public String getProcessingTimeInMilliseconds() {
        return processingTimeInMilliseconds;
    }

    public void setProcessingTimeInMilliseconds(String processingTimeInMilliseconds) {
        this.processingTimeInMilliseconds = processingTimeInMilliseconds;
    }

    public String getSearchablePDFURL() {
        return searchablePDFURL;
    }

    public void setSearchablePDFURL(String searchablePDFURL) {
        this.searchablePDFURL = searchablePDFURL;
    }

    public String getErrorDetails() {
        return ErrorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        ErrorDetails = errorDetails;
    }


    // Getter và Setter
}

