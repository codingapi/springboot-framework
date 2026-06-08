---
name: command-executor
description: CQRS 命令执行器接口，定义标准化的命令-响应契约，支持带参和无参两种执行模式
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

在 CQRS（命令查询职责分离）架构中，写操作（Command）和读操作（Query）需要明确分离。实际开发中常见以下问题：

1. **Service 方法签名不统一**：有的方法返回 void，有的返回 DTO，有的返回 Response，调用方无法预期
2. **命令对象缺乏规范**：不同开发者定义的命令参数格式各异，缺少统一的输入输出契约
3. **响应与命令脱节**：命令执行后的结果没有标准化的封装方式，成功/失败判断逻辑分散
4. **DDD 应用层编排困难**：Application Service 需要协调多个领域服务，但缺乏清晰的命令执行抽象

`IExecutor` 接口定义了两种命令执行器契约：
- `IExecutor.Command<R, C>` — 接收命令对象 C，返回响应 R
- `IExecutor.Void<R>` — 无参执行，返回响应 R

其中 R 必须继承自 `Response`，确保所有命令执行结果都遵循统一响应格式。

## 如何使用

### 1. 定义命令对象

命令对象是纯数据载体，描述"要做什么"：

```java
@Getter
@Setter
public class CreateUserCommand {
    private String name;
    private String email;
    private Integer age;
}
```

### 2. 实现 Command 执行器

```java
@Component
public class CreateUserExecutor implements IExecutor.Command<SingleResponse<UserDTO>, CreateUserCommand> {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public SingleResponse<UserDTO> execute(CreateUserCommand command) {
        User user = new User();
        user.setName(command.getName());
        user.setEmail(command.getEmail());
        user.setAge(command.getAge());
        
        userRepository.save(user);
        
        UserDTO dto = convertToDTO(user);
        return SingleResponse.of(dto);
    }
}
```

### 3. 实现 Void 执行器

适用于不需要传入命令对象的场景：

```java
@Component
public class SystemCleanupExecutor implements IExecutor.Void<Response> {
    
    @Autowired
    private CacheService cacheService;
    
    @Override
    public Response execute() {
        cacheService.clearAll();
        return Response.buildSuccess();
    }
}
```

### 4. 在 Controller 或 Application Service 中调用

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private CreateUserExecutor createUserExecutor;
    
    @PostMapping
    public SingleResponse<UserDTO> createUser(@RequestBody CreateUserCommand cmd) {
        return createUserExecutor.execute(cmd);
    }
}
```

## 使用实例

### DDD 应用层中的命令编排

```java
// ===== 命令定义 =====

@Getter @Setter
public class TransferMoneyCommand {
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String remark;
}

// ===== 执行器实现 =====

@Component
public class TransferMoneyExecutor 
        implements IExecutor.Command<SingleResponse<TransactionDTO>, TransferMoneyCommand> {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Override
    @Transactional
    public SingleResponse<TransactionDTO> execute(TransferMoneyCommand cmd) {
        // 1. 加载聚合根
        Account fromAccount = accountRepository.findById(cmd.getFromAccountId())
            .orElseThrow(() -> LocaleMessageException.of("ACCOUNT_NOT_FOUND", cmd.getFromAccountId()));
        Account toAccount = accountRepository.findById(cmd.getToAccountId())
            .orElseThrow(() -> LocaleMessageException.of("ACCOUNT_NOT_FOUND", cmd.getToAccountId()));
        
        // 2. 执行业务逻辑
        fromAccount.debit(cmd.getAmount());
        toAccount.credit(cmd.getAmount());
        
        // 3. 保存状态
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        // 4. 创建交易记录
        Transaction tx = new Transaction(fromAccount, toAccount, cmd.getAmount(), cmd.getRemark());
        transactionRepository.save(tx);
        
        // 5. 推送领域事件
        EventPusher.push(new MoneyTransferredEvent(tx.getId()));
        
        return SingleResponse.of(convertToDTO(tx));
    }
}

// ===== Controller =====

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    
    @Autowired
    private TransferMoneyExecutor transferExecutor;
    
    @PostMapping
    public SingleResponse<TransactionDTO> transfer(@RequestBody TransferMoneyCommand cmd) {
        return transferExecutor.execute(cmd);
    }
}
```

### 批量命令执行器

```java
@Getter @Setter
public class BatchImportUsersCommand {
    private List<UserImportItem> users;
}

@Component
public class BatchImportUsersExecutor 
        implements IExecutor.Command<MultiResponse<UserDTO>, BatchImportUsersCommand> {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public MultiResponse<UserDTO> execute(BatchImportUsersCommand cmd) {
        List<UserDTO> results = new ArrayList<>();
        
        for (UserImportItem item : cmd.getUsers()) {
            User user = new User();
            user.setName(item.getName());
            user.setEmail(item.getEmail());
            userRepository.save(user);
            results.add(convertToDTO(user));
        }
        
        return MultiResponse.of(results, results.size());
    }
}
```

### 在 Application Service 中组合多个执行器

```java
@Service
public class OrderApplicationService {
    
    @Autowired
    private CreateOrderExecutor createOrderExecutor;
    
    @Autowired
    private DeductInventoryExecutor deductInventoryExecutor;
    
    @Autowired
    private SendNotificationExecutor sendNotificationExecutor;
    
    @Transactional
    public SingleResponse<OrderDTO> placeOrder(CreateOrderCommand cmd) {
        // 1. 创建订单
        SingleResponse<OrderDTO> orderResponse = createOrderExecutor.execute(cmd);
        if (!orderResponse.isSuccess()) {
            return orderResponse;
        }
        
        // 2. 扣减库存
        DeductInventoryCommand inventoryCmd = new DeductInventoryCommand();
        inventoryCmd.setOrderId(orderResponse.getData().getId());
        inventoryCmd.setItems(cmd.getItems());
        Response inventoryResp = deductInventoryExecutor.execute(inventoryCmd);
        if (!inventoryResp.isSuccess()) {
            throw LocaleMessageException.of("INVENTORY_DEDUCT_FAILED");
        }
        
        // 3. 发送通知（异步，不影响主流程）
        try {
            sendNotificationExecutor.execute(
                new SendOrderNotificationCommand(orderResponse.getData().getId()));
        } catch (Exception e) {
            log.warn("Failed to send order notification", e);
        }
        
        return orderResponse;
    }
}
```

### IExecutor 接口结构

```java
public interface IExecutor {
    
    /**
     * 带命令参数的执行器
     * @param <R> 响应类型，必须继承 Response
     * @param <C> 命令类型
     */
    interface Command<R extends Response, C> {
        R execute(C command);
    }
    
    /**
     * 无参数的执行器
     * @param <R> 响应类型，必须继承 Response
     */
    interface Void<R extends Response> {
        R execute();
    }
}
```

### 设计原则

1. **单一职责**：每个执行器只负责一个业务命令
2. **响应标准化**：返回值必须是 `Response` 或其子类
3. **命令不可变**：命令对象作为输入不应在执行过程中被修改
4. **事务边界清晰**：`@Transactional` 加在执行器方法上，而非 Controller
5. **异常即响应**：业务异常通过 `LocaleMessageException` 抛出，由全局处理器转为标准错误响应
