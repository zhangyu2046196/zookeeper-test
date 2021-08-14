package com.youyuan.case3;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 类名称：CuratorLock <br>
 * 类描述：     测试通过Curator框架实现分布式锁 <br>
 *
 * @author zhangyu
 * @version 1.0.0
 * @date 创建时间：2021/8/14 22:25<br>
 */
public class CuratorLock {
    /**
     * zookeeper集群地址信息
     */
    private final static String url = "192.168.1.18:2181,192.168.1.19:2181,192.168.1.20:2181";
    /**
     * session超时时间数据
     */
    private final static Integer sessonTimeOut = 2000;
    /**
     * 连接超时时间
     */
    private static final Integer connecTimeOut = 2000;

    public static void main(String[] args) {
        //1. 创建分布式锁1  path是锁的根目录
        final InterProcessMutex lock1 = new InterProcessMutex(getCuratorFromwork(), "/locks");
        //2. 创建分布式锁2  path为锁的根目录
        final InterProcessMutex lock2 = new InterProcessMutex(getCuratorFromwork(), "/locks");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.acquire();
                    System.out.println("线程1获取锁成功");
                    lock1.acquire();
                    System.out.println("线程1再次获取锁成功");
                    Thread.sleep(5 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lock1.release();
                        System.out.println("线程1释放锁成功");
                        lock1.release();
                        System.out.println("线程1再次释放锁成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.acquire();
                    System.out.println("线程2获取锁成功");
                    lock2.acquire();
                    System.out.println("线程2再次获取锁成功");
                    Thread.sleep(5 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lock2.release();
                        System.out.println("线程2释放锁成功");
                        lock2.release();
                        System.out.println("线程2再次释放锁成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 方法名: getCuratorFromwork <br>
     * 方法描述: 创建基于Curator框架的分布式锁 <br>
     *
     * @return {@link CuratorFramework 返回Curator框架}
     * @date 创建时间: 2021/8/14 22:29 <br>
     * @author zhangyu
     */
    private static CuratorFramework getCuratorFromwork() {
        //第一个参数是连接失败后多少毫秒重试,第二个参数是重试多少次
        RetryPolicy policy = new ExponentialBackoffRetry(3000, 3);
        CuratorFramework framework = CuratorFrameworkFactory.builder()
                .connectString(url)
                .sessionTimeoutMs(sessonTimeOut)
                .connectionTimeoutMs(connecTimeOut)
                .retryPolicy(policy)
                .build();
        framework.start();
        System.out.println("zookeeper服务启动成功");
        return framework;
    }

}
