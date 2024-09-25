package com.zalo.Spring_Zalo.Response;

public class ReplacementResult {
    private String replacedText;
    private boolean replacementOccurred;

    public ReplacementResult(String replacedText, boolean replacementOccurred) {
        this.replacedText = replacedText;
        this.replacementOccurred = replacementOccurred;
    }

    public String getReplacedText() {
        return replacedText;
    }

    public boolean isReplacementOccurred() {
        return replacementOccurred;
    }
}
