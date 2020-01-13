package com.asiainfo.redis.config;

import com.asiainfo.redis.utils.JedisClusterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: zhang pengcheng
 * @date: 2019/3/1 8:36 PM
 * @description:
 */

@Configuration
public class JedisClusterConfig {

    @Autowired
    private RedisConfig redisConfig;

    private static Logger logger = LoggerFactory.getLogger(JedisClusterUtil.class);

    //使用单例模式，把JedisCluster作为static的类成员，(使用懒汉单例模式)
    private static JedisCluster jedisCluster = null;

    @Bean
    public synchronized JedisCluster getJedisCluster() {
        try {
            logger.info(" >>>>>>> REDIS CLUSTER连接池,开始启动 >>>>>>> ");
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPoolConfig.setTestOnReturn(true);
            jedisPoolConfig.setTestOnCreate(true);
            jedisPoolConfig.setTestWhileIdle(true);
            jedisPoolConfig.setMaxTotal(300);
            jedisPoolConfig.setMinIdle(5);
            // 一定要设置不然会一直等待获取连接导致线程阻塞
            jedisPoolConfig.setMaxWaitMillis(6000);
            //获得节点配置信息
            Set<HostAndPort> nodes = new HashSet<>();
            if (redisConfig.getClusterNodes() != null) {
                for (String ipAndPort : redisConfig.getClusterNodes()) {
                    String[] ipOrPort = ipAndPort.split(":");
                    HostAndPort hostAndPort = new HostAndPort(ipOrPort[0], Integer.parseInt(ipOrPort[1]));
                    nodes.add(hostAndPort);
                }
            }
            //初始化 只有当jedisCluster为空时才实例化
            if (jedisCluster == null&&nodes.size() > 0)  {
                //redis有密码,配置JedisCluster
                if (redisConfig.getPassword() != null) {
                    jedisCluster = new JedisCluster(nodes, redisConfig.getConnectionTimeout(), redisConfig.getSoTimeout(), redisConfig.getMaxAttempts(), redisConfig.getPassword(), jedisPoolConfig);
                }
                //redis无密码,配置JedisCluster
                else {
                    jedisCluster = new JedisCluster(nodes, redisConfig.getConnectionTimeout(), redisConfig.getSoTimeout(), redisConfig.getMaxAttempts(), jedisPoolConfig);
                }
                logger.info(" >>>>>>> REDIS CLUSTER 连接池,启动成功 >>>>>> ");
            } else {
                logger.warn("{} redis 连接异常", nodes);
            }
        } catch (Exception e) {
            logger.error(">>>>>> REDIS CLUSTER 连接池,初始化失败 >>>>>> ", e);
            e.printStackTrace();
        }
        return jedisCluster;
    }
}