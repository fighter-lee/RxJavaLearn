package top.fighter_lee.rxjavalearn;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class MainActivity extends AppCompatActivity implements ObservableOnSubscribe<Integer> {
    private static final String TAG = "MainActivity";
    private ObservableEmitter<Integer> e;
    private int defer_i;
    private Disposable dispose;
    private int retryCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建符
        //                test();
        //        deferTest();
        //                empty_error_throw();
        //        from();
        //        future();
        //        block();
        //过滤符
        //        contain_test1();
        //        distinct_test();
        //        filter_test();
        //        debounce_test();
        retryWhen();
//        zipWith();
    }

    public void block() {
        Observable.just(1, 2, 3, 4)
                .blockingForEach(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: " + integer);
                    }
                });
        Log.d(TAG, "block: 完成了 1");
        Observable<Integer> just = Observable.just(1, 2, 3);
        Integer integer = just.take(1).blockingFirst();
        Log.d(TAG, "block: " + integer);
        Log.d(TAG, "block: 完成了 2");
        just.elementAt(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "block: 接收：" + integer);
            }
        });
        Log.d(TAG, "block: 完成了 3");

    }

    public void zipWith() {
        Observable.just(100,200,300,400)
                .zipWith(Observable.range(1, 3), new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer e, Integer integer) throws Exception {
                        Log.d(TAG, "apply: "+integer);
                        return integer+e;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: "+integer);
            }
        });
    }

    public void retryWhen() {
        retryCount = 0;
        Observable.just(888)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (retryCount == 1) {
                            throw new Exception("aaaaaaaaaa");
                        }
                        if (retryCount == 2) {
                            throw new Exception("bbbbbbbbbb");
                        }
//                        if (retryCount == 3) {
//                            throw new Exception("cccccccccc");
//                        }
                    }
                })
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
//                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
//                            @Override
//                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
//                                ++retryCount;
//                                Log.d(TAG, "apply: " + retryCount);
//                                Log.e(TAG, "apply: ", throwable);
//                                if (retryCount < 3) {
//                                    return Observable.timer(1000, TimeUnit.MILLISECONDS);
//                                }
//                                return Observable.error(throwable);
//                            }
//                        });
                        //实现方式二
                        retryCount++;
                        return throwableObservable.zipWith(Observable.range(1, 3), new BiFunction<Throwable, Integer, Integer>() {
                            @Override
                            public Integer apply(Throwable throwable, Integer integer) throws Exception {
                                Log.d(TAG, "apply: exception:"+throwable.getMessage());
                                Log.d(TAG, "apply: "+integer);
                                //每次时间增加
                                return integer * 1000;
                            }
                        }).flatMap(new Function<Integer, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Integer integer) throws Exception {
                                Log.d(TAG, "apply: "+integer);
                                return Observable.timer(integer,TimeUnit.MILLISECONDS);
                            }
                        });
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG, "error", throwable);
            }
        });


    }

    public void test() {
        Observable.create(this)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        dispose = d;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                        //                        dispose.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void empty_error_throw() {
        //创建不排放项目，但正常终止，可观察到的
        Observable.empty()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, "onNext: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });

        Log.d(TAG, "===================================");

        //抛出异常
        Observable.error(new Exception("出错了"))
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });

        Log.d(TAG, "===================================");

        //创建一个可观察不排放项目，不会终止
        Observable.never()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    public void futureLearn() {
        FutureTask<Integer> futureTask = FutureTaskPool.getInstance().executeTask(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                SystemClock.sleep(500);
                Log.d(TAG, "1 Runnable in FutureTask ..." + " Thread id：" + Thread.currentThread().getId());
                return 23;
            }
        });
        //        futureTask.cancel(true);
        if (futureTask.isCancelled()) {
            Log.d(TAG, "future: iscancel");
        } else {
            try {
                Log.d(TAG, "2 Callable in FutureTask ... Result:" + futureTask.get() + " Thread id：" + Thread.currentThread().getId());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }
        }
        Log.d(TAG, "future: 完成" + futureTask.isDone());
    }

    public void future() {
        FutureTask<Integer> futureTask = FutureTaskPool.getInstance().executeTask(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                SystemClock.sleep(2000);
                Log.d(TAG, "1 Runnable in FutureTask ..." + " Thread id：" + Thread.currentThread().getId());
                return 23;
            }
        });

        Observable.fromFuture(futureTask)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    public void from() {
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            integers.add(i);
        }
        Observable.fromArray(integers)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        for (Integer integer : integers) {
                            Log.d(TAG, "accept: " + integer);
                        }
                    }
                });

        Log.d(TAG, "===================================");

        Future<Integer> future = new Future<Integer>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Integer get() throws InterruptedException, ExecutionException {
                SystemClock.sleep(4000);
                return 1;
            }

            @Override
            public Integer get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                Log.d(TAG, "get: " + timeout + "," + unit);
                return 2;
            }
        };

        Observable.fromFuture(future).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer);
            }
        });
    }

    public void deferTest() {
        defer_i = 1;
        Observable<Integer> defer = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return Observable.just(defer_i);
            }
        });
        Observable<Integer> just = Observable.just(defer_i);
        defer_i = 10;

        defer.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: def:" + integer);
            }
        });

        just.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: just:" + integer);
            }
        });
    }

    public void contain_test1() {
        Observable.just(1, 2, 3, 4, 5)
                .contains(4)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.d(TAG, "accept: " + aBoolean);
                    }
                });
    }

    public void distinct_test() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(1);
            }
        }).distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "下游 accept: " + integer);
                    }
                });
    }

    public void filter_test() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(1);
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                if (integer > 2) {
                    return true;
                } else {
                    return false;
                }
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "下游 accept: " + integer);
            }
        });
    }

    public void debounce_test() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                SystemClock.sleep(500);
                e.onNext(2);
                e.onNext(3);
                SystemClock.sleep(500);
                e.onNext(1);
            }
        })
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: " + integer);
                    }
                });
    }

    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
        this.e = e;
    }

    public void send(View view) {
        e.onNext(2);
        Log.d(TAG, "send: " + e.isDisposed());
    }
}
