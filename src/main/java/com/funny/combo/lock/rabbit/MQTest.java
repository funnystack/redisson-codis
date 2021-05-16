package com.funny.combo.lock.rabbit;

public class MQTest {
    public static void main(String[] args) throws Exception {
        MQReceiver MQReceiver = new MQReceiver();
        MQReceiver.start();

        MQSender.sendMessage("fangli");
    }
}
