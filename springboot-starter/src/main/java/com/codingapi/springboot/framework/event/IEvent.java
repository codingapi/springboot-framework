package com.codingapi.springboot.framework.event;


/**
 * 默认同步事件
 * <p>
 * 关于事件与事务之间的关系说明:
 * 事件本身不应该同步主业务的事务，即事件对于主业务来说，可成功可失败，成功与失败都不应该强关联主体业务。
 * 若需要让主体业务与分支做事务同步的时候，那不应该采用事件机制，而应该直接采用调用的方式实现业务绑定。
 */
public interface IEvent {


}
