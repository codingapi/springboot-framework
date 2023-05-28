springboot-starter 功能介绍

## 事件机制
该框架的事件机制是基于ApplicationEvent机制实现的，通过事件机制可以实现应用内部的消息传递，事件机制的使用方式如下：

### 事件定义

```java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 同步事件
 */
@Setter
@Getter
@AllArgsConstructor
public class DemoChangeEvent implements IEvent {

    private String beforeName;
    private String currentName;

}
```
`IEvent`分为`ISyncEvent`和`IAsyncEvent`，分别代表同步事件和异步事件，IEvent等同于ISyncEvent是同步事件。
同步事件与异步事件的区别在于同步事件的事件触发与响应都是在同一个线程中的，而异步事件则是在不同的线程中。
关于事件与事务之间的关系说明：

事件本身不应该同步主业务的事务，即事件对于主业务来说，可成功可失败，成功与失败都不应该强关联主体业务。  
若需要让主体业务与分支做事务同步的时候，那不应该采用事件机制，而应该直接采用调用的方式实现业务绑定。

事件的切换需要更换依赖的接口

```java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 异步事件
 */
@Setter
@Getter
@AllArgsConstructor
public class DemoChangeEvent implements IAsyncEvent {

    private String beforeName;
    private String currentName;

}
```

### 事件发布

事件的发布通过`EventPusher.push(event)`实现，如下：

```java
//push event
EventPusher.push(new DemoChangeEvent(beforeName, name));
```
### 事件订阅

事件的订阅需要依赖`IHandler<Event>`接口，如下：

```java
import com.codingapi.springboot.framework.event.DemoChangeEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Handler
public class DemoChangeLogHandler implements IHandler<DemoChangeEvent> {

    @Override
    public void handler(DemoChangeEvent event) {
        log.info("print before name :{},current name :{}", event.getBeforeName(), event.getCurrentName());
    }

}
```
DemoChangeEvent是事件的定义，DemoChangeLogHandler是事件的订阅者，通过`@Handler`注解标识该类为事件的订阅者，通过`IHandler`接口的泛型指定事件的类型，通过`handler`方法实现事件的处理逻辑。
`@Handler`是一个Bean注解，也可以使用Spring的注解`@Component`代替，如下：

```java
import com.codingapi.springboot.framework.event.DemoChangeEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DemoChangeLogHandler implements IHandler<DemoChangeEvent> {

    @Override
    public void handler(DemoChangeEvent event) {
        log.info("print before name :{},current name :{}", event.getBeforeName(), event.getCurrentName());
    }

}
```
事件的异常处理,可以通过重写`error(exception)`方法实现,异常回掉,在多订阅的情况下,为了实现订阅的独立性,将异常的处理放在回掉函数中。实例如下：

```java

import com.codingapi.springboot.framework.event.DemoChangeEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Handler
public class DemoChangeLogHandler implements IHandler<DemoChangeEvent> {

    @Override
    public void handler(DemoChangeEvent event) {
        log.info("print before name :{},current name :{}", event.getBeforeName(), event.getCurrentName());
    }

    @Override
    public void error(Exception exception) {
        log.error("DemoChangeLogHandler error", exception);
    }
}
```
## Domain行为监听

该框架提供了对domain行为的监听，目前支持的监听由两种。
1. domain对象创建时的监听
2. domain对象中的字段值发生改变时的监听

同时框架提供IDomain接口，该接口提供了domain对象的持久化方法与删除方法：

1. persist()：持久化方法，该方法会触发domain对象的持久化事件
2. delete()：删除方法，该方法会触发domain对象的删除事件


