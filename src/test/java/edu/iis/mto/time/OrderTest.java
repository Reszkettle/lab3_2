package edu.iis.mto.time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTest {

    @Mock
    private Clock clock;

    private static long INVALID_HOURS_PERIOD = Order.VALID_PERIOD_HOURS + 1;

    private Order order;

    @BeforeEach
    void setUp() {
        this.order = new Order(clock);
    }

    @Test
    void shouldCancelExpiredOrderOnConfirmation() {
        // given
        Order.State expectedState = Order.State.CANCELLED;
        Instant submissionTime = Instant.EPOCH;
        Instant confirmationTime = submissionTime.plus(INVALID_HOURS_PERIOD, ChronoUnit.HOURS);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(submissionTime).thenReturn(confirmationTime);

        // when
        order.submit();

        // then
        assertThrows(OrderExpiredException.class, () -> order.confirm());
        assertEquals(expectedState, order.getOrderState());
    }

}
