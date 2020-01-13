package com.asiainfo.redis.utils;

import com.asiainfo.redis.po.TestMan;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author: zhang pengcheng
 * @date: 2019/3/2 5:34 PM
 * @description: JedisClusterUtil测试类
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class JedisClusterUtilTest {

    @Autowired
    JedisClusterUtil jedisClusterUtil;

    Jedis jedis;
    @Autowired
    private JedisCluster jedisCluster;


    static String prefix = "浙江全省宽带数据:浙江全省宽带数据";
    static String KEY_SPLIT = ":"; //用于隔开缓存前缀与缓存键值
    String nameKey = prefix + KEY_SPLIT + "浙江全省宽带数据";


    @Test
    public void set() {
        jedisClusterUtil.set("name", "jedis");
        Assert.assertEquals("jedis", jedisClusterUtil.get("name"));
    }


    @Test
    public void get() {
    }


    @Test
    public void setObject() {
        TestMan man = new TestMan();
        man.setId(10087L);
        man.setAge(18);
        man.setPassword("********");
        man.setSex(0);
        man.setUsername("弓长");
        jedisClusterUtil.setObject("10087L", man, 200);
        Assert.assertNotNull(jedisClusterUtil.getObject("10087L"));
    }

    @Test
    public void getObject() {
        System.out.println(jedisClusterUtil.getObject("10087L"));
    }

    @Test
    public void hasKey() {
    }

    @Test
    public void setWithExpireTime() {
    }


    @Test
    public void delete() {
        jedisClusterUtil.delete("10087L");
        Assert.assertEquals(null, jedisClusterUtil.get("10087L"));
    }

    /**
     * 批量操作key
     * keySlot算法中，如果key包含{}，就会使用第一个{}内部的字符串作为hash key，这样就可以保证拥有同样{}内部字符串的key就会拥有相同slot。
     * redis.clients.util.JedisClusterCRC16#getSlot(java.lang.String)
     * <p>
     * 注意：这样的话，本来可以hash到不同的slot中的数据都放到了同一个slot中，所以使用的时候要注意数据不要太多导致一个slot数据量过大，数据分布不均匀！
     * <p>
     * MSET 是一个原子性(atomic)操作，所有给定 key 都会在同一时间内被设置，某些给定 key 被更新而另一些给定 key 没有改变的情况，不可能发生。
     */
    @Test
    public void msetTest() throws InterruptedException {
        /**
         * jedisCluster.mset("sf","d","aadf","as");
         * 直接这样写，会报错：redis.clients.jedis.exceptions.JedisClusterException: No way to dispatch this command to Redis Cluster because keys have different slots.
         * 这是因为key不在同一个slot中
         */

        String result = jedisCluster.mset("{" + prefix + KEY_SPLIT + "}" + "name", "张三", "{" + prefix + KEY_SPLIT + "}" + "age", "23", "{" + prefix + KEY_SPLIT + "}" + "address", "adfsa", "{" + prefix + KEY_SPLIT + "}" + "score", "100");
        System.out.println(result);

        String name = jedisCluster.get("{" + prefix + KEY_SPLIT + "}" + "name");
        System.out.println(name);

        Long del = jedisCluster.del("{" + prefix + KEY_SPLIT + "}" + "age");
        System.out.println(del);

        List<String> values = jedisCluster.mget("{" + prefix + KEY_SPLIT + "}" + "name", "{" + prefix + KEY_SPLIT + "}" + "age", "{" + prefix + KEY_SPLIT + "}" + "address");
        System.out.println(values);
    }


    /**
     * MSETNX 命令：它只会在所有给定 key 都不存在的情况下进行设置操作。
     * http://doc.redisfans.com/string/mset.html
     */
    @Test
    public void msetnxTest() throws InterruptedException {
        Long msetnx = jedisCluster.msetnx(
                "{" + prefix + KEY_SPLIT + "}" + "name", "张三",
                "{" + prefix + KEY_SPLIT + "}" + "age", "23",
                "{" + prefix + KEY_SPLIT + "}" + "address", "adfsa",
                "{" + prefix + KEY_SPLIT + "}" + "score", "100");
        System.out.println(msetnx);

        System.out.println(jedisCluster.mget(
                "{" + prefix + KEY_SPLIT + "}" + "name",
                "{" + prefix + KEY_SPLIT + "}" + "age",
                "{" + prefix + KEY_SPLIT + "}" + "address",
                "{" + prefix + KEY_SPLIT + "}" + "score"));//[张三, 23, adfsa, 100]

        //name这个key已经存在，由于mset是原子的，该条指令将全部失败
        msetnx = jedisCluster.msetnx(
                "{" + prefix + KEY_SPLIT + "}" + "phone", "110",
                "{" + prefix + KEY_SPLIT + "}" + "name", "张三",
                "{" + prefix + KEY_SPLIT + "}" + "abc", "asdfasfdsa");
        System.out.println(msetnx);


        System.out.println(jedisCluster.mget(
                "{" + prefix + KEY_SPLIT + "}" + "name",
                "{" + prefix + KEY_SPLIT + "}" + "age",
                "{" + prefix + KEY_SPLIT + "}" + "address",
                "{" + prefix + KEY_SPLIT + "}" + "score",
                "{" + prefix + KEY_SPLIT + "}" + "phone",
                "{" + prefix + KEY_SPLIT + "}" + "abc"));//[张三, 23, adfsa, 100, null, null]
    }


    /**
     * getset:设置key值，并返回旧值
     */
    @Test
    public void getsetTest() throws InterruptedException {
        System.out.println(jedisCluster.set(nameKey, "zhangsan"));
        System.out.println(jedisCluster.get(nameKey));
        System.out.println(jedisCluster.getSet(nameKey, "lisi"));
        System.out.println(jedisCluster.get(nameKey));
    }

    /**
     * append: 追加. 其返回值是追加后数据的长度
     */
    @Test
    public void appendTest() throws InterruptedException {
        System.out.println(jedisCluster.append(nameKey, "aa"));
        System.out.println(jedisCluster.get(nameKey));
        System.out.println(jedisCluster.append(nameKey, "lisi"));
        System.out.println(jedisCluster.get(nameKey));


    }

}