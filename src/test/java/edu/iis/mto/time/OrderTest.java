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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTest {

    @Mock
    private Clock clock;

    private static final long INVALID_HOURS_PERIOD = Order.VALID_PERIOD_HOURS + 1;

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
        try {
            order.confirm();
        } catch (RuntimeException ignored) {
        }

        assertEquals(expectedState, order.getOrderState());
    }

    @Test
    void shouldConfirmActiveOrderOnConfirmation() {
        Order.State expectedState = Order.State.CONFIRMED;
        Instant submissionTime = Instant.EPOCH;
        Instant confirmationTime = submissionTime.plus(Order.VALID_PERIOD_HOURS, ChronoUnit.HOURS);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(submissionTime).thenReturn(confirmationTime);

        // when
        order.submit();
        try {
            order.confirm();
        } catch (RuntimeException ignored) {
        }

        // then
        assertEquals(expectedState, order.getOrderState());
    }

    @Test
    void shouldThrowOrderExpiredExceptionWhenOrderIsExpiredOnConfirmation() {
        // given
        Instant submissionTime = Instant.EPOCH;
        Instant confirmationTime = submissionTime.plus(INVALID_HOURS_PERIOD, ChronoUnit.HOURS);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(submissionTime).thenReturn(confirmationTime);

        // when
        order.submit();

        // then
        assertThrows(OrderExpiredException.class, () -> order.confirm());
    }

    @Test
    void shouldNotThrowExceptionWhenOrderIsActiveOnConfirmation() {
        // given
        Instant submissionTime = Instant.EPOCH;
        Instant confirmationTime = submissionTime.plus(Order.VALID_PERIOD_HOURS, ChronoUnit.HOURS);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(submissionTime).thenReturn(confirmationTime);

        // when
        order.submit();

        // then
        assertDoesNotThrow(() -> order.confirm());
    }



}
