package com.mindhub.homebanking.dtos;

public class LoanApplicationDTO {

    private long LoanId;
    private Double amount;
    private int payments;
    private String toAccountNumber;

    public long getLoanId() {
        return LoanId;
    }

    public void setLoanId(long loanId) {
        LoanId = loanId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }
}
