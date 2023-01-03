package com.mindhub.homebanking.Controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.model.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @RequestMapping("/loans")
    @JsonIgnore
    public List<LoanDTO> getLoans(){
        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(toList());
    }

    @RequestMapping(value="/loans", method=RequestMethod.POST)
    public ResponseEntity<String> addLoans(@RequestBody LoanApplicationDTO loanApplicationDTO,
                                           Authentication authentication) {

        if(loanApplicationDTO.getAmount() < 1){
            return new ResponseEntity<>("El monto debe ser mayor a 0", HttpStatus.FORBIDDEN);
        }

        if(loanApplicationDTO.getPayments() < 1){
            return new ResponseEntity<>("Las cuotas deben ser mayor a 0", HttpStatus.FORBIDDEN);
        }

        if(loanApplicationDTO.getToAccountNumber() == null || loanApplicationDTO.getToAccountNumber() == ""){
            return new ResponseEntity<>("Dato cuenta destino sin valor", HttpStatus.FORBIDDEN);
        }
        Optional<Loan> loan = loanRepository.findById(loanApplicationDTO.getLoanId());

        if(loanApplicationDTO.getAmount() > loan.get().getMaxAmount()){
            return new ResponseEntity<>("El monto solicitado excede el monto maximo permitido", HttpStatus.FORBIDDEN);
        }

        if(loan == null){
            return new ResponseEntity<>("EL prestamo no existe", HttpStatus.FORBIDDEN);
        }



        if(!validarCuotas(loan.get().getPayments(), loanApplicationDTO.getPayments())){
            return new ResponseEntity<>("Cantidad de cuotas no coincide con los del prestamo", HttpStatus.FORBIDDEN);
        }
        Account account = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());
        if(account == null){
            return new ResponseEntity<>("La cuenta de destino no existe", HttpStatus.FORBIDDEN);
        }

        Client client = clientRepository.findByEmail(authentication.getName());
        if(!validarCuentaCliente(client.getAccounts(),account)){
            return new ResponseEntity<>("La cuenta destino no pertenece al cliente", HttpStatus.FORBIDDEN);
        }

        Double newMonto = loanApplicationDTO.getAmount() * 1.2;
        Transaction transaction = new Transaction(TransactionType.CREDIT, LocalDateTime.now(),newMonto, loan.get().getName()+" loan approved");
        account.setBalance(account.getBalance() + newMonto);
        transaction.setAccount(account);

        ClientLoan newLoan = new ClientLoan(newMonto,client,loan.get(),loanApplicationDTO.getPayments());

        clientLoanRepository.save(newLoan);
        transactionRepository.save(transaction);
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private boolean validarCuentaCliente(Set<Account> accounts, Account account) {
        for (Account paymentInList: accounts) {
            if(paymentInList.getClient() == account.getClient()){
                return true;
            }
        }
        return false;
    }

    public boolean validarCuotas(List<Integer> payments, int loanApplicationDTOPayments){
        for (Integer paymentInList: payments) {
            if(paymentInList.compareTo(loanApplicationDTOPayments) == 0){
                return true;
            }
        }
        return false;
    }


}
