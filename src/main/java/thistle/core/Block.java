package thistle.core;

public interface Block {
    Block NOP = new Block() {
        @Override
        public void execute() throws Exception {

        }
    };

    void execute() throws Exception;
}
