package ke.co.nectar.payments.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ke.co.nectar.payments.entity.audit.DateAudit;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    private String ref;

    @NotNull
    private Instant scheduled;

    private Instant fulfilled;

    @Column(name = "result_code")
    private String resultCode;

    @Column(name = "result_desc")
    private String resultDesc;

    @NotNull
    private String type;

    @NotNull
    private Double value;

    @NotNull
    @Column(name = "user_ref")
    private String userRef;

    public Payment() {}

    public Payment(String ref, Instant scheduled, Instant fulfilled,
                   String resultCode, String resultDesc,
                   String type, Double value, String userRef) {
        setRef(ref);
        setScheduled(scheduled);
        setFulfilled(fulfilled);
        setResultCode(resultCode);
        setResultDesc(resultDesc);
        setType(type);
        setValue(value);
        setUserRef(userRef);
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

    public Instant getScheduled() {
        return scheduled;
    }

    public void setScheduled(Instant scheduled) {
        this.scheduled = scheduled;
    }

    public Instant getFulfilled() {
        return fulfilled;
    }

    public void setFulfilled(Instant fulfilled) {
        this.fulfilled = fulfilled;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUserRef() {
        return userRef;
    }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }
}
