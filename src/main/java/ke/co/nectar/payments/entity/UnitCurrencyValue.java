package ke.co.nectar.payments.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ke.co.nectar.payments.entity.audit.DateAudit;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "unit_currency_value")
public class UnitCurrencyValue extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "currency")
    private String currency;

    @NotNull
    private Double value;

    public UnitCurrencyValue() {}

    public UnitCurrencyValue(String currency, Double value) {
        setCurrency(currency);
        setValue(value);
    }

    @Override
    public String toString() {
        return String.format("id: %d", id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
