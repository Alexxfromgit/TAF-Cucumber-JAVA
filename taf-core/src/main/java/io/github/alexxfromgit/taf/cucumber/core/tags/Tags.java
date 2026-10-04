package io.github.alexxfromgit.taf.cucumber.core.tags;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Value tags in the form {@code @name=value}. Allure understands {@code @severity=}, {@code @owner=},
 * {@code @issue=} and {@code @tmsLink=} natively; the framework adds:
 * <ul>
 *     <li>{@code @epic=Web_shop} - top level of the Allure behaviours tree (on the Feature);</li>
 *     <li>{@code @known-issue=GH-12} - failures are reported under "Known issues" with a link;</li>
 *     <li>{@code @quarantined=2026-11-15} - the scenario is skipped until that date.</li>
 * </ul>
 * Tags cannot contain spaces in Gherkin: write {@code @epic=Web_shop}; the epic is shown as "Web shop".
 */
public final class Tags {

    public static final String EPIC = "epic";
    public static final String OWNER = "owner";
    public static final String KNOWN_ISSUE = "known-issue";
    public static final String QUARANTINED = "quarantined";

    private Tags() {
    }

    /** Raw value of the first {@code @name=value} among {@code tags}. */
    public static Optional<String> value(Collection<String> tags, String name) {
        return values(tags, name).stream().findFirst();
    }

    public static List<String> values(Collection<String> tags, String name) {
        String prefix = "@" + name + "=";
        return tags.stream()
                .filter(t -> t.startsWith(prefix) && t.length() > prefix.length())
                .map(t -> t.substring(prefix.length()))
                .toList();
    }

    public static boolean has(Collection<String> tags, String name) {
        return tags.contains("@" + name) || !values(tags, name).isEmpty();
    }
}
