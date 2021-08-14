package com.youyuan.case1;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * 类名称：DistributeServer <br>
 * 类描述： 服务器动态上下线项目之服务端 <br>
 *
 * @author zhangyu
 * @version 1.0.0
 * @date 创建时间：2021/8/14 17:53<br>
 */
public class DistributeServer {

    /**
     * zookeeper集群地址信息
     */
    private String url = "192.168.1.18:2181,192.168.1.19:2181,192.168.1.20:2181";
    /**
     * 超时时间信息
     */
    private Integer timeOut = 2000;
    /**
     * zookeeper客户端
     */
    private ZooKeeper zkClient;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        DistributeServer distributeServer = new DistributeServer();
        //1. 创建zookeeper连接
        distributeServer.connect();
        //2. 服务器启动创建临时节点
        distributeServer.regist(args[0]);
        //3. 执行业务逻辑
        distributeServer.business();
    }

    /**
     * 方法名: business <br>
     * 方法描述: 执行业务逻辑 <br>
     * <p>
     * 此处模拟休眠
     *
     * @date 创建时间: 2021/8/14 18:02 <br>
     * @author zhangyu
     */
    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);

    }

    /**
     * 方法名: regist <br>
     * 方法描述: 服务器启动创建临时顺序节点 <br>
     *
     * @param hostName 服务器名称
     * @date 创建时间: 2021/8/14 17:59 <br>
     * @author zhangyu
     */
    private void regist(String hostName) throws KeeperException, InterruptedException {
        String createResult = zkClient.create("/servers/" + hostName, hostName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(createResult + " is on line ");
    }

    /**
     * 方法名: connect <br>
     * 方法描述: 创建客户端 <br>
     *
     * @date 创建时间: 2021/8/14 17:57 <br>
     * @author zhangyu
     */
    private void connect() throws IOException {
        zkClient = new ZooKeeper(url, timeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }


}
