package com.funny.combo.lock.api;

public interface CodisLock {

    boolean acquire(String key) ;


    boolean release(String key);

}
