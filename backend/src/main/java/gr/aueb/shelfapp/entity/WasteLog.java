package gr.aueb.shelfapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** A record of a product that ended up thrown away. Maps to "waste_logs". */
@Entity
@Table(name = "waste_logs")
public class WasteLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "waste_date", nullable = false)
    private LocalDate wasteDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WasteReason reason;

    protected WasteLog() {
        // required by JPA, do not call directly
    }

    public WasteLog(Product product, WasteReason reason) {
        this.product = product;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDate getWasteDate() {
        return wasteDate;
    }

    public WasteReason getReason() {
        return reason;
    }
}
