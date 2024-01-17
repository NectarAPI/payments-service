package ke.co.nectar.payments.repository;

import ke.co.nectar.payments.entity.UnitCurrencyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitCurrencyValueRepository extends JpaRepository<UnitCurrencyValue, Long> {

    UnitCurrencyValue findByCurrency(String currency);
}
