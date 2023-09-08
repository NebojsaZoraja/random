package bankAccount;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import bankAccount.dto.CustomUserDto;

@FeignClient(name = "users-service")
public interface UsersServiceProxy {

	@GetMapping("/users-service/user/{email}")
	public ResponseEntity<CustomUserDto> getUserByEmail(@PathVariable String email);
}
