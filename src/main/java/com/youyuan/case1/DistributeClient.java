package com.youyuan.case1;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名称：DistributeClient <br>
 * 类描述： 服务器动态上下线项目之客户端 <br>
 *
 * @author zhangyu
 * @version 1.0.0
 * @date 创建时间：2021/8/14 18:03<br>
 */
public class DistributeClient {
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
        DistributeClient distributeClient = new DistributeClient();
        //1. 创建客户端
        distributeClient.connect();
        //2. 注册监听
        distributeClient.getServerList();
        //3. 业务逻辑
        distributeClient.business();
    }

    /**
     * 方法名: business <br>
     * 方法描述: 业务逻辑处理 <br>
     * 此处模拟休眠
     *
     * @date 创建时间: 2021/8/14 18:11 <br>
     * @author zhangyu
     */
    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 方法名: connect <br>
     * 方法描述: 建立客户端连接 <br>
     *
     * @date 创建时间: 2021/8/14 18:06 <br>
     * @author zhangyu
     */
    private void connect() throws IOException {
        zkClient = new ZooKeeper(url, timeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                try {
                    getServerList();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 方法名: getServerList <br>
     * 方法描述: 获取监听节点服务列表 <br>
     *
     * @date 创建时间: 2021/8/14 18:07 <br>
     * @author zhangyu
     */
    public void getServerList() throws KeeperException, InterruptedException {
        //服务列表
        List<String> serverList = new ArrayList();
        //获取监听节点下的节点列表
        List<String> children = zkClient.getChildren("/servers", Boolean.TRUE);
        for (String child : children) {
            serverList.add(new String(zkClient.getData("/servers/" + child, Boolean.FALSE, null)));
        }
        System.out.println("服务器列表:" + serverList);
    }
}
