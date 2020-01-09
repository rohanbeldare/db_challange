package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		accountsService.getAccountsRepository().clearAccounts();
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test
	public void successfulTransfer() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account fromAccount = new Account(uniqueId);
		fromAccount.setBalance(BigDecimal.valueOf(10000));
		this.accountsService.createAccount(fromAccount);

		uniqueId = "Id-1" + System.currentTimeMillis();
		Account toAccount = new Account(uniqueId);
		toAccount.setBalance(BigDecimal.valueOf(10000));
		this.accountsService.createAccount(toAccount);

		String result = this.accountsService.transfer(fromAccount.getAccountId(), toAccount.getAccountId(),
				BigDecimal.valueOf(100));
		assertEquals("Transfer Successful", result);

	}

	@Test
	public void insufficientBalance() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account fromAccount = new Account(uniqueId);
		fromAccount.setBalance(BigDecimal.valueOf(100));
		this.accountsService.createAccount(fromAccount);

		uniqueId = "Id-1" + System.currentTimeMillis();
		Account toAccount = new Account(uniqueId);
		toAccount.setBalance(BigDecimal.valueOf(100));
		this.accountsService.createAccount(toAccount);
		try {
			String result = this.accountsService.transfer(fromAccount.getAccountId(), toAccount.getAccountId(),
					BigDecimal.valueOf(100));
		} catch (Exception e) {
			assertEquals("Insufficient Balance", e.getMessage());
		}

	}

}
