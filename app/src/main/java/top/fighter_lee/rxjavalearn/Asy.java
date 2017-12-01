package top.fighter_lee.rxjavalearn;

import android.os.SystemClock;

/**
 * @author fighter_lee
 * @date 2017/11/30
 */
public class Asy {

    public interface Callback{
        void ok();
    }

    public static void start(final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(5000);
                callback.ok();
            }
        }).start();
    }

}
