package thistle.suite;

import org.junit.Test;
import thistle.core.Block;
import thistle.core.FinallyBlock;
import thistle.core.ThenBlock;
import thistle.core.WhenBlock;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class SpecificationTest {

    Block initialisation = mock(Block.class);

    WhenBlock premise1 = mock(WhenBlock.class),
            premise2 = mock(WhenBlock.class);

    ThenBlock case1 = mock(ThenBlock.class);

    FinallyBlock finally1 = mock(FinallyBlock.class);

    Specification testee = Specification.builder()
            .description("bar")
            .initialisation(initialisation)
            .premises(Arrays.asList(premise1, premise2))
            .cases(Collections.singletonList(case1))
            .finallyDo(Collections.singletonList(finally1))
            .build();

    @Test
    public void shouldReturnExpectedDescription() {
        assertThat(testee.description, equalTo("bar"));
        assertThat(testee.describe(), equalTo("bar"));
    }

    @Test
    public void shouldSetEvertythingEmptyIfNotProvided() {
        testee = Specification.builder().build();

        assertThat(testee.description, equalTo(""));
        assertThat(testee.initialisation, is(Block.NOP));
        assertThat(testee.premises.isEmpty(),is(true));
        assertThat(testee.cases.isEmpty(), is(true));
        assertThat(testee.finallyDo.isEmpty(), is(true));
    }

    @Test
    public void shouldInitialisePremises() {
        assertThat(testee.premises.get(0), equalTo(premise1));
        assertThat(testee.premises.get(1), equalTo(premise2));
    }

    @Test
    public void shouldInitialiseCases() {
        assertThat(testee.cases.size(), equalTo(1));
        assertThat(testee.cases.get(0), equalTo(case1));
    }

    @Test
    public void shouldInitialiseInitBlock() {
        assertThat(testee.initialisation, equalTo(initialisation));
    }

    @Test
    public void shouldInitialiseFinallyDo() {
        assertThat(testee.finallyDo.size(), equalTo(1));
        assertThat(testee.finallyDo.get(0), equalTo(finally1));
    }

    @Test
    public void shouldAddSpecifications() {
        assertThat(testee.getSubSpecifications().isEmpty(), is(true));
        Specification subSpec = mock(Specification.class);
        testee.addSubSpecification(subSpec);

        assertThat(testee.getSubSpecifications().size(), equalTo(1));
        assertThat(testee.getSubSpecifications().get(0), equalTo(subSpec));
    }
}
