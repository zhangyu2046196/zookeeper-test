package com.youyuan.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * 类名称：DistributeLock <br>
 * 类描述： 测试分布式锁 <br>
 * <p>
 * 节点目录：/locks/seq-
 *
 * @author zhangyu
 * @version 1.0.0
 * @date 创建时间：2021/8/14 19:31<br>
 */
public class DistributeLock {
    /**
     * zookeeper集群地址信息
     */
    private final String url = "192.168.1.18:2181,192.168.1.19:2181,192.168.1.20:2181";
    /**
     * 连接超时时间
     */
    private final Integer timeOut = 2000;
    /**
     * 客户端
     */
    private ZooKeeper zkClient;
    /**
     * 根节点
     */
    private final String ROOT_NODE = "/locks";
    /**
     * 客户端连接
     */
    private CountDownLatch connectLatch = new CountDownLatch(1);
    /**
     * 当前节点全路径
     */
    private String currentNode;
    /**
     * 获取锁节点名称
     */
    private String waitPath;
    /**
     * 当前等待连接
     */
    private CountDownLatch waitLatch = new CountDownLatch(1);
    /**
     * 监听的上一个节点全路径
     */
    private String beforeNode;

    public DistributeLock() throws IOException, InterruptedException, KeeperException {
        //1. 创建zookeeper连接
        zkClient = new ZooKeeper(url, timeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                //连接请求成功
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connectLatch.countDown();
                }
                //监听节点删除
                if (watchedEvent.getType() == Event.EventType.NodeDeleted && Objects.equals
                        (watchedEvent.getPath(), beforeNode)) {
                    waitLatch.countDown();
                }
            }
        });
        connectLatch.await();
        //2. 判断跟节点/locks是否存在,不存在创建
        Stat exist = zkClient.exists(ROOT_NODE, Boolean.FALSE);
        if (null == exist) {
            //创建根节点
            String rootName = zkClient.create(ROOT_NODE, UUID.randomUUID().toString().getBytes(), ZooDefs.Ids
                    .OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("跟节点创建成功:" + rootName);
        }
    }

    /**
     * 方法名: lock <br>
     * 方法描述: 获取锁 <br>
     *
     * @date 创建时间: 2021/8/14 19:35 <br>
     * @author zhangyu
     */
    public void lock() throws KeeperException, InterruptedException {
        //1. 创建临时顺序节点
        currentNode = zkClient.create(ROOT_NODE + "/seq-", UUID.randomUUID().toString().getBytes(), ZooDefs.Ids
                .OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        //2. 判断创建的节点是否是最小节点,如果是获取锁,如果不是对前一个节点监听
        //2.1 获取根节点下子节点列表
        List<String> children = zkClient.getChildren(ROOT_NODE, Boolean.FALSE);
        if (children.size() == 1) {
            System.out.println("当前节点获取锁成功");
            return;
        }
        //2.2 对子节点集合排序
        Collections.sort(children);
        //2.3 判断第一个节点是否是当前节点,如果是获取锁成功,否则需要对上一个节点监听
        waitPath = currentNode.substring((ROOT_NODE + "/").length());
        if (Objects.equals(children.get(0), waitPath)) {
            System.out.println("当前节点是子节点最小获取锁成功");
            return;
        }
        //2.3 对前一个节点监听
        //当前节点下标
        int index = children.indexOf(waitPath);
        beforeNode=ROOT_NODE + "/" + children.get(index - 1);
        zkClient.getData(beforeNode, Boolean.TRUE, null);
        waitLatch.await();
        return;
    }

    /**
     * 方法名: unLock <br>
     * 方法描述: 释放锁 <br>
     * <p>
     * 删除当前获取锁的节点
     *
     * @date 创建时间: 2021/8/14 19:35 <br>
     * @author zhangyu
     */
    public void unLock() throws KeeperException, InterruptedException {
        //删除当前节点释放锁
        zkClient.delete(currentNode, -1);
    }

}
