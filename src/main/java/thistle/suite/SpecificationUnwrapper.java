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
        final List<String> conditions = Lists.transform(specification.premises, this.<WhenBlock>getDescription());
        final List<String> after = Lists.transform(specification.finallyDo, this.<FinallyBlock>getDescription());
        final List<TestCase> localTestCases = unwrapSpecification(specification, conditions, after);
        final List<TestCase> nestedTestCases = unwrapSubspecifications(specification, conditions, after);
        return ImmutableList.<TestCase>builder().addAll(localTestCases).addAll(nestedTestCases).build();
    }

    private List<TestCase> unwrapSpecification(Specification specification, List<String> premisesDescriptions, List<String> finallyDescriptions) {
        final List<TestCase> result = new ArrayList<TestCase>();
        for (ThenBlock thenBlock : specification.cases) {
            final List<Block> blockSequence = new ArrayList<Block>();
            final TestDescription testDescription = TestDescription.testDescription(ImmutableList.of(specification.describe()), premisesDescriptions, thenBlock.describe(), finallyDescriptions);
            blockSequence.add(specification.initialisation);
            blockSequence.addAll(specification.premises);
            blockSequence.add(thenBlock);
            blockSequence.addAll(specification.finallyDo);
            result.add(testCase(testDescription, new BlockSequence(blockSequence)));
        }
        return result;
    }

    private List<TestCase> unwrapSubspecifications(Specification specification, List<String> premisesDescriptions, List<String> finallyDescriptions) {
        final List<TestCase> result = new ArrayList<TestCase>();
        for (Specification subSpecification : specification.getSubSpecifications()) {
            final List<TestCase> subSpecificationTestCases = unwrap(subSpecification);
            for (TestCase subTest : subSpecificationTestCases) {
                final BlockSequence initialisation = new BlockSequence(Collections.singletonList(specification.initialisation));
                final BlockSequence premises = new BlockSequence(specification.premises);
                final BlockSequence finallyDo = new BlockSequence(specification.finallyDo);
                final List<String> subDescription = ImmutableList.<String>builder().add(specification.describe()).addAll(subTest.testDescription.descriptions).build();
                final List<String> subConditions = ImmutableList.<String>builder().addAll(premisesDescriptions).addAll(subTest.testDescription.conditions).build();
                final List<String> subAfter = ImmutableList.<String>builder().addAll(subTest.testDescription.after).addAll(finallyDescriptions).build();
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
