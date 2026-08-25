package gr.aueb.shelfapp.repository;

import gr.aueb.shelfapp.entity.Donation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByProduct_User_Id(Long userId);
}