实例domain如下：
```java

import lombok.Getter;

public class Demo implements IDomain {

    @Getter
    private final long id;

    @Getter
    private String name;

    @Getter
    private Animal animal;

    public Demo(String name) {
        this.name = name;
        this.id = System.currentTimeMillis();
        this.animal = new Animal();
        this.animal.setName("cat");
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeAnimalName(String name) {
        this.animal.setName(name);
    }
}

```
无法通过调用domain的构造完成对行为的监听，需要通过`DomainProxyFactory.create()`来创建domain对象.  
实例代码如下：
```java
import com.codingapi.springboot.framework.domain.proxy.DomainProxyFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DomainProxyFactoryTest {

    @Test
    void createEntity() {
        // 在domain对象创建的时候会触发DomainCreateEvent事件
        Demo demo = DomainProxyFactory.create(Demo.class, "test");
        //这里将会抛出FieldChangeEvent事件
        demo.changeAnimalName("123");
        //这里将不会触发事件，因为name值还是test
        demo.changeName("test");
        //这里将会抛出FieldChangeEvent事件
        demo.changeName("test123");
        //这里将会抛出DomainPersistEvent事件
        demo.persist();
        //这里将会抛出DomainDeleteEvent事件
        demo.delete();
        
        System.out.println(demo);
    }
}
```
执行的打印如下：
```
2023-05-28T08:57:00.505+08:00  INFO 13748 --- [           main] c.c.s.f.handler.DemoCreateHandler        : create domain -> com.codingapi.springboot.framework.domain.Demo@4cc12db2
2023-05-28T08:57:00.507+08:00  INFO 13748 --- [           main] c.c.s.f.h.EntityFiledChangeHandler       : field change event -> FieldChangeEvent(simpleName=Demo, timestamp=1685235420507, fieldName=animal.name, oldValue=cat, newValue=123)
2023-05-28T08:57:00.512+08:00  INFO 13748 --- [           main] c.c.s.f.h.EntityFiledChangeHandler       : field change event -> FieldChangeEvent(simpleName=Demo, timestamp=1685235420512, fieldName=name, oldValue=test, newValue=test123)
2023-05-28T08:57:00.513+08:00  INFO 13748 --- [           main] c.c.s.f.handler.DemoPersistEventHandler  : DomainPersistEvent handler DomainPersistEvent(entity=com.codingapi.springboot.framework.domain.Demo@4cc12db2, simpleName=Demo, timestamp=1685235420513)
2023-05-28T08:57:00.516+08:00  INFO 13748 --- [           main] c.c.s.f.handler.DemoDeleteHandler        : delete domain -> com.codingapi.springboot.framework.domain.Demo@4cc12db2

```

## 转换工具
该框架提供了一系列的转换工具，将BeanA转换为BeanB，转换工具的使用方式如下：
```java
  Demo demo = new Demo("xiaoming");
  DemoEntity entity = BeanConvertor.convert(demo, DemoEntity.class);
```

## 序列化能力
该框架提供了一系列的序列化工具，将对象转换为JSON对象，序列化工具的使用方式如下：
```java
  Demo demo = new Demo("xiaoming");
  JSONObject json = JSONObject.parseObject(demo.toJson());
```
将对象转换为Map对象，序列化工具的使用方式如下：
```java
  Demo demo = new Demo("xiaoming");
  Map<String, Object> map = demo.toMap();
```

## 请求与响应约定

为统一controller的请求，提供了一套请求与响应的约定，请求与响应的约定如下：
`PageRequest`主要用户分页的请求参数
```java
    List request(PageRequest request){
        request.setCurrent(2);
        request.setPageSize(10);
        request.getSort();
    }
```
`Response`约定了响应数据格式，返回的字段有
```java
    // 响应状态
    private boolean success;
    // 错误码
    private String errCode;
    // 错误提示
    private String errMessage;
```
`SingleResponse`约定了单个对象的响应数据格式，返回的字段有
```java
    // 响应状态
    private boolean success;
    // 错误码
    private String errCode;
    // 错误提示
    private String errMessage;
    // 响应数据
    private T data;
```
`MultiResponse` 约定了多个对象的响应数据格式，返回的字段有
```java
    // 响应状态
    private boolean success;
    // 错误码
    private String errCode;
    // 错误提示
    private String errMessage;
    // 响应数据
    private Content<T> data;
```
`MapResponse` 约定了Map对象的响应数据格式，返回的字段有
```java
    // 响应状态
    private boolean success;
    // 错误码
    private String errCode;
    // 错误提示
    private String errMessage;
    // 响应数据
    private Map<String, Object> data;
```
## 业务机制
该框架提供了一套异常机制，异常机制的使用方式如下：
```java
    throw new LocaleMessageException("1001", "业务异常");
```
`LocaleMessageException`支持国际化，在resources文件下创建messages.properties的国际化的配置，例如messages_en_US.properties、messages_zh_CN.properties：
```properties
# messages_en_US.properties
hello.error=Hello Error
login.error=login error
token.expire=token expire
api.error=this is an error info
```
```properties
# messages_zh_CN.properties
hello.error=你好 出错了
login.error=\u767B\u9646\u5931\u8D25
token.expire=TOKEN\u8FC7\u671F
api.error=\u8FD9\u662F\u4E00\u4E2A\u9519\u8BEF\u63D0\u793A\u4FE1\u606F
```
在使用LocaleMessageException时直接指定key的名称即可
```java
    throw new LocaleMessageException("hello.error");
```
## 加密工具类
AES加密
```java
package com.codingapi.springboot.framework.crypto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AESTest {

    static byte[] key;
    static byte[] iv;

    @BeforeAll
    static void before() throws Exception {
        AES aes = new AES();
        key = aes.getKey();
        iv = aes.getIv();

        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println("keys:" + encoder.encodeToString(key));
        System.out.println("ivs:" + encoder.encodeToString(iv));

    }

    @Test
    void aes1() throws Exception {
        AES aes = new AES();
        String content = "hello world";
        Base64.Encoder encoder = Base64.getEncoder();
        String encrypt = encoder.encodeToString(aes.encrypt(content.getBytes()));
        System.out.println("encrypt:" + encrypt);

        Base64.Decoder decoder = Base64.getDecoder();
        String decrypt = new String(aes.decrypt(decoder.decode(encrypt)));
        System.out.println("decrypt:" + decrypt);

        assertEquals(content, decrypt, "AES encrypt error");
    }

    @Test
    void aes2() throws Exception {
        AES aes = new AES(key, iv);
        String content = "hello world";
        Base64.Encoder encoder = Base64.getEncoder();
        String encrypt = encoder.encodeToString(aes.encrypt(content.getBytes()));
        System.out.println("encrypt:" + encrypt);
    }

}
```
AES加密
```java
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class DESTest {

    @Test
    void getKey() throws NoSuchAlgorithmException {
        DES des = new DES();
        byte[] key = des.getKey();
        assertNotNull(key);
    }

    @Test
    void encryptAndDecrypt() throws Exception {
        DES des = new DES();
        String word = "123";
        byte[] encrypt = des.encrypt(word.getBytes());
        byte[] decrypt = des.decrypt(encrypt);
        assertEquals(word,new String(decrypt));
    }
 
}
```
RSA加密
```java

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class RSATest {

    @Test
    void getPrivateKey()  throws NoSuchAlgorithmException {
        RSA rsa = new RSA();
        assertNotNull(rsa.getPrivateKey());
        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println("privateKey:" + encoder.encodeToString(rsa.getPrivateKey()));
    }

    @Test
    void getPublicKey() throws NoSuchAlgorithmException {
        RSA rsa = new RSA();
        assertNotNull(rsa.getPublicKey());
        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println("publicKey:" + encoder.encodeToString(rsa.getPublicKey()));
    }

    @Test
    void encryptAndDecrypt() throws Exception {
        RSA rsa = new RSA();
        String content = "hello world";
        byte[] encrypt = rsa.encrypt(content.getBytes());
        byte[] data = rsa.decrypt(encrypt);
        assertEquals(content,new String(data));
    }

}
```

