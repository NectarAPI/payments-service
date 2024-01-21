package ke.co.nectar.payments.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ke.co.nectar.payments.entity.audit.DateAudit;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "credits")
public class Credits extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    private String ref;

    @NotNull
    @Column(name = "purchase_date")
    @JsonProperty("purchase_date")
    private Instant purchaseDate;

    @NotNull
    @Column(name = "user_ref")
    @JsonProperty("user_ref")
    private String userRef;

    @NotNull
    private Double value;

    @NotNull
    private String currency;

    @NotNull
    private Double units;

    @OneToOne
    @JoinColumn(name = "payment_id")
    @JsonIgnore
    private Payment payment;

    public Credits() {}

    public Credits(String ref, Instant purchaseDate, String userRef,
                   Double value, String currency, Double units,
                   Payment payment) {
        setRef(ref);
        setPurchaseDate(purchaseDate);
        setUserRef(userRef);
        setValue(value);
        setCurrency(currency);
        setUnits(units);
        setPayment(payment);
    }

    @Override
    public String toString() {
        return String.format("ref: %s", ref);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getUserRef() {
        return userRef;
    }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getUnits() {
        return units;
    }

    public void setUnits(Double units) {
        this.units = units;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
