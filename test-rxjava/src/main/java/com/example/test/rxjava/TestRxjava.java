package com.example.test.rxjava;


import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class TestRxjava {
    void test() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@org.jetbrains.annotations.NotNull ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
            }
        });

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {
                System.out.println("onSubscribe");
            }

            @Override
            public void onNext(@NotNull Integer integer) {

                System.out.println("onNext");
            }

            @Override
            public void onError(@NotNull Throwable e) {
                System.out.println("onError");

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });

        observable.doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("do on next, accept " + integer);
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("do on complete");
            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("do finally");
            }
        }).subscribe();
    }
}
