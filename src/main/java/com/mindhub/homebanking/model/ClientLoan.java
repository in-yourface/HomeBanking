package com.mindhub.homebanking.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class ClientLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Double amount;

   @ManyToOne
   @JoinColumn(name = "client_id")
   private Client client;

   @ManyToOne
   @JoinColumn(name = "loan_id")
   private Loan loan;


    private Integer payments;


    public ClientLoan(){

    }
    public ClientLoan(Double amount, Client client, Loan loan, Integer payments){
        this.amount = amount;
        this.payments = payments;
        this.client = client;
        this.loan = loan;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public void setPayments(Integer payments) {
        this.payments = payments;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }
}
