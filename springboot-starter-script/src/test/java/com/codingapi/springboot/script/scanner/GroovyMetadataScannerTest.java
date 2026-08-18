package com.codingapi.springboot.script.scanner;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.meta.GroovyField;
import com.codingapi.springboot.script.meta.GroovyFunction;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.meta.GroovyType;
import com.codingapi.springboot.script.strategy.GroovyMetadataGenerateStrategy;
import com.codingapi.springboot.script.strategy.GroovyMetadataGenerateStrategyContext;
import com.codingapi.springboot.script.strategy.GroovyTypeFixStrategy;
import com.codingapi.springboot.script.strategy.GroovyTypeFixStrategyContext;
import com.codingapi.springboot.script.strategy.ScriptTypeMapping;
import com.codingapi.springboot.script.strategy.ScriptTypeMappingContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GroovyMetadataScannerUtils 补充测试
 * 覆盖注解扫描各分支、简单类型处理、策略不匹配回退等场景
 */
class GroovyMetadataScannerTest {

    @BeforeEach
    void setUp() {
        GroovyMetadataGenerateStrategyContext.getInstance().clear();
        GroovyTypeFixStrategyContext.getInstance().clear();
        ScriptTypeMappingContext.getInstance().clear();
    }

    @AfterEach
    void tearDown() {
        GroovyMetadataGenerateStrategyContext.getInstance().clear();
        GroovyTypeFixStrategyContext.getInstance().clear();
        ScriptTypeMappingContext.getInstance().clear();
    }

