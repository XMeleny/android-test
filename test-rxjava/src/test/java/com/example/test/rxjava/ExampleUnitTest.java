package com.example.test.rxjava;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    static class Transformer implements MaybeOnSubscribe<Integer> {
        @Override
        public void subscribe(@NotNull MaybeEmitter<Integer> emitter) throws Exception {
            MyCallback callback = new MyCallback() {
                @Override
                public void onSuccess() {
                    System.out.println("callback onSuccess");
                    emitter.onSuccess(0);
                    emitter.onComplete();
                }

                @Override
                public void onError() {
                    System.out.println("callback onError");
                    emitter.onError(new Throwable("0"));
                    emitter.onComplete();
                }
            };
//            callback.onError();
            callback.onSuccess();
        }
    }

    public Maybe<Integer> requestFalse(MaybeOnSubscribe<Integer> source) {
        return Maybe.create(source)
                .onErrorComplete(new Predicate<Throwable>() {
                    @Override
                    public boolean test(@NotNull Throwable throwable) throws Exception {
                        System.out.println("request, onErrorComplete");
                        return false;
                    }
                });
    }

    public Maybe<Integer> requestTrue(MaybeOnSubscribe<Integer> source) {
        return Maybe.create(source)
                .onErrorComplete(new Predicate<Throwable>() {
                    @Override
                    public boolean test(@NotNull Throwable throwable) throws Exception {
                        System.out.println("request, onErrorComplete");
                        return true;
                    }
                });
    }


    public Maybe<Integer> doApiRequestUsingTrue() {
        return requestTrue(new Transformer())
                .doOnSuccess(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("doApiRequestUsingTrue request doOnSuccess1");
                    }
                })
                .doOnSuccess(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("doApiRequestUsingTrue request doOnSuccess2");
                    }
                });
    }

    public Maybe<Integer> doApiRequestUsingFalse() {
        return requestFalse(new Transformer())
                .doOnSuccess(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("doApiRequestUsingFalse request doOnSuccess");
                    }
                });
    }

    @Test
    public void testTrue() {
        doApiRequestUsingTrue().doOnSuccess(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("test doApiRequestUsingTrue doOnSuccess");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println("test doApiRequestUsingTrue doOnError");
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("test doApiRequestUsingTrue doOnComplete");
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("subscribe onSuccess");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println("subscribe onError");
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("subscribe onComplete");
            }
        });
    }

    @Test
    public void testFalse() {
        doApiRequestUsingFalse().doOnSuccess(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("test doApiRequestUsingFalse doOnSuccess");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println("test doApiRequestUsingFalse doOnError");
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("test doApiRequestUsingFalse doOnComplete");
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("subscribe onSuccess");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println("subscribe onError");
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("subscribe onComplete");
            }
        });
    }
}