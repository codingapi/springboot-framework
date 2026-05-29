package com.codingapi.springboot.script.scanner;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.annotation.ScriptField;
import com.codingapi.springboot.script.annotation.ScriptFunction;
import com.codingapi.springboot.script.annotation.ScriptParameter;
import com.codingapi.springboot.script.annotation.ScriptType;
import com.codingapi.springboot.script.strategy.ScriptTypeMappingContext;
import com.codingapi.springboot.script.meta.GroovyField;
import com.codingapi.springboot.script.meta.GroovyFunction;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.meta.GroovyType;
import com.codingapi.springboot.script.strategy.GroovyMetadataGenerateStrategyContext;
import com.codingapi.springboot.script.strategy.GroovyTypeFixStrategyContext;
import lombok.Getter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 脚本元数据对象扫描工具
 */
public class GroovyMetadataScannerUtils {

    public static GroovyMetadata scanner(GroovyScript groovyScript) {
        // 手动构建的元数据则，直接返回
        GroovyMetadata metadata = GroovyMetadataGenerateStrategyContext.getInstance().generate(groovyScript);
        if (metadata != null) {
            return metadata;
        }
        // 扫描元数据信息
        GroovyMetadataHolder groovyMetadataHolder = new GroovyMetadataHolder(groovyScript);
        groovyMetadataHolder.scanner();
        return groovyMetadataHolder.getMetadata();
    }


    /**
     * 脚本元数据持有者
     */
    private static class GroovyMetadataHolder {

        @Getter
        private final GroovyMetadata metadata;
        private final GroovyScript groovyScript;

        private final List<Class<?>> scannerHistoryList;

        public GroovyMetadataHolder(GroovyScript groovyScript) {
            this.metadata = new GroovyMetadata(groovyScript);
            this.groovyScript = groovyScript;
            this.scannerHistoryList = new ArrayList<>();
        }

        /**
         * 添加扫描历史记录
         *
         * @param target 扫描对象
         */
        public void addHistory(Class<?> target) {
            this.scannerHistoryList.add(target);
        }

        /**
         * 是否在历史扫描对象
         *
         * @param target 扫描对象
         */
        public boolean hasHistory(Class<?> target) {
            return this.scannerHistoryList.contains(target);
        }


        /**
         * 扫描请求对象的数据类型
         */
        private void scannerRequestTypes() {
            Map<String, Class<?>> requests = groovyScript.getRequests();
            if (requests != null && !requests.isEmpty()) {
                for (Class<?> objClass : requests.values()) {
                    GroovyTypeScanner scanner = new GroovyTypeScanner(objClass, this);
                    scanner.scanner();
                }
            }
        }

        /**
         * 扫描绑定对象的数据类型
         */
        private void scannerBindTypes() {
            Map<String, Class<?>> binds = groovyScript.getBinds();
            if (binds != null && !binds.isEmpty()) {
                for (Class<?> objClass : binds.values()) {
                    GroovyTypeScanner scanner = new GroovyTypeScanner(objClass, this);
                    scanner.scanner();
                }
            }
        }

        /**
         * 扫描返回数据对象数据类型
         */
        private void scannerReturnType() {
            Class<?> returnTypeClass = this.groovyScript.getReturnType();
            if (returnTypeClass != null) {
                GroovyTypeScanner scanner = new GroovyTypeScanner(returnTypeClass, this);
                scanner.scanner();
            }
        }


        /**
         * 扫描对象
         */
        public void scanner() {
            this.scannerRequestTypes();
            this.scannerBindTypes();
            this.scannerReturnType();

            this.loadRequestFields();
            this.loadBindFields();
        }

        /**
         * 加载请求参数字段信息
         */
        private void loadRequestFields() {
            Map<String, Class<?>> requests = groovyScript.getRequests();
            if (requests != null && !requests.isEmpty()) {
                for (String key : requests.keySet()) {
                    Class<?> objClass = this.getTargetClass(requests.get(key));
                    String dataType = objClass.getSimpleName();
                    GroovyField groovyField = new GroovyField();
                    groovyField.setDataType(dataType);
                    groovyField.setName(key);
                    GroovyType groovyType = this.metadata.getType(dataType);
                    if (groovyType != null) {
                        groovyField.setDescription(groovyType.getDescription());
                    }
                    this.metadata.addRequest(groovyField);
                }
            }
        }

        /**
         * 添加数据类型
         */
        public void addType(GroovyType groovyType) {
            this.metadata.put(groovyType.getDataType(), groovyType);
        }


        /**
         * 加载绑定对象的参数信息
         */
        private void loadBindFields() {
            Map<String, Class<?>> binds = groovyScript.getBinds();
            if (binds != null && !binds.isEmpty()) {
                for (String key : binds.keySet()) {
                    Class<?> objClass = this.getTargetClass(binds.get(key));
                    GroovyField groovyField = new GroovyField();
                    String dataType = objClass.getSimpleName();
                    groovyField.setDataType(dataType);
                    groovyField.setName(key);
                    GroovyType groovyType = this.metadata.getType(dataType);
                    if (groovyType != null) {
                        groovyField.setDescription(groovyType.getDescription());
                    }
                    this.metadata.addBind(groovyField);
                }
            }
        }

