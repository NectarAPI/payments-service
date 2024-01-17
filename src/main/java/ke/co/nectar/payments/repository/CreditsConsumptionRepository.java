package ke.co.nectar.payments.repository;

import ke.co.nectar.payments.entity.CreditsConsumption;
import ke.co.nectar.payments.service.credits.impl.TimelineRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface CreditsConsumptionRepository extends JpaRepository<CreditsConsumption, Long> {

    CreditsConsumption findByRef(String ref);

    List<CreditsConsumption> getByUserRef(String userRef);

    @Transactional
    @Query("SELECT COALESCE(SUM(c.units), 0) FROM CreditsConsumption c WHERE c.userRef = :userRef")
    Double getCreditsConsumptionUnitsForUser(@Param("userRef") String userRef);

    @Query(value = "SELECT * FROM  (SELECT date_part('month', consumption_date\\:\\:date) AS month, date_part('year', consumption_date\\:\\:date) AS year, SUM(units) AS units FROM credits_consumption WHERE user_ref = :userRef GROUP BY month, year ORDER BY year,month ASC) AS res \n" +
            "\n" +
            "OFFSET \n" +
            "\n" +
            "CASE \n" +
            "\n" +
            "WHEN ((\n" +
            "\n" +
            "SELECT COUNT(*) FROM  (SELECT date_part('month', consumption_date\\:\\:date) AS month, date_part('year', consumption_date\\:\\:date) AS year, SUM(units) AS units FROM credits_consumption WHERE user_ref = :userRef GROUP BY month, year ORDER BY year,month ASC)\n" +
            " as RES\n" +
            "\n" +
            ") - :months )\n" +
            "\n" +
            "< 0 THEN 0\n" +
            "\n" +
            "ELSE \n" +
            "\n" +
            "((\n" +
            "\n" +
            "SELECT COUNT(*) FROM  (SELECT date_part('month', consumption_date\\:\\:date) AS month, date_part('year', consumption_date\\:\\:date) AS year, SUM(units) AS units FROM credits_consumption WHERE user_ref = :userRef GROUP BY month, year ORDER BY year,month ASC)\n" +
            " as RES\n" +
            "\n" +
            ") - :months )\n" +
            "\n" +
            "END;",
    nativeQuery = true)
    List<TimelineRequest> getTimelineRequests(@Param("userRef") String userRef,
                                              @Param("months") int months);
}
