package io.github.alexxfromgit.taf.cucumber.core.tags;

import io.github.alexxfromgit.taf.cucumber.core.failure.FrameworkException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Optional;

/**
 * {@code @quarantined=2026-11-15}: the scenario is skipped until that date (exclusive) and then runs again
 * automatically, so quarantine can never silently become permanent. Put the reason in a comment above the tag.
 * The feature linter rejects dates more than {@code lint.quarantine.max-days} (default 90) away.
 */
public final class Quarantine {

    private Quarantine() {
    }

    /** The quarantine end date if the scenario is quarantined today. */
    public static Optional<LocalDate> activeUntil(Collection<String> tags, LocalDate today) {
        return Tags.value(tags, Tags.QUARANTINED).map(Quarantine::parse).filter(today::isBefore);
    }

    public static LocalDate parse(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new FrameworkException("@quarantined=" + value + " must be an ISO date like 2026-11-15", e);
        }
    }
}
