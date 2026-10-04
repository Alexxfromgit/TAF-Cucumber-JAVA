package io.github.alexxfromgit.taf.cucumber.core.lint;

import io.github.alexxfromgit.taf.cucumber.core.config.SecretsGuard;
import io.github.alexxfromgit.taf.cucumber.core.config.TafConfig;
import io.github.alexxfromgit.taf.cucumber.core.failure.FrameworkException;
import io.github.alexxfromgit.taf.cucumber.core.tags.Tags;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Static checks that keep a growing feature suite navigable and safe to publish:
 * <ul>
 *     <li>every Feature has an {@code @epic=} tag (where does it belong in the report?);</li>
 *     <li>every Scenario has an {@code @owner=} tag, directly or inherited from its Feature/Rule;</li>
 *     <li>every {@code @quarantined=} date is valid and at most {@code lint.quarantine.max-days} (90) away;</li>
 *     <li>no {@code .properties} file in the project contains a secret-like value.</li>
 * </ul>
 * Run it as a plain JUnit test: {@code FeatureLinter.onClasspath().assertClean();}
 * Toggle rules with {@code lint.require-epic}, {@code lint.require-owner}, {@code lint.secrets}.
 */
public final class FeatureLinter {

    private FeatureLinter() {
    }

    public static FeatureLinter onClasspath() {
        return new FeatureLinter();
    }

    public List<LintViolation> check() {
        TafConfig config = TafConfig.get();
        List<LintViolation> violations = new ArrayList<>();
        try (ScanResult scan = new ClassGraph().disableJarScanning().scan()) {
            for (Resource feature : scan.getResourcesWithExtension("feature")) {
                if (feature.getClasspathElementFile() != null && feature.getClasspathElementFile().isDirectory()) {
                    lintFeature(feature.getPath(), read(feature), config, violations);
                }
            }
            if (config.bool("lint.secrets", true)) {
                lintProperties(scan, violations);
            }
        }
        return violations;
    }

    public void assertClean() {
        List<LintViolation> violations = check();
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder("Feature lint found ").append(violations.size())
                    .append(" problem(s):");
            violations.forEach(v -> message.append("\n  ").append(v));
            throw new FrameworkException(message.toString());
        }
    }

    /** Lints one feature file's text; visible for tests. */
    static void lintFeature(String path, String text, TafConfig config, List<LintViolation> out) {
        List<String> pendingTags = new ArrayList<>();
        List<String> featureTags = List.of();
        List<String> ruleTags = List.of();
        String[] lines = text.split("\\R");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("@")) {
                pendingTags.addAll(Arrays.asList(line.replaceAll("\\s+#.*$", "").split("\\s+")));
                continue;
            }
            String location = path + ":" + (i + 1);
            if (line.startsWith("Feature:")) {
                featureTags = List.copyOf(pendingTags);
                if (config.bool("lint.require-epic", true) && !Tags.has(featureTags, Tags.EPIC)) {
                    out.add(new LintViolation(location, "Feature has no @epic= tag (where does it belong in the report?)"));
                }
                checkQuarantine(location, featureTags, config, out);
            } else if (line.startsWith("Rule:")) {
                ruleTags = List.copyOf(pendingTags);
                checkQuarantine(location, ruleTags, config, out);
            } else if (line.matches("(Scenario|Scenario Outline|Scenario Template|Example):.*")) {
                List<String> all = new ArrayList<>(featureTags);
                all.addAll(ruleTags);
                all.addAll(pendingTags);
                String scenario = line.substring(line.indexOf(':') + 1).trim();
                if (config.bool("lint.require-owner", true) && !Tags.has(all, Tags.OWNER)) {
                    out.add(new LintViolation(location, "Scenario '" + scenario + "' has no @owner= tag (who maintains it?)"));
                }
                checkQuarantine(location, pendingTags, config, out);
            }
            if (!line.isEmpty() && !line.startsWith("#")) {
                pendingTags.clear();
            }
        }
    }

    private static void checkQuarantine(String location, List<String> tags, TafConfig config, List<LintViolation> out) {
        for (String value : Tags.values(tags, Tags.QUARANTINED)) {
            int maxDays = config.integer("lint.quarantine.max-days", 90);
            try {
                LocalDate until = LocalDate.parse(value);
                if (until.isAfter(LocalDate.now().plusDays(maxDays))) {
                    out.add(new LintViolation(location, "@quarantined=" + value + " is more than " + maxDays
                            + " days away - fix or delete the scenario instead"));
                }
            } catch (DateTimeParseException e) {
                out.add(new LintViolation(location, "@quarantined=" + value + " is not an ISO date (yyyy-MM-dd)"));
            }
        }
    }

    private static void lintProperties(ScanResult scan, List<LintViolation> out) {
        for (Resource resource : scan.getResourcesWithExtension("properties")) {
            if (resource.getClasspathElementFile() == null || !resource.getClasspathElementFile().isDirectory()) {
                continue;
            }
            Properties properties = new Properties();
            try (InputStream in = resource.open()) {
                properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            } catch (IOException e) {
                out.add(new LintViolation(resource.getPath(), "cannot be read: " + e.getMessage()));
                continue;
            }
            Map<String, String> values = new LinkedHashMap<>();
            properties.stringPropertyNames().forEach(k -> values.put(k, properties.getProperty(k)));
            SecretsGuard.findViolations(values).forEach(key -> out.add(new LintViolation(resource.getPath(),
                    "'" + key + "' looks like a secret - provide it as environment variable " + TafConfig.toEnvName(key))));
        }
    }

    private static String read(Resource resource) {
        try (InputStream in = resource.open()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FrameworkException("Cannot read " + resource.getPath(), e);
        }
    }
}
