package ke.co.nectar.payments.repository;

import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.service.payment.impl.PaymentsResultsDistributionCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentsRepository extends JpaRepository<Payment, Long> {

    Payment findByRef(String ref);

    @Query("SELECT COALESCE(SUM(p.value), 0) FROM Payment p WHERE p.userRef = :userRef")
    Double getPaymentsTotalForUserRef(@Param("userRef") String userRef);

    @Query(value = "SELECT sum(value) AS value, result_desc AS result FROM payments WHERE user_ref = :userRef GROUP BY result_desc",
            nativeQuery = true)
    List<PaymentsResultsDistributionCount> getPaymentsRequestsDistributionCount(@Param("userRef") String userRef);

}
