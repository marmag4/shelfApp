package gr.aueb.shelfapp.repository;

import gr.aueb.shelfapp.entity.SharingPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharingPointRepository extends JpaRepository<SharingPoint, Long> {
}
