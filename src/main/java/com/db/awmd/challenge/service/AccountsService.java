package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.TransferException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;

	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public String transfer(String fromAccountId, String toAccountId, BigDecimal transferAmount) {
		if (fromAccountId.isEmpty() || toAccountId.isEmpty()) {
			throw new TransferException("Account Id cannot be empty/blank");
		}

		if (fromAccountId.equalsIgnoreCase(toAccountId) || transferAmount.longValue() < 0) {
			throw new TransferException("Trasfer Not possible due to wrong parameters");
		}

		Account from = this.accountsRepository.getAccount(fromAccountId);
		Account to = this.accountsRepository.getAccount(toAccountId);
		if (from != null && to != null) {
			if (((from.getBalance().subtract(transferAmount)).longValue()) > Long.valueOf(0)) {
				from.setBalance(from.getBalance().subtract(transferAmount));
				to.setBalance(to.getBalance().add(transferAmount));
				// notificationService.notifyAboutTransfer(from, "Transferred " + transferAmount
				// + " to - "+ to);
				// notificationService.notifyAboutTransfer(to, "Received " + transferAmount + "
				// from - "+ from);
				return "Transfer Successful";
			} else {
				throw new TransferException("Insufficient Balance");
			}
		} else {
			throw new TransferException("Account Not Exist");
		}

	}

	public Map<String, Account> getAccounts() {
		return this.accountsRepository.getAllAccounts();
	}

	public AccountsRepository getAccountsRepository() {
		return this.accountsRepository;
	}
}
