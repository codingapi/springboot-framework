springboot-starter-id-generator

一个单服务下的id自增构造器，为了解决id由数据库生成的问题，如果是分布式服务，可以使用snowflake算法生成id，如果是单服务，可以使用这个构造器生成id

```java

import com.codingapi.springboot.generator.IdGenerate;
import lombok.Getter;

/**
 * @author lorne
 * @since 1.0.0
 */
public class Demo implements IdGenerate {

    @Getter
    private Integer id;

    @Getter
    private String name;

    public Demo(String name) {
        this.id = generateIntId();
        this.name = name;
    }
    
}

```

只需要实现IdGenerate接口即可，然后调用generateIntId()方法即可生成id。

IdGenerate接口提供了两个方法，generateIntId()和generateLongId()，分别用于生成int类型和long类型的id。
也可以使用generateStringId()方法生成String类型的id。