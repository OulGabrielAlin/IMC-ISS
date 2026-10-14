package org.example.service.dto;

public class EligibilityResult {
    private Boolean isEligible;
    private Double score;
    private String message;

    public EligibilityResult(Boolean isEligible, Double score, String message) {
        this.isEligible = isEligible;
        this.score = score;
        this.message = message;
    }

    public Boolean getEligible() {
        return isEligible;
    }

    public Double getScore() {
        return score;
    }

    public String getMessage() {
        return message;
    }
}
