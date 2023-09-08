package currencyConversion;

import currencyConversion.dto.BankAccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "bank-account")
public interface BankAccountServiceProxy {

    @GetMapping("/bank-account/account/{email}")
    public ResponseEntity<BankAccountDto> getBankAccountByEmail(@PathVariable String email);

    @PutMapping("/bank-account/account/convert")
    public ResponseEntity<BankAccountDto> convertCurrencies(@RequestBody BankAccountDto updatedAccount);
}