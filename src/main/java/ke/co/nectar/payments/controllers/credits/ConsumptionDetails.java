package ke.co.nectar.payments.controllers.credits;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ConsumptionDetails {

    @JsonProperty("consumption_date")
    private Instant consumptionDate;

    @JsonProperty("units")
    private Double units;

    @JsonProperty("token_ref")
    private String tokenRef;

    public ConsumptionDetails() {}

    public ConsumptionDetails(Instant consumptionDate,
                              Double units,
                              String tokenRef) {
        setConsumptionDate(consumptionDate);
        setUnits(units);
        setTokenRef(tokenRef);
    }

    public Instant getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(Instant consumptionDate) {
        this.consumptionDate = consumptionDate;
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
