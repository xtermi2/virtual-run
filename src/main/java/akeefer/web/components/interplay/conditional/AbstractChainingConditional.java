package akeefer.web.components.interplay.conditional;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract Impl to logical connect Conditionals.
 */
@SuppressWarnings("serial")
public abstract class AbstractChainingConditional implements IConditional<Void> {

    private final List<? extends IConditional<?>> conditionals;

    public AbstractChainingConditional(IConditional<?>... conditionals) {
        this.conditionals = Arrays.asList(conditionals);
    }

    public AbstractChainingConditional(List<? extends IConditional<?>> conditionals) {
        this.conditionals = conditionals;
    }

    @Override
    public final boolean isFulfilled() {
        return isFulfilled(conditionals);
    }

    /**
     * @param conditionals Conditionals to check
     * @return result of the check
     */
    protected abstract boolean isFulfilled(List<? extends IConditional<?>> conditionals);

    /*
     * Don't call!
     */
    @Override
    public Void getObject() {
        throw new UnsupportedOperationException("getObject is not supported");
    }

    /*
     * Don't call!
     */
    @Override
    public void setObject(Void object) {
        throw new UnsupportedOperationException("setObject is not supported");
    }

    @Override
    public void detach() {
        if (conditionals != null) {
            for (IConditional<?> conditional : conditionals) {
                conditional.detach();
            }
        }
    }
}
