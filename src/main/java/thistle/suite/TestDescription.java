package thistle.suite;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class TestDescription {
    public final ImmutableList<String> descriptions;
    public final ImmutableList<String> conditions;
    public final String testCase;
    public final ImmutableList<String> after;

    public TestDescription(ImmutableList<String> descriptions, ImmutableList<String> conditions, String testCase, ImmutableList<String> after) {
        this.descriptions = descriptions;
        this.conditions = conditions;
        this.testCase = testCase;
        this.after = after;
    }

    public static TestDescription testDescription(List<String> descriptions, List<String> conditions, String testCase, List<String> after) {
        return new TestDescription(ImmutableList.<String>builder().addAll(descriptions).build(),
                ImmutableList.<String>builder().addAll(conditions).build(),
                testCase,
                ImmutableList.<String>builder().addAll(after).build());
    }
}
