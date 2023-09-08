package bankAccount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long>{
	BankAccount findByEmail(String email);
	BankAccount findById(long id);
	boolean existsByEmail(String email);
}
