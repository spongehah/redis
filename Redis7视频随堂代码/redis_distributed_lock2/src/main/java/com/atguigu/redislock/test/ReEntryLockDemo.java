package com.atguigu.redislock.test;

import sun.management.counter.Variability;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @auther zzyy
 * @create 2023-01-06 10:36
 */
public class ReEntryLockDemo
{

    final Object obj = new Object();
    public void entry01()
    {
        new Thread(() -> {
            synchronized (obj)
            {
                System.out.println(Thread.currentThread().getName()+"\t"+"外层调用");
                synchronized (obj)
                {
                    System.out.println(Thread.currentThread().getName()+"\t"+"中层调用");
                    synchronized (obj)
                    {
                        System.out.println(Thread.currentThread().getName()+"\t"+"内层调用");
                    }
                }
            }

        },"t1").start();
    }

    public void entry02()
    {
        m1();
    }

    private synchronized void m1()
    {
        System.out.println(Thread.currentThread().getName()+"\t"+"外层调用synchronized");
        m2();
    }

    private synchronized void m2()
    {
        System.out.println(Thread.currentThread().getName()+"\t"+"中层调用synchronized");
        m3();
    }

    private synchronized void m3()
    {
        System.out.println(Thread.currentThread().getName()+"\t"+"内层调用synchronized");
    }

    Lock lock = new ReentrantLock();
    public void entry03()
    {
        new Thread(() -> {
            lock.lock();
            try
            {
                System.out.println(Thread.currentThread().getName()+"\t"+"外层调用lock");
                lock.lock();
                try
                {
                    System.out.println(Thread.currentThread().getName()+"\t"+"内层调用lock");
                }finally {
                    lock.unlock();
                }
            }finally {
                lock.unlock();
            }
        },"t1").start();

        //暂停毫秒
        try { TimeUnit.MILLISECONDS.sleep(2); } catch (InterruptedException e) { e.printStackTrace(); }

        new Thread(() -> {
            lock.lock();
            try
            {
                System.out.println(Thread.currentThread().getName()+"\t"+"外层调用lock");
            }finally {
                lock.unlock();
            }

        },"t2").start();
    }

    public static void main(String[] args)
    {
        ReEntryLockDemo demo = new ReEntryLockDemo();

        //demo.entry01();
        //demo.entry02();
        demo.entry03();

        //在一个Synchronized修饰的方法或代码块的内部调用本类的其他Synchronized修饰的方法或代码块时，是永远可以得到锁的
    }
}
