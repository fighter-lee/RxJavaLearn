package top.fighter_lee.rxjavalearn;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author fighter_lee
 * @date 2017/11/17
 */
public class FutureTaskPool {

    private FutureTaskPool() {
    }

    private volatile static FutureTaskPool futureThreadPool;

    private static ExecutorService threadExecutor;

    public static FutureTaskPool getInstance() {
        if (futureThreadPool == null) {
            synchronized (FutureTaskPool.class) {
                futureThreadPool = new FutureTaskPool();
                threadExecutor = Executors.newSingleThreadExecutor();
            }
        }
        return futureThreadPool;
    }

    public <T> FutureTask<T> executeTask(Callable<T> callable){
        FutureTask<T> futureTask= new FutureTask<T>(callable);
        threadExecutor.submit(futureTask);
        return futureTask;
    }

    public  <T> FutureTask<T>  executeFutureTask(SimpleFutureTask<T> mFutureTask){
        threadExecutor.submit(mFutureTask);
        return mFutureTask;
    }

}
