package thistle.runner;

import thistle.suite.TestDescription;

public interface TestDescriptionProcessor {
    String getDescription(TestDescription testDescription);
}
