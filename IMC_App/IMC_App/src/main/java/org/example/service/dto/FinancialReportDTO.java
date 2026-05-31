package org.example.service.dto;

public class FinancialReportDTO {
    private Integer totalActiveCredits;
    private Double totalLoanAmount;
    private Double totalCollectedAmount;
    private Integer totalOverdueRates;

    public FinancialReportDTO(Integer totalActiveCredits, Double totalLoanAmount, Double totalCollectedAmount, Integer totalOverdueRates) {
        this.totalActiveCredits = totalActiveCredits;
        this.totalLoanAmount = totalLoanAmount;
        this.totalCollectedAmount = totalCollectedAmount;
        this.totalOverdueRates = totalOverdueRates;
    }

    public Integer getTotalActiveCredits() {
        return totalActiveCredits;
    }

    public void setTotalActiveCredits(Integer totalActiveCredits) {
        this.totalActiveCredits = totalActiveCredits;
    }

    public Double getTotalLoanAmount() {
        return totalLoanAmount;
    }

    public void setTotalLoanAmount(Double totalLoanAmount) {
        this.totalLoanAmount = totalLoanAmount;
    }

    public Double getTotalCollectedAmount() {
        return totalCollectedAmount;
    }

    public void setTotalCollectedAmount(Double totalCollectedAmount) {
        this.totalCollectedAmount = totalCollectedAmount;
    }

    public Integer getTotalOverdueRates() {
        return totalOverdueRates;
    }

    public void setTotalOverdueRates(Integer totalOverdueRates) {
        this.totalOverdueRates = totalOverdueRates;
    }
}
