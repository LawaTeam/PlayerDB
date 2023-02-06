package com.mclawa.playerdb;

public interface DataCallback<T> {
    void accept(T t);
    default void fail(Throwable throwable) {
        throw new RuntimeException(throwable);
    }
}
