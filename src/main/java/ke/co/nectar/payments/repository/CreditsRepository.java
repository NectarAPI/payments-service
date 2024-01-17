package ke.co.nectar.payments.repository;

import ke.co.nectar.payments.entity.Credits;
import ke.co.nectar.payments.service.credits.impl.TimelineRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface CreditsRepository extends JpaRepository<Credits, Long> {

    Credits findByRef(String ref);

    List<Credits> getByUserRef(String userRef);

    Credits getByRef(String ref);

    @Transactional
    @Query("Select COALESCE(SUM(c.units), 0) FROM Credits c where c.userRef = :userRef")
    Double getUnitsForUserRef(@Param("userRef") String userRef);

    @Transactional
    @Query("Select COALESCE(SUM(c.value), 0) FROM Credits c where c.userRef = :userRef")
    Double getValueForUserRef(@Param("userRef") String userRef);

    @Query(value = "SELECT * FROM  (SELECT date_part('month', purchase_date\\:\\:date) AS month, date_part('year', purchase_date\\:\\:date) AS year, SUM(units) AS units FROM credits WHERE user_ref = :userRef GROUP BY month, year ORDER BY year,month ASC) AS res \n" +
            "\n" +
            "OFFSET \n" +
            "\n" +
            "CASE \n" +
            "\n" +
            "WHEN ((\n" +
            "\n" +
            "SELECT COUNT(*) FROM  (SELECT date_part('month', purchase_date\\:\\:date) AS month, date_part('year', purchase_date\\:\\:date) AS year, SUM(units) AS units FROM credits WHERE user_ref = :userRef GROUP BY month, year ORDER BY year,month ASC)\n" +
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
            "SELECT COUNT(*) FROM  (SELECT date_part('month', purchase_date\\:\\:date) AS month, date_part('year', purchase_date\\:\\:date) AS year, SUM(units) AS units FROM credits WHERE user_ref = :userRef GROUP BY month, year ORDER BY year,month ASC)\n" +
            " as RES\n" +
            "\n" +
            ") - :months )\n" +
            "\n" +
            "END;",
            nativeQuery = true)
    List<TimelineRequest> getTimelineRequests(@Param("userRef") String userRef,
                                              @Param("months") int months);

}