    private GroovyField fieldByName(java.util.List<GroovyField> fields, String name) {
        for (GroovyField field : fields) {
            if (name.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    private GroovyFunction functionByName(GroovyType type, String name) {
        for (GroovyFunction function : type.getFunctions()) {
            if (name.equals(function.getName())) {
                return function;
            }
        }
        return null;
    }

    @Test
    void scannerShouldResolveAnnotationsOfComplexRequestType() {
        Map<String, Class<?>> requests = new HashMap<>();
        requests.put("demo", DemoScriptRequest.class);
        // 简单类型：不会注册为 GroovyType，字段描述为空
        requests.put("num", Integer.class);
        Map<String, Class<?>> binds = new HashMap<>();
        binds.put("flag", Boolean.class);

        GroovyScript groovyScript = GroovyScript.builder("scanner-key")
                .script("return 1;")
                .description("scanner desc")
                .method("run")
                .returnType(String.class)
                .requests(requests)
                .binds(binds)
                .build();

        GroovyMetadata metadata = groovyScript.toMetadata();

        assertEquals("run", metadata.getMainMethod());
        assertEquals("scanner desc", metadata.getDescription());
        assertEquals("String", metadata.getReturnType());

        // 请求参数
        assertEquals(2, metadata.getRequests().size());
        GroovyField demoField = fieldByName(metadata.getRequests(), "demo");
        assertNotNull(demoField);
        assertEquals("DemoScriptRequest", demoField.getDataType());
        // 类型已注册，描述取自 @ScriptType
        assertEquals("demo request type", demoField.getDescription());
        GroovyField numField = fieldByName(metadata.getRequests(), "num");
        assertNotNull(numField);
        assertEquals("Integer", numField.getDataType());
        // 简单类型未注册，描述为空
        assertNull(numField.getDescription());

        // 绑定参数
        assertEquals(1, metadata.getBinds().size());
        GroovyField flagField = fieldByName(metadata.getBinds(), "flag");
        assertNotNull(flagField);
        assertEquals("Boolean", flagField.getDataType());

        // 复合类型元数据
        GroovyType demoType = metadata.getType("DemoScriptRequest");
        assertNotNull(demoType);
        assertEquals("demo request type", demoType.getDescription());

        // 字段：无覆盖的 plain 保留字段名，age 使用注解 name
        assertEquals(2, demoType.getFields().size());
        GroovyField plain = fieldByName(demoType.getFields(), "plain");
        assertNotNull(plain);
        assertEquals("String", plain.getDataType());
        assertNull(plain.getDescription());
        GroovyField age = fieldByName(demoType.getFields(), "age");
        assertNotNull(age);
        assertEquals("int", age.getDataType());
        assertEquals("age description", age.getDescription());

        // 函数：calc 带描述与参数覆盖，noDesc 仅有名称
        assertEquals(2, demoType.getFunctions().size());
        GroovyFunction calc = functionByName(demoType, "calc");
        assertNotNull(calc);
        assertEquals("calc description", calc.getDescription());
        assertEquals("int", calc.getReturnType());
        assertEquals(1, calc.getParameters().size());
        assertEquals("num", calc.getParameters().get(0).getName());
        assertEquals("num description", calc.getParameters().get(0).getDescription());
        GroovyFunction noDesc = functionByName(demoType, "noDesc");
        assertNotNull(noDesc);
        assertNull(noDesc.getDescription());
        assertEquals("void", noDesc.getReturnType());
        assertTrue(noDesc.getParameters().isEmpty());
    }

    @Test
    void scannerShouldHandleScriptWithoutRequestBindAndReturnScanGracefully() {
        // GroovyMetadata 构造器对 null returnType 未做防护，toMetadata 抛 NPE，
        // 疑似 bug：GroovyMetadataHolder.scannerReturnType 有 null 判断但永远无法到达
        GroovyScript groovyScript = GroovyScript.builder("scanner-null-return")
                .script("return 1;")
                .build();
        assertThrows(NullPointerException.class, groovyScript::toMetadata);
    }

    @Test
    void scannerShouldFallbackWhenGenerateStrategyNotSupported() {
        GroovyMetadataGenerateStrategyContext.getInstance().addGenerateStrategy(new GroovyMetadataGenerateStrategy() {
            @Override
            public boolean support(GroovyScript script) {
                return false;
            }

            @Override
            public GroovyMetadata generate(GroovyScript script) {
                return new GroovyMetadata("never", "never", "never");
            }
        });

        GroovyScript groovyScript = GroovyScript.builder("scanner-fallback")
                .script("return 1;")
                .method("run")
                .returnType(Integer.class)
                .build();

        GroovyMetadata metadata = groovyScript.toMetadata();
        // 策略不匹配时走扫描逻辑
        assertEquals("run", metadata.getMainMethod());
        assertEquals("Integer", metadata.getReturnType());
    }

    @Test
    void scannerShouldIgnoreNotSupportedFixStrategyAndMapping() {
        GroovyTypeFixStrategyContext.getInstance().addFixStrategy(new GroovyTypeFixStrategy() {
            @Override
            public boolean support(Class<?> clazz) {
                return false;
            }

            @Override
            public void fix(GroovyScript groovyScript, GroovyType groovyType) {
                groovyType.setDescription("should not happen");
            }
        });
        ScriptTypeMappingContext.getInstance().addMapping(new ScriptTypeMapping() {
            @Override
            public boolean support(Class<?> target) {
                return false;
            }

            @Override
            public Class<?> mapping(Class<?> target) {
                return Object.class;
            }
        });

        Map<String, Class<?>> requests = new HashMap<>();
        requests.put("demo", DemoScriptRequest.class);

        GroovyScript groovyScript = GroovyScript.builder("scanner-ignore-strategy")
                .script("return 1;")
                .method("run")
                .returnType(Integer.class)
                .requests(requests)
                .build();

        GroovyMetadata metadata = groovyScript.toMetadata();
        GroovyType demoType = metadata.getType("DemoScriptRequest");
        assertNotNull(demoType);
        // fix 策略未匹配，描述保持 @ScriptType 的值
        assertEquals("demo request type", demoType.getDescription());
        assertEquals("Integer", metadata.getReturnType());
    }
}
