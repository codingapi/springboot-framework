package com.codingapi.springboot.fast.generator;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
import org.hibernate.tool.schema.SourceType;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.internal.ExceptionHandlerCollectingImpl;
import org.hibernate.tool.schema.spi.*;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * DynamicTableGenerator 数据库表动态构建器
 */
public class DynamicTableGenerator {

    /**
     * 数据库方言
     */
    private final Dialect dialect;
    private final StandardServiceRegistry serviceRegistry;
    private final SchemaManagementTool managementTool;

    public DynamicTableGenerator(Class<?> dialectClass, String jdbcUrl) {
        this(dialectClass,jdbcUrl,null,null);
    }

    public DynamicTableGenerator(Class<?> dialectClass, String jdbcUrl, String username, String password) {
        try {
            this.dialect = (Dialect) dialectClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate dialect", e);
        }

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.DIALECT, dialect.getClass().getName())
                .applySetting("hibernate.connection.url", jdbcUrl);
        if (StringUtils.hasText(username)) {
            builder.applySetting("hibernate.connection.username", username);
        }
        if (StringUtils.hasText(password)) {
            builder.applySetting("hibernate.connection.password", password);
        }
        this.serviceRegistry = builder.build();
        this.managementTool = serviceRegistry.getService(SchemaManagementTool.class);
    }

    private class ExecutionOptionsImpl implements ExecutionOptions {

        @Override
        public Map<String, Object> getConfigurationValues() {
            Map<String, Object> config = new HashMap<>();
            config.put(AvailableSettings.DIALECT, dialect.getClass().getName());
            return config;
        }

        @Override
        public boolean shouldManageNamespaces() {
            return false;
        }

        @Override
        public ExceptionHandler getExceptionHandler() {
            return new ExceptionHandlerCollectingImpl();
        }
    }

    private static class SourceDescriptorImpl implements SourceDescriptor {

        @Override
        public SourceType getSourceType() {
            return SourceType.METADATA;
        }

        @Override
        public ScriptSourceInput getScriptSourceInput() {
            return new ScriptSourceInput() {
                @Override
                public void prepare() {

                }

                @Override
                public List<String> read(ImportSqlCommandExtractor importSqlCommandExtractor) {
                    return new ArrayList<>();
                }

                @Override
                public void release() {

                }
            };
        }
    }

    private static class TargetDescriptorImpl implements TargetDescriptor {
        private final List<String> sqlCommands = new ArrayList<>();
        private final boolean executable;

        public TargetDescriptorImpl(boolean executable) {
            this.executable = executable;
        }

        @Override
        public EnumSet<TargetType> getTargetTypes() {
            EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.SCRIPT);
            if(executable) {
                // 在数据库中执行
                targetTypes.add(TargetType.DATABASE);
            }
            return targetTypes;
        }

        @Override
        public ScriptTargetOutput getScriptTargetOutput() {
            return new ScriptTargetOutput() {
                @Override
                public void prepare() {

                }

                @Override
                public void accept(String command) {
                    sqlCommands.add(command);
                }

                @Override
                public void release() {

                }
            };
        }

        public String getDDL() {
            return String.join("\n", sqlCommands);
        }
    }

    /**
     * 创建删除DDL语句
     * @param entityClass entity class对象
     * @return DDL
     */
    public String generateDropTableDDL(Class<?> entityClass) {
        return this.generateDropTableDDL(entityClass,false);
    }

    /**
     * 创建删除DDL语句
     * @param entityClass entity class对象
     * @param executable 是否数据中执行
     * @return DDL
     */
    public String generateDropTableDDL(Class<?> entityClass,boolean executable) {
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(entityClass);
        Metadata metadata = metadataSources.buildMetadata();
        TargetDescriptorImpl targetDescriptor = new TargetDescriptorImpl(executable);

        managementTool.getSchemaDropper(Collections.emptyMap()).doDrop(metadata,
                new ExecutionOptionsImpl(),
                new SourceDescriptorImpl(),
                targetDescriptor);

        return targetDescriptor.getDDL();
    }

    /**
     * 创建创建DDL语句
     * @param entityClass entity class对象
     * @return DDL
     */
    public String generateCreateTableDDL(Class<?> entityClass) {
        return this.generateCreateTableDDL(entityClass,false);
    }

    /**
     * 创建创建DDL语句
     * @param entityClass entity class对象
     * @param executable 是否数据中执行
     * @return DDL
     */
    public String generateCreateTableDDL(Class<?> entityClass,boolean executable) {
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(entityClass);
        Metadata metadata = metadataSources.buildMetadata();

        TargetDescriptorImpl targetDescriptor = new TargetDescriptorImpl(executable);

        managementTool.getSchemaCreator(Collections.emptyMap()).doCreation(metadata,
                new ExecutionOptionsImpl(),
                new SourceDescriptorImpl(),
                targetDescriptor);
        return targetDescriptor.getDDL();
    }

    /**
     * 创建变更DDL语句，当数据不存在时则为创建，当数据中的表对象与entityClass不一致时则创建调整DDL
     * @param entityClass entity class对象
     * @return DDL
     */
    public String generateMigratorTableDDL(Class<?> entityClass) {
        return this.generateMigratorTableDDL(entityClass,false);
    }

    /**
     * 创建变更DDL语句，当数据不存在时则为创建，当数据中的表对象与entityClass不一致时则创建调整DDL
     * @param entityClass entity class对象
     * @param executable 是否数据中执行
     * @return DDL
     */
    public String generateMigratorTableDDL(Class<?> entityClass,boolean executable) {
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(entityClass);
        Metadata metadata = metadataSources.buildMetadata();

        TargetDescriptorImpl targetDescriptor = new TargetDescriptorImpl(executable);

        managementTool.getSchemaMigrator(Collections.emptyMap()).doMigration(metadata,
                new ExecutionOptionsImpl(),
                targetDescriptor);
        return targetDescriptor.getDDL();
    }

    /**
     * 检验对象与数据库是否一致，一致时无异常信息
     * @param entityClass entity class对象
     * @return 异常列表
     */
    public List<Exception> validatorTable(Class<?> entityClass) {
        ExceptionHandlerCollectingImpl exceptionHandler = new ExceptionHandlerCollectingImpl();
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(entityClass);
        Metadata metadata = metadataSources.buildMetadata();
        List<Exception> exceptionList = new ArrayList<>();
        try {
            managementTool.getSchemaValidator(Collections.emptyMap()).doValidation(metadata,
                    new ExecutionOptionsImpl() {
                        @Override
                        public ExceptionHandler getExceptionHandler() {
                            return exceptionHandler;
                        }
                    });
        } catch (Exception e) {
            exceptionList.add(e);
        }
        exceptionList.addAll(exceptionHandler.getExceptions());
        return exceptionList;
    }

}
