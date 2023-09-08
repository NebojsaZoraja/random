package bankAccount;

import java.util.Base64;
import java.util.List;

import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import bankAccount.dto.CustomUserDto;


@RestController
public class BankAccountController {
	
	@Autowired
	private BankAccountRepository repo;
	
	@Autowired
	private UsersServiceProxy proxy;

	@GetMapping("/bank-account")
	public ResponseEntity<List<BankAccount>> getAllBankAccounts(){
		return ResponseEntity.ok(repo.findAll());
	}

	@GetMapping("/bank-account/account/{email}")
	public ResponseEntity<BankAccount> getBankAccountByEmail(@PathVariable String email){
		if(repo.existsByEmail(email)){
			var account = repo.findByEmail(email);
			return ResponseEntity.status(200).body(account);
		}
		return ResponseEntity.status(200).body(null);
	}
	
	@GetMapping("/bank-account/account")
	public ResponseEntity<BankAccount> getBankAccountForUser(@RequestHeader("Authorization") String auth){
		
		String authUser = getUserEmailFromAuth(auth);
		
		BankAccount account = repo.findByEmail(authUser);

		if(account == null) {
			return new ResponseEntity<BankAccount>(HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(account);
	}
	
	@PostMapping("/bank-account")
	public ResponseEntity<?> addNewBankAccount(@RequestBody BankAccount newAccount){
		ResponseEntity<CustomUserDto> response = proxy.getUserByEmail(newAccount.getEmail());
		CustomUserDto user = response.getBody();
		
		if(user == null) {
			return ResponseEntity.badRequest().body("User with given email doesn't exist!");
		}
		
		if(repo.existsById(newAccount.getId())) {
			return ResponseEntity.badRequest().body("Account with given id already exists!");
		}
		
		if(repo.existsByEmail(newAccount.getEmail())) {
			return ResponseEntity.badRequest().body("Account with given email already exists!");
		}
		
		repo.save(newAccount);
		
		return ResponseEntity.ok(newAccount);
	}

	@PutMapping("/bank-account/account/{email}")
	public ResponseEntity<?> updateBankAccount(@RequestBody BankAccount updatedAccount, @PathVariable String email){
		BankAccount currentAccount = repo.findByEmail(email);
		CustomUserDto existingUser = proxy.getUserByEmail(updatedAccount.getEmail()).getBody();
		if(existingUser != null){
			repo.save(updatedAccount);
			return ResponseEntity.status(200).body(updatedAccount);
		}
		if(currentAccount.getId() != updatedAccount.getId()){
			return ResponseEntity.status(400).body("Invalid update request");
		}
		return ResponseEntity.status(400).body("The provided email doesn't match any existing user's!");
	}

	@PutMapping("/bank-account/account/convert")
	public ResponseEntity<BankAccount> convertCurrencies(@RequestBody BankAccount updatedAccount){
		repo.save(updatedAccount);
		return ResponseEntity.status(200).body(updatedAccount);
	}

	@DeleteMapping("/bank-account/account/{email}")
	public void deleteBankAccountByEmail(@PathVariable String email){
		if(repo.existsByEmail(email)) {
			var bankAccount = repo.findByEmail(email);
			repo.delete(bankAccount);
		}
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<String> rateLimiterExceptionHandler(Exception ex){
		return ResponseEntity.status(500).body("Internal server error!");
	}
	
	private String getUserEmailFromAuth(String auth) {
		String credentials = new String(Base64.getDecoder().decode(auth.substring(6)));
        String[] emailPassword = credentials.split(":");
        return emailPassword[0];
	}
}
