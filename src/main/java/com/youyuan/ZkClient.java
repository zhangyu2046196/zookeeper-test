package com.youyuan;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * 类名称：ZkClient <br>
 * 类描述： zookeeper客户端 <br>
 *
 * @author zhangyu
 * @version 1.0.0
 * @date 创建时间：2021/8/14 12:16<br>
 */
public class ZkClient {

    /**
     * 集群地址信息
     */
    private String url = "192.168.1.18:2181,192.168.1.19:2181,192.168.1.20:2181";
    /**
     * 超时时间(毫秒)
     */
    private Integer timeOut = 2000;
    /**
     * 客户端
     */
    private ZooKeeper zkClient;

    /**
     * 方法名: init <br>
     * 方法描述: 初始化客户端 <br>
     *
     * @date 创建时间: 2021/8/14 12:23 <br>
     * @author zhangyu
     */
    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(url, timeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("--------------------");
                List<String> children = null;
                try {
                    children = zkClient.getChildren("/youyuan", true);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (String child : children) {
                    System.out.println(child);
                }
                System.out.println("--------------------");
            }
        });
    }

    /**
     * 方法名: createNode <br>
     * 方法描述: 创建节点信息 <br>
     *
     * @date 创建时间: 2021/8/14 12:24 <br>
     * @author zhangyu
     */
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        String createResult = zkClient.create("/youyuan", "北京".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode
                .PERSISTENT);
        System.out.println("创建节点返回结果:" + createResult);
    }

    /**
    * 方法名: getChildren <br>
    * 方法描述: 获取子节点且循环监听 <br>
    *
    * @date 创建时间: 2021/8/14 12:53 <br>
    * @author zhangyu
    */
    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        //path监听的路径
        //watch设置为true时监听的程序走的初始化时的watch的匿名内部类中的process    方法
        List<String> children = zkClient.getChildren("/youyuan", true);
        for (String child : children) {
            System.out.println(child);
        }

        //阻塞线程方式
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
    * 方法名: exist <br>
    * 方法描述: 判断节点是否存在 <br>
    *
    * @date 创建时间: 2021/8/14 13:02 <br>
    * @author zhangyu
    */
    @Test
    public void exist() throws KeeperException, InterruptedException {
        Stat exist = zkClient.exists("/youyuan/youyuan7", false);
        System.out.println(exist==null?"no exist":" exist");
    }
}
