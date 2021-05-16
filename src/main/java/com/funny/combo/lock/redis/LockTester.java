package com.funny.combo.lock.redis;

public class LockTester {

    public static void main(String[] args) {
        ComboLock comboLock = new ComboLock();

        boolean lockResult = comboLock.acquire("123");


        System.out.println(comboLock.release("123"));

    }
}
