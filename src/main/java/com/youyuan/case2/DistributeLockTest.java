package com.youyuan.case2;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * 类名称：DistributeLockTest <br>
 * 类描述： 测试分布式锁 <br>
 *
 * @author zhangyu
 * @version 1.0.0
 * @date 创建时间：2021/8/14 20:01<br>
 */
public class DistributeLockTest {

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        final DistributeLock lock1 = new DistributeLock();
        final DistributeLock lock2 = new DistributeLock();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.lock();
                    System.out.println("线程1获取锁成功");
                    Thread.sleep(5 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lock1.unLock();
                        System.out.println("线程1释放锁");
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.lock();
                    System.out.println("线程2获取锁成功");
                    Thread.sleep(5 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lock2.unLock();
                        System.out.println("线程2释放锁");
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
