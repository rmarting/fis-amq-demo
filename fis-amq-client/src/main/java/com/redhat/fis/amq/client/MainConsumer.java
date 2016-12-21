package com.redhat.fis.amq.client;

import com.redhat.fis.amq.client.consumer.Consumer;

public class MainConsumer {

    public static void main(String[] args) throws Exception {
        // TODO Sanitize args

        // System.setProperty("javax.net.ssl.trustStore", "/home/rmarting/Workspaces/FIS/fis-amq-demo/fis-amq-client/src/main/amq-consumer-server.ks");
        // System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        // System.setProperty("javax.net.ssl.keyStore", "/home/rmarting/Workspaces/FIS/fis-amq-demo/fis-amq-client/src/main/amq-consumer-server.ks");
        // System.setProperty("javax.net.ssl.keyStorePassword", "newpass");

        for (int i = 0; i < 10; i++) {
            // Consumers
            thread(new Consumer(args[0], args[1], args[2], args[3]), false);
            // thread(new Consumer(args[0], args[1], args[2], args[3]), false);
            // thread(new Consumer(args[0], args[1], args[2], args[3]), false);
            // thread(new Consumer(args[0], args[1], args[2], args[3]), false);
            // thread(new Consumer(args[0], args[1], args[2], args[3]), false);

            Thread.sleep(100);
        }
    }

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

}
