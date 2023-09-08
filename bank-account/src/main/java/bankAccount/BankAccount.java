package bankAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BankAccount {

	@Id
	private long id;
	
    @Column(unique = true)
	private String email;
    
    @Column(name = "usd_quantity")
    private Double usdQuantity;
	
    @Column(name = "eur_quantity")
    private Double eurQuantity;
    
    @Column(name = "gbp_quantity")
    private Double gbpQuantity;
    
    @Column(name = "chf_quantity")
    private Double chfQuantity;
    
    @Column(name = "rsd_quantity")
    private Double rsdQuantity;
	
    @Column(name = "rub_quantity")
    private Double rubQuantity;
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Double getUsdQuantity() {
		return usdQuantity;
	}

	public void setUsdQuantity(Double usdQuantity) {
		this.usdQuantity = usdQuantity;
	}

	public Double getEurQuantity() {
		return eurQuantity;
	}

	public void setEurQuantity(Double eurQuantity) {
		this.eurQuantity = eurQuantity;
	}

	public Double getGbpQuantity() {
		return gbpQuantity;
	}

	public void setGbpQuantity(Double gbpQuantity) {
		this.gbpQuantity = gbpQuantity;
	}

	public Double getChfQuantity() {
		return chfQuantity;
	}

	public void setChfQuantity(Double chfQuantity) {
		this.chfQuantity = chfQuantity;
	}

	public Double getRsdQuantity() {
		return rsdQuantity;
	}

	public void setRsdQuantity(Double rsdQuantity) {
		this.rsdQuantity = rsdQuantity;
	}

	public Double getRubQuantity() {
		return rubQuantity;
	}

	public void setRubQuantity(Double rubQuantity) {
		this.rubQuantity = rubQuantity;
	}

	
}
