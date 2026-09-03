package Manager_vnd.Manager.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

@Component
public class VietnamTime {

    public static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final Clock clock;

    public VietnamTime() {
        this(Clock.system(ZONE));
    }

    public VietnamTime(Clock clock) {
        this.clock = clock.withZone(ZONE);
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    public LocalDate firstDayOfMonth() {
        return today().withDayOfMonth(1);
    }

    public ZonedDateTime now() {
        return ZonedDateTime.now(clock);
    }
}
