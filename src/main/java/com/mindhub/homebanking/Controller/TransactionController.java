package com.mindhub.homebanking.Controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindhub.homebanking.model.Account;
import com.mindhub.homebanking.model.Client;
import com.mindhub.homebanking.model.Transaction;
import com.mindhub.homebanking.model.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository personRepository;

    @GetMapping("/transactions")
    @JsonIgnore
    public List<Transaction> getTransactions(){
        return transactionRepository.findAll();
    }

    @RequestMapping("/transactions/{id}")
    public Transaction getTransaction(@PathVariable Long id){
        return transactionRepository.getReferenceById(id);
    }

    @Transactional
    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber,
            @RequestParam String amount, @RequestParam String description, Authentication authentication) {
        Account origenCuenta;
        Account destinoCuenta;

        if(validarNull(amount)){
            return new ResponseEntity<>("El campo monto viene vacio", HttpStatus.FORBIDDEN);
        }

        if(validarNull(description)){
            return new ResponseEntity<>("El campo descripcion viene vacio", HttpStatus.FORBIDDEN);
        }

        if(validarNull(fromAccountNumber)){
            return new ResponseEntity<>("El campo cuenta origen viene vacio", HttpStatus.FORBIDDEN);
        }

        if(validarNull(toAccountNumber)){
            return new ResponseEntity<>("El campo cuenta destino viene vacio", HttpStatus.FORBIDDEN);
        }

        origenCuenta = accountRepository.findByNumber(fromAccountNumber);
        if (origenCuenta ==  null) {
            return new ResponseEntity<>("Cuenta origen no encontrada", HttpStatus.FORBIDDEN);
        }

        if(validarCuentaClienteOrigen(origenCuenta, authentication)){
            return new ResponseEntity<>("Cuenta origen no le pertenece al cliente autenticado", HttpStatus.FORBIDDEN);
        }

        destinoCuenta = accountRepository.findByNumber(toAccountNumber);
        if (destinoCuenta ==  null) {
            return new ResponseEntity<>("Cuenta destino no encontrada", HttpStatus.FORBIDDEN);
        }

        if(origenCuenta.equals(destinoCuenta)){
            return new ResponseEntity<>("Las cuentas origen y destino son iguales", HttpStatus.FORBIDDEN);
        }

        Double monto = Double.parseDouble(amount);
        if(origenCuenta.getBalance() < monto){
            return new ResponseEntity<>("Las cuentas origen no tiene monto suficiente", HttpStatus.FORBIDDEN);
        }


        Transaction newDebit = new Transaction(TransactionType.DEBIT, LocalDateTime.now(),monto*-1,description + " " + destinoCuenta.getNumber());
        Transaction newCredit = new Transaction(TransactionType.CREDIT,LocalDateTime.now(),monto,description + " " + origenCuenta.getNumber());

        origenCuenta.addTrasanction(newDebit);
        origenCuenta.setBalance(origenCuenta.getBalance() - monto);
        destinoCuenta.addTrasanction(newCredit);
        destinoCuenta.setBalance(destinoCuenta.getBalance() + monto);

        accountRepository.save(origenCuenta);
        accountRepository.save(destinoCuenta);
        transactionRepository.save(newDebit);
        transactionRepository.save(newCredit);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private boolean validarNull(String valor){
        if(valor != null){
            return false;
        }
        return true;
    }

    private boolean validarCuentaClienteOrigen(Account origenCuenta,Authentication authentication){
        Client clientOrigen = origenCuenta.getClient();
        Client client = personRepository.findByEmail(authentication.getName());
        if(clientOrigen.getId() == client.getId()){
            return false;
        }
        return true;
    }

}
