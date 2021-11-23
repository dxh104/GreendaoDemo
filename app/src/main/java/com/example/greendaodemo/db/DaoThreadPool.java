package com.example.greendaodemo.db;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DaoThreadPool {//数据库专用线程池
    private static ExecutorService newSingleThreadPool;
    private static DaoThreadPool instance;

    public static DaoThreadPool getInstance() {
        if (instance == null) {
            synchronized (ExecutorService.class) {
                if (instance == null) {
                    instance = new DaoThreadPool();
                    newSingleThreadPool = Executors.newSingleThreadExecutor();
                }
            }
        }
        return instance;
    }

    public void executeSingleRunnable(Runnable runnable) {
        if (newSingleThreadPool != null && !newSingleThreadPool.isShutdown())
            newSingleThreadPool.execute(runnable);
    }
}
