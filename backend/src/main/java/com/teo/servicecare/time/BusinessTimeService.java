package com.teo.servicecare.time;

import java.time.*;

public final class BusinessTimeService {
    private BusinessTimeService() {
    }

    public static long workingSecondsBetween(LocalDateTime start, LocalDateTime end,
            LocalTime dayStart, LocalTime dayEnd,
            boolean monFriOnly) {
        if (!end.isAfter(start))
            return 0L;
        LocalDate d = start.toLocalDate();
        LocalDate last = end.toLocalDate();
        long total = 0L;
        while (!d.isAfter(last)) {
            if (!monFriOnly || isMonToFri(d)) {
                LocalDateTime ds = LocalDateTime.of(d, dayStart);
                LocalDateTime de = LocalDateTime.of(d, dayEnd);
                LocalDateTime s = max(start, ds);
                LocalDateTime e = min(end, de);
                if (e.isAfter(s))
                    total += Duration.between(s, e).getSeconds();
            }
            d = d.plusDays(1);
        }
        return Math.max(total, 0);
    }

    public static LocalDateTime addWorkingSeconds(LocalDateTime start, long seconds,
            LocalTime dayStart, LocalTime dayEnd,
            boolean monFriOnly) {
        if (seconds <= 0)
            return start;
        LocalDateTime cur = start;
        long remaining = seconds;
        while (remaining > 0) {
            if (monFriOnly && !isMonToFri(cur.toLocalDate())) {
                cur = LocalDateTime.of(cur.toLocalDate().plusDays(1), dayStart);
                continue;
            }
            LocalDateTime windowStart = LocalDateTime.of(cur.toLocalDate(), dayStart);
            LocalDateTime windowEnd = LocalDateTime.of(cur.toLocalDate(), dayEnd);
            if (cur.isBefore(windowStart))
                cur = windowStart;
            if (!cur.isBefore(windowEnd)) {
                cur = LocalDateTime.of(cur.toLocalDate().plusDays(1), dayStart);
                continue;
            }
            long avail = Duration.between(cur, windowEnd).getSeconds();
            long take = Math.min(avail, remaining);
            cur = cur.plusSeconds(take);
            remaining -= take;
            if (remaining > 0) {
                cur = LocalDateTime.of(cur.toLocalDate().plusDays(1), dayStart);
            }
        }
        return cur;
    }

    private static boolean isMonToFri(LocalDate d) {
        int v = d.getDayOfWeek().getValue();
        return v >= 1 && v <= 5;
    }

    private static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) ? a : b;
    }

    private static LocalDateTime min(LocalDateTime a, LocalDateTime b) {
        return a.isBefore(b) ? a : b;
    }
}