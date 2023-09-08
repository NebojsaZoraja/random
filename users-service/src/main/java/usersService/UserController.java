package usersService;

import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import usersService.dto.BankAccountDto;
import usersService.model.CustomUser;

@RestController
public class UserController {

	@Autowired
	private CustomUserRepository repo;

	@Autowired
	private BankAccountServiceProxy proxy;
	
	@GetMapping("/users-service/users")
	public List<CustomUser> getAllUsers(){
		return repo.findAll();
	}
	
	@GetMapping("/users-service/user/{email}")
	public ResponseEntity<CustomUser> getUserByEmail(@PathVariable String email){
		CustomUser user = repo.findByEmail(email);
		return new ResponseEntity<CustomUser>(user,HttpStatus.OK);
	}
	
	@PostMapping("/users-service/users")
	public ResponseEntity<CustomUser> createUser(@RequestHeader("Authorization")String auth, @RequestBody CustomUser user) {
		CustomUser authUser = getAuthUser(auth);

		if(user.getRole().equals("ADMIN")) {

			if(!authUser.getRole().equals("OWNER")) {
				return new ResponseEntity<CustomUser>(HttpStatus.FORBIDDEN);
			}

			CustomUser createdUser = repo.save(user);

			return ResponseEntity.status(201).body(createdUser);

		} else if (user.getRole().equals("OWNER")){
			return new ResponseEntity<CustomUser>(HttpStatus.BAD_REQUEST);

		} else {
			CustomUser createdUser = repo.save(user);
			return ResponseEntity.status(201).body(createdUser);
		}
	}
	
	 @PutMapping("/users-service/users")
     public ResponseEntity<CustomUser> updateUser(@RequestHeader("Authorization")String auth, @RequestBody CustomUser user){
		 CustomUser authUser = getAuthUser(auth);

		 if(repo.existsById(user.getId())) {

			 CustomUser existingUser = repo.findById(user.getId());

			 //ako dodeljujemo rolu admina
			 if(user.getRole().equals("ADMIN")) {

				 //ako trenutni user nije owner
				 if(!authUser.getRole().equals("OWNER")) {
					return new ResponseEntity<CustomUser>(HttpStatus.FORBIDDEN);
				 }
				 //ako je trenutni user owner
				 CustomUser temp = repo.save(user);
				 return new ResponseEntity<CustomUser>(temp, HttpStatus.OK);

				 //ako pokusavamo da dodelimo ulogu ownera
			 } else if (user.getRole().equals("OWNER")){
					return new ResponseEntity<CustomUser>(HttpStatus.BAD_REQUEST);
				//ako menjamo rolu na usera ili menjamo podatke o useru
			 } else {
				 //ako uloga postojeceg usera kojeg menjamo nije user
				 if(!existingUser.getRole().equals("USER")) {
					 //ne mozemo da menjamo ako nismo owner
					 if(!authUser.getRole().equals("OWNER")) {
						return new ResponseEntity<CustomUser>(HttpStatus.FORBIDDEN);
					}
					 //ne mozemo da menjamo ulogu owneru
					 if(existingUser.getRole().equals("OWNER")) {
							return new ResponseEntity<CustomUser>(HttpStatus.FORBIDDEN);
					 }

					 //owner menja podatke o adminu
					 CustomUser temp = repo.save(user);
					 return new ResponseEntity<CustomUser>(temp, HttpStatus.OK);
				 }
				 String existingUserEmail = existingUser.getEmail();

				 CustomUser temp = repo.save(user);
				 BankAccountDto account = proxy.getBankAccountByEmail(existingUserEmail).getBody();
				 if(account != null){
					 account.setEmail(user.getEmail());
					 proxy.updateBankAccount(account, existingUserEmail);
				 }
				 return new ResponseEntity<CustomUser>(temp, HttpStatus.OK);
			}
		}
		return new ResponseEntity<CustomUser>(HttpStatus.NOT_FOUND);
     }
	 
	 @DeleteMapping("/users-service/{id}")
	 public ResponseEntity<CustomUser> deleteUser(@PathVariable long id, @RequestHeader("Authorization")String auth){
		 if(repo.existsById(id)) {

		 	var user = repo.findById(id);
			repo.deleteById(id);
			proxy.deleteBankAccountByEmail(user.getEmail());
			return new ResponseEntity<CustomUser>(HttpStatus.OK);

			}
			return new ResponseEntity<CustomUser>(HttpStatus.NOT_FOUND);
	 }

	@ExceptionHandler({Exception.class})
	public ResponseEntity<String> rateLimiterExceptionHandler(Exception ex){
		return ResponseEntity.status(500).body(ex.getMessage());
	}
	 
	 private CustomUser getAuthUser(String auth) {
		 String credentials = new String(Base64.getDecoder().decode(auth.substring(6)));
	        String[] emailPassword = credentials.split(":");
	        String email = emailPassword[0];
	        return repo.findByEmail(email);
	 }
 }
