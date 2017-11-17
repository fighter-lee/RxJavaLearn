package top.fighter_lee.rxjavalearn;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author fighter_lee
 * @date 2017/11/17
 */
public abstract class SimpleFutureTask<T> extends FutureTask<T> {

    public SimpleFutureTask(Callable<T> callable) {
        super(callable);
    }

    @Override
    protected void done() {
        onFinish();
    }

    public abstract void onFinish();


}