package com.jjyh.it.utiles.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类    名:  ThreadPoolProxy
 * 创 建 者:  苏新伟
 * 创建时间:  2017/03/06 19:45
 * 描    述： 线程池的代理类
 * 描    述： 替原对象(ThreadPool)完成一些操作(提交任务,执行任务,移除任务)
 */
public class ThreadPoolProxy {
    ThreadPoolExecutor mExecutor;

    private int mCorePoolSize;//核心池大小


    public ThreadPoolProxy(int corePoolSize) {
        mCorePoolSize = corePoolSize;
    }

    /**
     * 创建ThreadPoolExecutor对象
     */
    private void initThreadPoolExecutor() {
        if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
            synchronized (ThreadPoolProxy.class) {
                if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
                    int maximumPoolSize = mCorePoolSize;//最大线程数
                    long keepAliveTime = 0;//保持时间
                    TimeUnit unit = TimeUnit.MILLISECONDS;//单位
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();//任务队列
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();//线程工厂
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
                    mExecutor = new ThreadPoolExecutor(mCorePoolSize, maximumPoolSize,
                            keepAliveTime, unit,
                            workQueue, threadFactory, handler);
                }
            }
        }
    }
    /*
     提交任务和执行任务的区别?
        1.是否有返回值?
            submit有返回值,执行没有返回值
        2.Future是干嘛的?
            1.描述异步执行完成之后的结果
            2.有方法检查任务是否执行完成,还可以等待执行完成,接收执行完成后的结果
            3.get方法可以接收结果,在任务执行完成之后,get方法可以阻塞等待结果准备好-->重点关心
            4.cancle方法可以去取消
            5.get方法还有一种重要的作用,可以抛出任务执行过程中遇到的异常
     */

    /**
     * 提交任务
     *
     * @param task
     */
    public Future submit(Runnable task) {
        //初始化mExecutor对象
        initThreadPoolExecutor();
        Future<?> submitResult = mExecutor.submit(task);
        return submitResult;
    }

    /**
     * 执行任务
     *
     * @param task
     */
    public void execute(Runnable task) {
        initThreadPoolExecutor();
        mExecutor.execute(task);
    }


    /**
     * 移除任务
     *
     * @param task
     */
    public void remove(Runnable task) {
        initThreadPoolExecutor();
        mExecutor.remove(task);
    }
}
