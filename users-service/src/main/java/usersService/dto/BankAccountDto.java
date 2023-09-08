package usersService.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class BankAccountDto {
    private long id;

    private String email;

    private Double usdQuantity;

    private Double eurQuantity;

    private Double gbpQuantity;

    private Double chfQuantity;

    private Double rsdQuantity;

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
