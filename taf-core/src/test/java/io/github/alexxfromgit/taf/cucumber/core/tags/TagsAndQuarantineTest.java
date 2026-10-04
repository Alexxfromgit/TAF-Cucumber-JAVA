package io.github.alexxfromgit.taf.cucumber.core.tags;

import io.github.alexxfromgit.taf.cucumber.core.failure.FrameworkException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TagsAndQuarantineTest {

    private static final List<String> TAGS = List.of("@smoke", "@epic=Web_shop", "@owner=shop-team",
            "@known-issue=GH_12", "@quarantined=2026-10-10");

    @Test
    void valueTagsAreParsedRaw() {
        assertThat(Tags.value(TAGS, Tags.EPIC)).contains("Web_shop");
        assertThat(Tags.value(TAGS, Tags.KNOWN_ISSUE)).contains("GH_12");
        assertThat(Tags.value(TAGS, "severity")).isEmpty();
        assertThat(Tags.has(TAGS, "smoke")).isTrue();
        assertThat(Tags.has(TAGS, Tags.OWNER)).isTrue();
        assertThat(Tags.has(List.of("@owner="), Tags.OWNER)).as("empty value does not count").isFalse();
    }

    @Test
    void quarantineIsActiveUntilItsDate() {
        assertThat(Quarantine.activeUntil(TAGS, LocalDate.of(2026, 10, 9))).contains(LocalDate.of(2026, 10, 10));
        assertThat(Quarantine.activeUntil(TAGS, LocalDate.of(2026, 10, 10))).isEmpty();
        assertThat(Quarantine.activeUntil(List.of("@smoke"), LocalDate.of(2026, 1, 1))).isEmpty();
        assertThatThrownBy(() -> Quarantine.activeUntil(List.of("@quarantined=next-week"), LocalDate.now()))
                .isInstanceOf(FrameworkException.class)
                .hasMessageContaining("ISO date");
    }
}
