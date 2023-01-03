package com.mindhub.homebanking.Controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindhub.homebanking.model.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientRepository personRepository;

    @GetMapping("/cards")
    @JsonIgnore
    public List<Card> getCards(){
        return cardRepository.findAll();
    }

    @RequestMapping("/cards/{id}")
    public Card getCards(@PathVariable Long id){
        return cardRepository.getReferenceById(id);
    }

    @RequestMapping("/clients/current/cards")
    public ResponseEntity<Object> register(
            @RequestParam CardColor cardColor, @RequestParam CardType cardType, Authentication authentication){
        Client client = personRepository.findByEmail(authentication.getName());
        int countType = 0;
        for(Card card : client.getCards()){
            if(cardType.equals(card.getType())){
                countType++;
            }
        }
        if(countType > 2){
            return new ResponseEntity<>("El cliente ya tiene 3 tarjetas registradas del tipo " + cardType , HttpStatus.FORBIDDEN);
        }

        int cvv = generarNumberRandom(999, 100);
        String numberCard = String.format("%04d",generarNumberRandom(9999, 0)) + "-" + String.format("%04d",generarNumberRandom(9999, 0))
                +"-"+ String.format("%04d",generarNumberRandom(9999,0)) + "-" + String.format("%04d",generarNumberRandom(9999,0));
        Card card = new Card (client.getFirstName() + " " + client.getLastName(), cardType, cardColor, numberCard,
                cvv,LocalDateTime.now().toString(), LocalDateTime.now().plusYears(5).toString());
        client.addCards(card);
        cardRepository.save(card);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private int generarNumberRandom(int max, int min){
        return (int) (Math.random() * (max - min) + min);
    }
}
