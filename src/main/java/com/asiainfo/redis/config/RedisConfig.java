package com.asiainfo.redis.config;

        import lombok.Data;
        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.stereotype.Component;

        import java.util.List;



@Data
@Component
@ConfigurationProperties(prefix = "spring.redis.config")
public class RedisConfig{

    /**
     * 集群节点
     */
    private List<String> clusterNodes;

    /**
     * 密码
     */
    private String password;

    /**
     * 连接超时时间
     */
    private int connectionTimeout;

    /**
     *读取数据超时时间
     */
    private int soTimeout;

    /**
     *最大尝试次数
     */
    private int maxAttempts;

}