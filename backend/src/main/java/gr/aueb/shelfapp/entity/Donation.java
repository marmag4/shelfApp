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

/** A product given away to a sharing point. Maps to "donations". */
@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sharing_point_id", nullable = false)
    private SharingPoint sharingPoint;

    @Column(name = "donation_date", nullable = false)
    private LocalDate donationDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DonationStatus status = DonationStatus.PENDING;

    protected Donation() {
        // required by JPA, do not call directly
    }

    public Donation(Product product, SharingPoint sharingPoint) {
        this.product = product;
        this.sharingPoint = sharingPoint;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public SharingPoint getSharingPoint() {
        return sharingPoint;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
    }
}
