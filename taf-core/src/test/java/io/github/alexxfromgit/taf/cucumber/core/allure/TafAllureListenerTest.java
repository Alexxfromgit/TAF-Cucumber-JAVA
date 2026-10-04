package io.github.alexxfromgit.taf.cucumber.core.allure;

import io.qameta.allure.model.Label;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.TestResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TafAllureListenerTest {

    @Test
    void epicTagBecomesEpicLabel() {
        TestResult result = result(Status.PASSED, "@epic=Web_shop", "smoke");

        TafAllureListener.apply(result);

        assertThat(result.getLabels()).anyMatch(l -> l.getName().equals("epic") && l.getValue().equals("Web shop"));
    }

    @Test
    void failedKnownIssueIsLinkedAndPrefixed() {
        TestResult result = result(Status.FAILED, "@known-issue=GH-12");
        result.setStatusDetails(new StatusDetails().setMessage("expected [2] but was [1]"));

        TafAllureListener.apply(result);
        TafAllureListener.apply(result);   // idempotent

        assertThat(result.getStatusDetails().getMessage()).isEqualTo("[Known issue GH-12] expected [2] but was [1]");
        assertThat(result.getLinks()).singleElement().satisfies(link -> {
            assertThat(link.getName()).isEqualTo("GH-12");
            assertThat(link.getType()).isEqualTo("issue");
        });
    }

    @Test
    void passingKnownIssueOnlyGetsTheLink() {
        TestResult result = result(Status.PASSED, "@known-issue=GH-12");

        TafAllureListener.apply(result);

        assertThat(result.getStatusDetails()).isNull();
        assertThat(result.getLinks()).hasSize(1);
    }

    private static TestResult result(Status status, String... tags) {
        List<Label> labels = new ArrayList<>();
        for (String tag : tags) {
            labels.add(new Label().setName("tag").setValue(tag));
        }
        return new TestResult().setStatus(status).setLabels(labels).setLinks(new ArrayList<>());
    }
}
