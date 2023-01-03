package com.mindhub.homebanking;

import com.mindhub.homebanking.model.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {
	@Autowired
	private PasswordEncoder passwordEnconder;

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository repository, AccountRepository repositoryAccount,
									  TransactionRepository transactionRepository, LoanRepository loanRepository,
									  ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (args) -> {
			// save a couple of customers
			Client nuevoClient = new Client("Jack", "Bauer","melba@mindhub.com",passwordEnconder.encode("1234"));
			Client nuevoClient2 = new Client("Black", "White","white@black.com", passwordEnconder.encode("1234"));

			Account account1 = new Account("VIN001", LocalDateTime.now(),5000.0);
			Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1),7500.0);

			Transaction transaction = new Transaction(TransactionType.DEBIT, LocalDateTime.now(), -200.0, "transa1");
			Transaction transaction2 = new Transaction(TransactionType.CREDIT, LocalDateTime.now(), 50.0, "transa2");
			Transaction transaction3 = new Transaction(TransactionType.DEBIT, LocalDateTime.now(), -100.0,"transa3");
			Transaction transaction4 = new Transaction(TransactionType.CREDIT, LocalDateTime.now(), 75.0, "transa4");

			Card card1 = new Card(nuevoClient.getFirstName() +" "+ nuevoClient.getLastName(), CardType.DEBIT, CardColor.GOLD, "1231-4564-7897-1231",
					111,LocalDateTime.now().toString(),LocalDateTime.now().plusYears(5).toString());
			Card card2 = new Card(nuevoClient.getFirstName() +" "+ nuevoClient.getLastName(), CardType.CREDIT, CardColor.TITANIUM, "2222-2332-3434-1231",
					222,LocalDateTime.now().toString(), LocalDateTime.now().plusYears(5).toString());
			Card card3 = new Card(nuevoClient2.getFirstName() +" "+ nuevoClient2.getLastName(), CardType.CREDIT, CardColor.SILVER, "3322-2456-3333-2244",
					772, LocalDateTime.now().toString(), LocalDateTime.now().plusYears(5).toString());

			Loan loan1 = new Loan("Hipotecario", 500000.0, List.of(12,24,36,48,60));
			Loan loan2 = new Loan("Personal", 100000.0, List.of(6,12,24));
			Loan loan3 = new Loan("Automotriz", 300000.0, List.of(6,12,24,36));

			ClientLoan clientLoan1 = new ClientLoan(400000.0, nuevoClient, loan1,60);
			ClientLoan clientLoan2 = new ClientLoan(50000.0, nuevoClient, loan2, 12);
			ClientLoan clientLoan3 = new ClientLoan(100000.0, nuevoClient2, loan2, 24);
			ClientLoan clientLoan4 = new ClientLoan(200000.0, nuevoClient2, loan3, 36);

			nuevoClient.addAccount(account1);
			nuevoClient.addAccount(account2);

			nuevoClient.addClientLoan(clientLoan1);
			nuevoClient.addClientLoan(clientLoan2);
			nuevoClient2.addClientLoan(clientLoan3);
			nuevoClient2.addClientLoan(clientLoan4);

			nuevoClient.addCards(card1);
			nuevoClient.addCards(card2);
			nuevoClient2.addCards(card3);

			account1.addTrasanction(transaction);
			account1.addTrasanction(transaction2);
			account1.addTrasanction(transaction4);
			account2.addTrasanction(transaction3);

			repository.save(nuevoClient);
			repository.save(nuevoClient2);
			repositoryAccount.save(account1);
			repositoryAccount.save(account2);
			transactionRepository.save(transaction);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			loanRepository.save(loan1) ;
			loanRepository.save(loan2);
			loanRepository.save(loan3);
			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
			clientLoanRepository.save(clientLoan4);
			cardRepository.save(card1);
			cardRepository.save(card2);
			cardRepository.save(card3);
		};
	}

}