        /**
         * 获取实际的类型对象
         *
         * @param type 类型
         * @return 实际类型
         */
        public Class<?> getTargetClass(Class<?> type) {
            return ScriptTypeMappingContext.getInstance().mapping(type);
        }


    }

    /**
     * 脚本扫描对象
     */
    private static class GroovyTypeScanner {

        private final Class<?> clazz;
        private final GroovyType object;
        private final GroovyMetadataHolder holder;

        public GroovyTypeScanner(Class<?> clazz, GroovyMetadataHolder holder) {
            this.clazz = holder.getTargetClass(clazz);
            this.object = new GroovyType();
            this.holder = holder;
        }


        /**
         * 加载字段信息
         */
        private void loadFields() {
            ReflectionUtils.doWithFields(this.clazz, field -> {
                ScriptField scriptField = field.getAnnotation(ScriptField.class);
                if (scriptField != null) {
                    Class<?> clazz = this.holder.getTargetClass(field.getType());

                    GroovyTypeScanner scanner = new GroovyTypeScanner(clazz, this.holder);
                    scanner.scanner();

                    GroovyField groovyField = new GroovyField();
                    groovyField.setDataType(clazz.getSimpleName());
                    groovyField.setName(field.getName());

                    if (StringUtils.hasText(scriptField.name())) {
                        groovyField.setName(scriptField.name());
                    }
                    if (StringUtils.hasText(scriptField.description())) {
                        groovyField.setDescription(scriptField.description());
                    }
                    this.object.addField(groovyField);
                }
            });
        }

        /**
         * 加载函数信息
         */
        private void loadMethods() {
            ReflectionUtils.doWithMethods(this.clazz, method -> {
                ScriptFunction scriptFunction = method.getAnnotation(ScriptFunction.class);
                if (scriptFunction != null) {
                    GroovyFunction groovyFunction = new GroovyFunction();
                    groovyFunction.setName(method.getName());

                    if (StringUtils.hasText(scriptFunction.name())) {
                        groovyFunction.setName(scriptFunction.name());
                    }

                    if (StringUtils.hasText(scriptFunction.description())) {
                        groovyFunction.setDescription(scriptFunction.description());
                    }

                    Parameter[] methodParameters = method.getParameters();
                    for (Parameter methodParameter : methodParameters) {
                        Class<?> parameterClass = this.holder.getTargetClass(methodParameter.getType());

                        GroovyTypeScanner scanner = new GroovyTypeScanner(parameterClass, this.holder);
                        scanner.scanner();

                        GroovyField groovyParameter = new GroovyField();
                        groovyParameter.setDataType(parameterClass.getSimpleName());
                        groovyParameter.setName(methodParameter.getName());

                        ScriptParameter scriptParameter = methodParameter.getAnnotation(ScriptParameter.class);
                        if (scriptParameter != null) {
                            if (StringUtils.hasText(scriptParameter.name())) {
                                groovyParameter.setName(scriptParameter.name());
                            }
                            if (StringUtils.hasText(scriptParameter.description())) {
                                groovyParameter.setDescription(scriptParameter.description());
                            }
                        }

                        groovyFunction.addParameter(groovyParameter);
                    }

                    Class<?> returnType = this.holder.getTargetClass(method.getReturnType());
                    GroovyTypeScanner scanner = new GroovyTypeScanner(returnType, this.holder);
                    scanner.scanner();
                    groovyFunction.setReturnType(returnType.getSimpleName());

                    this.object.addFunction(groovyFunction);
                }
            });
        }

        /**
         * 是否简单数据结构
         */
        private boolean isSimpleType() {
            return this.clazz.isPrimitive()
                    || ClassUtils.isPrimitiveOrWrapper(this.clazz)
                    || this.clazz == String.class
                    || this.clazz.isEnum()
                    || Number.class.isAssignableFrom(this.clazz)
                    || CharSequence.class.isAssignableFrom(this.clazz);
        }


        /**
         * 扫描类对象
         */
        public void scanner() {
            this.object.setDataType(this.clazz.getSimpleName());
            ScriptType scriptType = this.clazz.getAnnotation(ScriptType.class);
            if (scriptType != null) {
                this.object.setDescription(scriptType.description());
            }

            // 简单数据类型不处理
            if (this.isSimpleType()) {
                return;
            }

            if (holder.hasHistory(this.clazz)) {
                return;
            }
            this.holder.addHistory(this.clazz);
            this.loadFields();
            this.loadMethods();

            // 调整元数据
            GroovyTypeFixStrategyContext.getInstance().fix(this.holder.groovyScript, this.clazz, this.object);

            // 添加数据类型
            this.holder.addType(this.object);
        }

    }

}
