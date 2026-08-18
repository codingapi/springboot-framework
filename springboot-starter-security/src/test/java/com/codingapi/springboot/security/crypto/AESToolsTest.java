package com.codingapi.springboot.security.crypto;

import com.codingapi.springboot.framework.crypto.AES;
import com.codingapi.springboot.framework.crypto.AESUtils;
import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * AESTools 单元测试
 * <p>
 * 覆盖字符串与字节数组的加解密往返逻辑。
 * 测试类与 AESTools 同包，可调用包级私有的 init 方法。
 */
class AESToolsTest {

    @BeforeEach
    void setUp() throws Exception {
        AES aes = new AES(Base64.getDecoder().decode(AESUtils.key), Base64.getDecoder().decode(AESUtils.iv));
        AESTools.getInstance().init(aes);
    }

    @Test
    void getInstanceReturnsSingleton() {
        assertSame(AESTools.getInstance(), AESTools.getInstance());
    }

    @Test
    void encodeAndDecodeStringRoundTrip() {
        String input = "hello-世界-123456";

        String encoded = AESTools.getInstance().encode(input);
        assertNotNull(encoded);
        assertNotEquals(input, encoded);

        String decoded = AESTools.getInstance().decode(encoded);
        assertEquals(input, decoded);
    }

    @Test
    void encodeAndDecodeBytesRoundTrip() {
        byte[] input = "byte-array-input-字节".getBytes(StandardCharsets.UTF_8);

        byte[] encoded = AESTools.getInstance().encode(input);
        assertNotNull(encoded);

        byte[] decoded = AESTools.getInstance().decode(encoded);
        assertArrayEquals(input, decoded);
    }

    @Test
    void securityCryptoConfigurationInitializesTools() throws Exception {
        AES aes = new SecurityCryptoConfiguration().aes(new CodingApiSecurityProperties());
        assertNotNull(aes);

        // 配置类初始化后 AESTools 可正常完成加解密往返
        String encoded = AESTools.getInstance().encode("config-init");
        assertEquals("config-init", AESTools.getInstance().decode(encoded));
    }

}
