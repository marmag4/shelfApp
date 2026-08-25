package gr.aueb.shelfapp.repository;

import gr.aueb.shelfapp.entity.WasteLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteLogRepository extends JpaRepository<WasteLog, Long> {

    // Navigates WasteLog -> Product -> User automatically. Handy for statistics later.
    List<WasteLog> findByProduct_User_Id(Long userId);
}
