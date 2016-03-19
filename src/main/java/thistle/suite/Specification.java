package thistle.suite;

import com.google.common.collect.ImmutableList;
import thistle.core.*;

import java.util.ArrayList;
import java.util.List;

public class Specification implements Describable {

    public final String description;
    public final Block initialisation;
    public final ImmutableList<WhenBlock> premises;
    public final ImmutableList<ThenBlock> cases;
    public final ImmutableList<FinallyBlock> finallyDo;
    private final List<Specification> subSpecifications = new ArrayList<Specification>();

    private Specification(Builder builder) {
        this(builder.description,builder.initialisation,builder.premises,builder.cases,builder.finallyDo);
    }
    private Specification(String description, Block initialisation, List<WhenBlock> premises, List<ThenBlock> cases, List<FinallyBlock> finallyDo) {
        this.description = description;
        this.initialisation = initialisation;
        this.premises = ImmutableList.<WhenBlock>builder().addAll(premises).build();
        this.cases = ImmutableList.<ThenBlock>builder().addAll(cases).build();
        this.finallyDo = ImmutableList.<FinallyBlock>builder().addAll(finallyDo).build();
    }

    public void addSubSpecification(Specification specification) {
        subSpecifications.add(specification);
    }

    public List<Specification> getSubSpecifications() {
        return subSpecifications;
    }

    @Override
    public String describe() {
        return description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String description;
        private Block initialisation;
        private ImmutableList<WhenBlock> premises;
        private ImmutableList<ThenBlock> cases;
        private ImmutableList<FinallyBlock> finallyDo;

        private Builder() {
            this.description = "";
            this.initialisation = Block.NOP;
            this.premises = ImmutableList.of();
            this.cases = ImmutableList.of();
            this.finallyDo = ImmutableList.of();
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder initialisation(Block initialisation) {
            this.initialisation = initialisation;
            return this;
        }

        public Builder premises(List<WhenBlock> premises) {
            this.premises = ImmutableList.<WhenBlock>builder().addAll(premises).build();
            return this;
        }

        public Builder cases(List<ThenBlock> cases) {
            this.cases = ImmutableList.<ThenBlock>builder().addAll(cases).build();
            return this;
        }

        public Builder finallyDo(List<FinallyBlock> finallyDo) {
            this.finallyDo = ImmutableList.<FinallyBlock>builder().addAll(finallyDo).build();
            return this;
        }

        public Specification build() {
            return new Specification(this);
        }
    }
}
