package thistle.core;

public interface Block {
    void execute() throws Exception;
    Block NOP = new Block() {
        @Override
        public void execute() throws Exception {

        }
    };
}
