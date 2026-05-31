package org.example.service.dto;

public  class CreditSummaryDTO {
    private String clientName;
    private String cnp;
    private String contractNo;
    private Double sum;
    private String status;
    private String lastPaymentDate;

    public CreditSummaryDTO() {
    }

    public CreditSummaryDTO(String clientName, String cnp, String contractNo, Double sum, String status, String lastPaymentDate) {
        this.clientName = clientName;
        this.cnp = cnp;
        this.contractNo = contractNo;
        this.sum = sum;
        this.status = status;
        this.lastPaymentDate = lastPaymentDate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(String lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
}
