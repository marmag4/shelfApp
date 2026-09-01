package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.MonthlyTrendDto;
import gr.aueb.shelfapp.dto.StatsDto;
import gr.aueb.shelfapp.dto.WasteReasonBreakdownDto;
import gr.aueb.shelfapp.entity.Donation;
import gr.aueb.shelfapp.entity.Product;
import gr.aueb.shelfapp.entity.ProductStatus;
import gr.aueb.shelfapp.entity.WasteLog;
import gr.aueb.shelfapp.entity.WasteReason;
import gr.aueb.shelfapp.repository.DonationRepository;
import gr.aueb.shelfapp.repository.ProductRepository;
import gr.aueb.shelfapp.repository.WasteLogRepository;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Feature #5 from the project idea: a simple summary of how the logged-in
 * user is doing overall - how many products stayed active, got eaten, got
 * donated, or ended up wasted.
 *
 * "wastePercentage" is the headline number: of all the products that are no
 * longer active (eaten, donated, or wasted), what share ended up wasted?
 * That's the number that actually reflects whether the user is improving.
 *
 * "monthlyTrend" is the "over time" part promised by the README - the last
 * 6 months of waste vs. donations, so the Charts tab can show whether
 * things are actually getting better rather than just a snapshot.
 *
 * "wasteByReason" is the "why" behind the waste - a breakdown of every
 * WasteLog's reason (EXPIRED/SPOILED/OVERBOUGHT/OTHER), most common first,
 * so the user can see what's actually driving their waste rate.
 */
@Service
public class StatsService {

    private static final int TREND_MONTHS = 6;
    private static final DateTimeFormatter MONTH_LABEL_FORMAT =
            DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

    private final ProductRepository productRepository;
    private final DonationRepository donationRepository;
    private final WasteLogRepository wasteLogRepository;

    public StatsService(ProductRepository productRepository,
                         DonationRepository donationRepository,
                         WasteLogRepository wasteLogRepository) {
        this.productRepository = productRepository;
        this.donationRepository = donationRepository;
        this.wasteLogRepository = wasteLogRepository;
    }

    public StatsDto getStats(Long userId) {
        List<Product> products = productRepository.findByUserId(userId);

        long active = countByStatus(products, ProductStatus.ACTIVE);
        long consumed = countByStatus(products, ProductStatus.CONSUMED);
        long donated = countByStatus(products, ProductStatus.DONATED);
        long wasted = countByStatus(products, ProductStatus.WASTED);

        long resolved = consumed + donated + wasted;
        double wastePercentage = resolved > 0 ? (wasted * 100.0 / resolved) : 0.0;
        // round to 1 decimal place, e.g. 33.3 instead of 33.33333333333333
        wastePercentage = Math.round(wastePercentage * 10.0) / 10.0;

        List<Donation> donations = donationRepository.findByProduct_User_Id(userId);
        List<WasteLog> wasteLogs = wasteLogRepository.findByProduct_User_Id(userId);

        return new StatsDto(
                products.size(),
                active,
                consumed,
                donated,
                wasted,
                donations.size(),
                wasteLogs.size(),
                wastePercentage,
                buildMonthlyTrend(wasteLogs, donations),
                buildWasteByReason(wasteLogs)
        );
    }

    private long countByStatus(List<Product> products, ProductStatus status) {
        return products.stream().filter(p -> p.getStatus() == status).count();
    }

    /**
     * Buckets waste logs and donations into the last TREND_MONTHS calendar
     * months (oldest first, current month last), zero-filling any month
     * that had no activity so the chart always has a full, evenly-spaced
     * x-axis to draw - and silently drops anything older than the window,
     * since the trend is meant to show recent behaviour, not full history
     * (that's already covered by the lifetime totals above).
     */
    private List<MonthlyTrendDto> buildMonthlyTrend(List<WasteLog> wasteLogs, List<Donation> donations) {
        YearMonth currentMonth = YearMonth.now();

        List<YearMonth> months = new ArrayList<>();
        for (int i = TREND_MONTHS - 1; i >= 0; i--) {
            months.add(currentMonth.minusMonths(i));
        }

        Map<YearMonth, Long> wastedByMonth = wasteLogs.stream()
                .collect(Collectors.groupingBy(w -> YearMonth.from(w.getWasteDate()), Collectors.counting()));
        Map<YearMonth, Long> donatedByMonth = donations.stream()
                .collect(Collectors.groupingBy(d -> YearMonth.from(d.getDonationDate()), Collectors.counting()));

        return months.stream()
                .map(month -> new MonthlyTrendDto(
                        month.format(MONTH_LABEL_FORMAT),
                        wastedByMonth.getOrDefault(month, 0L),
                        donatedByMonth.getOrDefault(month, 0L)))
                .toList();
    }

    /**
     * Counts waste logs per WasteReason - always all 4 reasons (0 for any
     * never used), sorted with the most common reason first so the chart
     * reads as a ranked "top reasons" list rather than a fixed enum order.
     */
    private List<WasteReasonBreakdownDto> buildWasteByReason(List<WasteLog> wasteLogs) {
        Map<WasteReason, Long> countByReason = wasteLogs.stream()
                .collect(Collectors.groupingBy(WasteLog::getReason, Collectors.counting()));

        return Arrays.stream(WasteReason.values())
                .map(reason -> new WasteReasonBreakdownDto(reason.name(), countByReason.getOrDefault(reason, 0L)))
                .sorted(Comparator.comparingLong(WasteReasonBreakdownDto::count).reversed())
                .toList();
    }
}
