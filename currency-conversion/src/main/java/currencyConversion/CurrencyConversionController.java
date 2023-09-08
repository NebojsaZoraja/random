package currencyConversion;

import java.math.BigDecimal;
import java.util.Base64;

import currencyConversion.dto.BankAccountDto;
import currencyConversion.dto.ConversionResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private CurrencyExchangeProxy exchangeProxy;

	@Autowired
	private BankAccountServiceProxy bankAccountProxy;

	//localhost:8100/currency-conversion-feign?from=EUR&to=RSD&quantity=50
	@PutMapping("/currency-conversion")
	@CircuitBreaker(name = "conversionCircuitBreaker", fallbackMethod = "circuitBreakerFallBack")
	public ResponseEntity<?> getConversionFeign(@RequestParam String from, @RequestParam String to, @RequestParam double quantity, @RequestHeader("Authorization") String auth){
		boolean conversionSuccessful = false;

		CurrencyConversion conversion = exchangeProxy.getExchange(from, to).getBody();
		conversion.setConversionTotal(conversion.getConversionMultiple().multiply(BigDecimal.valueOf(quantity)));

		BankAccountDto account = bankAccountProxy.getBankAccountByEmail(getUserEmailFromAuth(auth)).getBody();
		if (account == null) {
			return ResponseEntity.badRequest().body("User does not have a bank account!");
		}
		switch (from){
			case "USD":
				if(account.getUsdQuantity()>=quantity){
					conversionSuccessful = true;
					account.setUsdQuantity(account.getUsdQuantity()-quantity);
				}
				break;
			case "GBP":
				if(account.getGbpQuantity()>=quantity){
					conversionSuccessful = true;
					account.setGbpQuantity(account.getGbpQuantity()-quantity);
				}
				break;
			case "RUB":
				if(account.getRubQuantity()>=quantity){
					conversionSuccessful = true;
					account.setRubQuantity(account.getRubQuantity()-quantity);
				}
				break;
			case "CHF":
				if(account.getChfQuantity()>=quantity){
					conversionSuccessful = true;
					account.setChfQuantity(account.getChfQuantity()-quantity);
				}
				break;
			case "EUR":
				if(account.getEurQuantity()>=quantity){
					conversionSuccessful = true;
					account.setEurQuantity(account.getEurQuantity()-quantity);
				}
				break;
		}

		if(conversionSuccessful){
			account.setRsdQuantity(account.getRsdQuantity()+conversion.getConversionTotal().doubleValue());
			BankAccountDto newAccountValues = bankAccountProxy.convertCurrencies(account).getBody();
			ConversionResponseDto response = new ConversionResponseDto(newAccountValues);
			response.setMessage("Successfully converted " + quantity + " " + from + " into " + conversion.getConversionTotal().toString() + " " + to);
			return ResponseEntity.ok(response);
		} else {
			ConversionResponseDto response = new ConversionResponseDto(account);
			response.setMessage("Conversion unsuccessful due to insufficient funds");
			return ResponseEntity.status(400).body(response);
		}
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<String> rateLimiterExceptionHandler(Exception ex){
		return ResponseEntity.status(500).body("Internal server error!");
	}
	
	public ResponseEntity<String>circuitBreakerFallBack(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}

	private String getUserEmailFromAuth(String auth) {
		String credentials = new String(Base64.getDecoder().decode(auth.substring(6)));
		String[] emailPassword = credentials.split(":");
		return emailPassword[0];
	}
	
}
