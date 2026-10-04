package io.github.alexxfromgit.taf.cucumber.core.allure;

import io.github.alexxfromgit.taf.cucumber.core.tags.Tags;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Link;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.util.ResultsUtils;

import java.util.List;

/**
 * Turns the framework's value tags into Allure metadata, right before each result is written (registered through
 * {@code META-INF/services/io.qameta.allure.listener.TestLifecycleListener}):
 * <ul>
 *     <li>{@code @epic=...} becomes the epic label (Allure's Cucumber plugin has no epic tag);</li>
 *     <li>{@code @known-issue=ID} adds an issue link and, when the scenario failed, prefixes the failure message with
 *     {@code [Known issue ID]} so the "Known issues" category picks it up.</li>
 * </ul>
 */
public class TafAllureListener implements TestLifecycleListener {

    static final String KNOWN_ISSUE_PREFIX = "[Known issue ";

    @Override
    public void beforeTestStop(TestResult result) {
        apply(result);
    }

    static void apply(TestResult result) {
        List<String> tags = result.getLabels().stream()
                .filter(l -> "tag".equals(l.getName()))
                .map(Label::getValue)
                .map(v -> v.startsWith("@") ? v : "@" + v)
                .toList();

        Tags.value(tags, Tags.EPIC).ifPresent(epic -> {
            result.getLabels().removeIf(l -> "epic".equals(l.getName()));
            result.getLabels().add(ResultsUtils.createEpicLabel(epic.replace('_', ' ')));
        });

        Tags.value(tags, Tags.KNOWN_ISSUE).ifPresent(issue -> {
            Link link = ResultsUtils.createIssueLink(issue);
            if (result.getLinks().stream().noneMatch(l -> issue.equals(l.getName()))) {
                result.getLinks().add(link);
            }
            if (result.getStatus() == Status.FAILED || result.getStatus() == Status.BROKEN) {
                StatusDetails details = result.getStatusDetails() == null ? new StatusDetails() : result.getStatusDetails();
                String message = details.getMessage() == null ? "" : details.getMessage();
                if (!message.startsWith(KNOWN_ISSUE_PREFIX)) {
                    details.setMessage(KNOWN_ISSUE_PREFIX + issue + "] " + message);
                }
                result.setStatusDetails(details);
            }
        });
    }
}
