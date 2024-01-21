package ke.co.nectar.payments.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ke.co.nectar.payments.entity.audit.DateAudit;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "credits_consumption")
public class CreditsConsumption extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    private String ref;

    @NotNull
    @Column(name = "consumption_date")
    @JsonProperty("consumption_date")
    private Instant consumptionDate;

    @NotNull
    @Column(name = "user_ref")
    @JsonProperty("user_ref")
    private String userRef;

    @NotNull
    private Double units;

    @NotNull
    @Column(name = "token_ref")
    @JsonProperty("token_ref")
    private String tokenRef;

    public CreditsConsumption() {}

    public CreditsConsumption(String ref, Instant consumptionDate,
                              String userRef, Double units, String tokenRef) {
        setRef(ref);
        setConsumptionDate(consumptionDate);
        setUserRef(userRef);
        setUnits(units);
        setTokenRef(tokenRef);
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

    public Instant getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(Instant consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    public String getUserRef() {
        return userRef;
    }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }

    public Double getUnits() {
        return units;
    }

    public void setUnits(Double units) {
        this.units = units;
    }

    public String getTokenRef() {
        return tokenRef;
    }

    public void setTokenRef(String tokenRef) {
        this.tokenRef = tokenRef;
    }
}
