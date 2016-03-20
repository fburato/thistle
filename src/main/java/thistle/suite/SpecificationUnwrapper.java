package thistle.suite;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import thistle.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static thistle.suite.TestCase.testCase;

public class SpecificationUnwrapper {

    public List<TestCase> unwrap(Specification specification) {
        final List<TestCase> result = new ArrayList<TestCase>();
        final List<String> conditions = Lists.transform(specification.premises, this.<WhenBlock>getDescription());
        final List<String> after = Lists.transform(specification.finallyDo, this.<FinallyBlock>getDescription());
        for (ThenBlock thenBlock : specification.cases) {
            final List<Block> blockSequence = new ArrayList<Block>();
            final TestDescription testDescription = TestDescription.testDescription(ImmutableList.of(specification.describe()), conditions, thenBlock.describe(), after);
            blockSequence.add(specification.initialisation);
            blockSequence.addAll(specification.premises);
            blockSequence.add(thenBlock);
            blockSequence.addAll(specification.finallyDo);
            result.add(testCase(testDescription, new BlockSequence(blockSequence)));

        }

        for (Specification subSpecification : specification.getSubSpecifications()) {
            final List<TestCase> subSpecificationTestCases = unwrap(subSpecification);
            for (TestCase subTest : subSpecificationTestCases) {
                final BlockSequence initialisation = new BlockSequence(Collections.singletonList(specification.initialisation));
                final BlockSequence premises = new BlockSequence(specification.premises);
                final BlockSequence finallyDo = new BlockSequence(specification.finallyDo);
                List<String> subDescription = ImmutableList.<String>builder().add(specification.describe()).addAll(subTest.testDescription.descriptions).build();
                List<String> subConditions = ImmutableList.<String>builder().addAll(conditions).addAll(subTest.testDescription.conditions).build();
                List<String> subAfter = ImmutableList.<String>builder().addAll(subTest.testDescription.after).addAll(after).build();
                result.add(testCase(TestDescription.testDescription(subDescription, subConditions, subTest.testDescription.testCase, subAfter), initialisation.catenate(premises).catenate(subTest.testExecution).catenate(finallyDo)));
            }
        }
        return result;
    }

    private <T extends DescribableBlock> Function<T, String> getDescription() {
        return new Function<T, String>() {
            @Override
            public String apply(T describableBlock) {
                return describableBlock.describe();
            }
        };
    }
}
