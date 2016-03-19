package thistle.suite;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import thistle.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static thistle.suite.TestCase.testCase;

public class SpecificationUnwrapper {

    private final static Joiner AND_JOINER = Joiner.on(" and ");
    private final static String WHEN = " when ";
    private final static String THEN = " then ";
    private final static String THEN_SUBTEST = " then, ";

    public List<TestCase> unwrap(Specification specification) {
        List<TestCase> result = new ArrayList<TestCase>();
        for (ThenBlock thenBlock : specification.cases) {
            List<Block> blockSequence = new ArrayList<Block>();
            String description = specification.description + WHEN + getDescriptions(specification.premises) + THEN + thenBlock.describe() + finallyDescription(specification.finallyDo);
            blockSequence.add(specification.initialisation);
            blockSequence.addAll(specification.premises);
            blockSequence.add(thenBlock);
            blockSequence.addAll(specification.finallyDo);
            result.add(testCase(description, new BlockSequence(blockSequence)));

        }

        for (Specification subSpecification : specification.getSubSpecifications()) {
            List<TestCase> subSpecificationTestCases = unwrap(subSpecification);
            for (TestCase subTest : subSpecificationTestCases) {
                BlockSequence initialisation = new BlockSequence(Collections.singletonList(specification.initialisation));
                BlockSequence premises = new BlockSequence(specification.premises);
                BlockSequence finallyDo = new BlockSequence(specification.finallyDo);
                String description = specification.description + WHEN + getDescriptions(specification.premises) + THEN_SUBTEST + subTest.testName + finallyDescription(specification.finallyDo);
                result.add(testCase(description, initialisation.catenate(premises).catenate(subTest.testExecution).catenate(finallyDo)));
            }
        }
        return result;
    }

    private String finallyDescription(ImmutableList<FinallyBlock> finallyDo) {
        if (finallyDo.isEmpty()) {
            return "";
        } else {
            return ". Finally " + getDescriptions(finallyDo);
        }
    }

    private <T extends DescribableBlock> String getDescriptions(ImmutableList<T> premises) {
        final List<String> premisesDescription = Lists.transform(premises, getDescription());
        return AND_JOINER.join(premisesDescription);
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
