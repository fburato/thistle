package thistle.runner;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import thistle.suite.TestDescription;

import java.util.ArrayList;
import java.util.List;

public class DefaultTestDescriptionProcessor implements TestDescriptionProcessor {

    public String getDescription(TestDescription testDescription) {
        return getDescriptions(testDescription.descriptions) +
                getConditions(testDescription.conditions) +
                testDescription.testCase +
                getAfter(testDescription.after);
    }

    private String getDescriptions(ImmutableList<String> descriptions) {
        return notEmptyJoiner(descriptions, ", ", "", ": ");
    }

    private String getConditions(ImmutableList<String> conditions) {
        return notEmptyJoiner(conditions, " and ", "When ", ", then ");
    }

    private String getAfter(ImmutableList<String> after) {
        return notEmptyJoiner(after, " and ", ". Finally ", "");
    }

    private String notEmptyJoiner(ImmutableList<String> list, String joiningSequence, String head, String tail) {
        List<String> notEmpty = getNotEmpty(list);
        if (notEmpty.isEmpty()) {
            return "";
        } else {
            return head + Joiner.on(joiningSequence).join(notEmpty) + tail;
        }
    }

    private List<String> getNotEmpty(ImmutableList<String> list) {
        List<String> result = new ArrayList<String>();
        for (String s : list) {
            if (!Strings.isNullOrEmpty(s)) {
                result.add(s);
            }
        }
        return result;
    }
}