## 数学工具类
为了解决浮点数计算精度问题，该框架提供了`Arithmetic`工具类，该工具类支持链式调用，使用方式如下：
```java
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArithmeticTest {

    @Test
    void test() {
        // 1 + 1 x 3 / 4 = 1.25
        assertEquals(Arithmetic.one().add(1).mul(3).div(4).getDoubleValue(),1.5);

        // 0.1+0.2=0.3
        assertEquals(Arithmetic.parse(0.1).add(0.2).getDoubleValue(),0.3);

        // (1.5 x 3.1) + (3.4 x 4.5) = 19.95
        assertEquals((Arithmetic.parse(1.5).mul(3.1)).add(Arithmetic.parse(3.4).mul(4.5)).getDoubleValue(),19.95);
    }
}
```

##  Http工具类
SessionClient工具类提供了对Cookies的缓存处理能力
```java
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class SessionClientTest {

    @Test
    void test(){
        SessionClient client = new SessionClient();
        String html = client.getHtml("https://www.baidu.com");
        assertNotNull(html);
    }
}
```
RestClient工具类提供了对Restful接口的调用能力
```java

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.rest.param.RestParamBuilder;
import com.codingapi.springboot.framework.rest.properties.HttpProxyProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class RestClientTest {

    @Test
    void okxTest() {
        String baseUrl = "https://www.okx.com/";
        HttpProxyProperties proxyProperties = new HttpProxyProperties();
        // 设置代理
        proxyProperties.setEnableProxy(true);
        proxyProperties.setProxyType(Proxy.Type.HTTP);
        proxyProperties.setProxyHost("127.0.0.1");
        proxyProperties.setProxyPort(7890);
        RestClient restClient = new RestClient(proxyProperties,baseUrl,5,"{}",null,null);
        String response = restClient.get("api/v5/market/candles", RestParamBuilder.create()
                .add("instId","BTC-USDT")
                .add("bar","1m")
                .add("limit","300")
        );
        log.info("response:{}",response);
        JSONObject jsonObject = JSONObject.parseObject(response);
        assertEquals(jsonObject.getJSONArray("data").size(),300);
    }
}
```
## 动态服务能力
`DynamicApplication` 提供动态的添加第三方的jar包的能力，替换SpringApplication的启动服务为DynamicApplication启动：
```java
import com.codingapi.springboot.framework.boot.DynamicApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FrameWorkApplication {

    public static void main(String[] args) {
        DynamicApplication.run(FrameWorkApplication.class, args);
    }
}
```

只要当前运行环境的./jars路径下存在第三方的jar，可以通过执行`DynamicApplication.restart()`方法，动态的加载第三方的jar包，实现动态的服务能力。
