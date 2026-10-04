package io.github.alexxfromgit.taf.cucumber.core.lint;

/** One finding of the {@link FeatureLinter}. */
public record LintViolation(String location, String problem) {

    @Override
    public String toString() {
        return location + ": " + problem;
    }
}
