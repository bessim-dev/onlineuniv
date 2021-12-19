package tn.univ.onlineuniv.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.univ.onlineuniv.models.Rate;

public interface RateRepository extends JpaRepository<Rate,Long> {
}
