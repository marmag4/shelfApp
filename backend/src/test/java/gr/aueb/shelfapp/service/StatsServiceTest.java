package gr.aueb.shelfapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gr.aueb.shelfapp.dto.MonthlyTrendDto;
import gr.aueb.shelfapp.dto.StatsDto;
import gr.aueb.shelfapp.dto.WasteReasonBreakdownDto;
import gr.aueb.shelfapp.entity.Donation;
import gr.aueb.shelfapp.entity.WasteLog;
import gr.aueb.shelfapp.entity.WasteReason;
import gr.aueb.shelfapp.repository.DonationRepository;
import gr.aueb.shelfapp.repository.ProductRepository;
import gr.aueb.shelfapp.repository.WasteLogRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the "monthly trend" and "waste by reason" parts of StatsService
 */
@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    private static final Long USER_ID = 1L;
    private static final DateTimeFormatter LABEL = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private WasteLogRepository wasteLogRepository;

    @InjectMocks
    private StatsService statsService;

    private WasteLog wasteLogOn(LocalDate date) {
        WasteLog log = mock(WasteLog.class);
        when(log.getWasteDate()).thenReturn(date);
        return log;
    }

    private Donation donationOn(LocalDate date) {
        Donation donation = mock(Donation.class);
        when(donation.getDonationDate()).thenReturn(date);
        return donation;
    }

    // getStats() always builds the monthly trend too, which calls
    // getWasteDate() on every waste log - so even reason-focused mocks need
    // a (throwaway) date stubbed, or that call NPEs on the unstubbed mock.
    private WasteLog wasteLogWithReason(WasteReason reason) {
        WasteLog log = mock(WasteLog.class);
        when(log.getWasteDate()).thenReturn(LocalDate.now());
        when(log.getReason()).thenReturn(reason);
        return log;
    }

    @Test
    void monthlyTrend_coversLast6MonthsOldestFirst_zeroFillingEmptyOnes() {
        when(productRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(wasteLogRepository.findByProduct_User_Id(USER_ID)).thenReturn(List.of());
        when(donationRepository.findByProduct_User_Id(USER_ID)).thenReturn(List.of());

        List<MonthlyTrendDto> trend = statsService.getStats(USER_ID).monthlyTrend();

        assertEquals(6, trend.size());
        assertEquals(YearMonth.now().minusMonths(5).format(LABEL), trend.get(0).month());
        assertEquals(YearMonth.now().format(LABEL), trend.get(5).month());
        trend.forEach(month -> {
            assertEquals(0, month.wastedCount());
            assertEquals(0, month.donatedCount());
        });
    }

    @Test
    void monthlyTrend_groupsSameMonthEntriesTogether() {
        LocalDate today = LocalDate.now();
        // Built as separate statements (not inline inside List.of(...)) on purpose - nesting a
        // mock()+when() call inside the argument list of another still-open when(...).thenReturn(...)
        // confuses Mockito's stubbing state (it throws UnfinishedStubbingException).
        WasteLog wasteToday = wasteLogOn(today);
        WasteLog wasteEarlierThisMonth = wasteLogOn(today.withDayOfMonth(1));
        Donation donationToday = donationOn(today);

        when(productRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(wasteLogRepository.findByProduct_User_Id(USER_ID)).thenReturn(
                List.of(wasteToday, wasteEarlierThisMonth));
        when(donationRepository.findByProduct_User_Id(USER_ID)).thenReturn(List.of(donationToday));

        MonthlyTrendDto currentMonth = statsService.getStats(USER_ID).monthlyTrend().get(5);

        assertEquals(2, currentMonth.wastedCount());
        assertEquals(1, currentMonth.donatedCount());
    }

    @Test
    void monthlyTrend_dropsEntriesOlderThanTheSixMonthWindow_butKeepsThemInLifetimeTotals() {
        LocalDate tooOld = LocalDate.now().minusMonths(8);
        WasteLog oldWasteLog = wasteLogOn(tooOld);

        when(productRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(wasteLogRepository.findByProduct_User_Id(USER_ID)).thenReturn(List.of(oldWasteLog));
        when(donationRepository.findByProduct_User_Id(USER_ID)).thenReturn(List.of());

        StatsDto stats = statsService.getStats(USER_ID);

        long totalWastedInTrend = stats.monthlyTrend().stream().mapToLong(MonthlyTrendDto::wastedCount).sum();
        assertEquals(0, totalWastedInTrend);
        // Still counted in the lifetime total - the trend window just doesn't reach back that far.
        assertEquals(1, stats.totalWasteLogs());
    }

    @Test
    void wasteByReason_zeroFillsReasonsNeverLogged() {
        when(productRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(wasteLogRepository.findByProduct_User_Id(USER_ID)).thenReturn(List.of());
        when(donationRepository.findByProduct_User_Id(USER_ID)).thenReturn(List.of());

        List<WasteReasonBreakdownDto> breakdown = statsService.getStats(USER_ID).wasteByReason();

        assertEquals(4, breakdown.size());
        breakdown.forEach(row -> assertEquals(0, row.count()));
    }

    @Test
    void wasteByReason_groupsCountsByReason() {
        WasteLog expired1 = wasteLogWithReason(WasteReason.EXPIRED);
        WasteLog expired2 = wasteLogWithReason(WasteReason.EXPIRED);
        WasteLog spoiled = wasteLogWithReason(WasteReason.SPOILED);

        when(productRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(wasteLogRepository.findByProduct_User_Id(USER_ID)).thenReturn(
                List.of(expired1, expired2, spoiled));
        when(donationRepository.findByProduct_User_Id(USER_ID)).thenReturn(List.of());

        List<WasteReasonBreakdownDto> breakdown = statsService.getStats(USER_ID).wasteByReason();

        long expiredCount = breakdown.stream()
                .filter(row -> row.reason().equals("EXPIRED"))
                .findFirst().orElseThrow().count();
        long spoiledCount = breakdown.stream()
                .filter(row -> row.reason().equals("SPOILED"))
                .findFirst().orElseThrow().count();

        assertEquals(2, expiredCount);
        assertEquals(1, spoiledCount);
    }

    @Test
    void wasteByReason_sortsMostCommonReasonFirst() {
        WasteLog overbought = wasteLogWithReason(WasteReason.OVERBOUGHT);
        WasteLog expired1 = wasteLogWithReason(WasteReason.EXPIRED);
        WasteLog expired2 = wasteLogWithReason(WasteReason.EXPIRED);
        WasteLog expired3 = wasteLogWithReason(WasteReason.EXPIRED);
        WasteLog spoiled1 = wasteLogWithReason(WasteReason.SPOILED);
        WasteLog spoiled2 = wasteLogWithReason(WasteReason.SPOILED);

        when(productRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(wasteLogRepository.findByProduct_User_Id(USER_ID)).thenReturn(
                List.of(overbought, expired1, expired2, expired3, spoiled1, spoiled2));
        when(donationRepository.findByProduct_User_Id(USER_ID)).thenReturn(List.of());

        List<WasteReasonBreakdownDto> breakdown = statsService.getStats(USER_ID).wasteByReason();

        assertEquals("EXPIRED", breakdown.get(0).reason());
        assertEquals(3, breakdown.get(0).count());
        assertEquals("SPOILED", breakdown.get(1).reason());
        assertEquals(2, breakdown.get(1).count());
        assertEquals("OVERBOUGHT", breakdown.get(2).reason());
        assertEquals(1, breakdown.get(2).count());
        assertEquals("OTHER", breakdown.get(3).reason());
        assertEquals(0, breakdown.get(3).count());
    }
}
