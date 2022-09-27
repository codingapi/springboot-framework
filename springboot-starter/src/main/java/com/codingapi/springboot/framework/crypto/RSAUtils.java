package com.codingapi.springboot.framework.crypto;

import lombok.SneakyThrows;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

public class RSAUtils {

    /**
     * base64 privateKey
     */
    public final static String privateKey ="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCmTVOwBrazof7jtI8TeS0iv4rExgQZd5xfdVbsG3u6OAjBfPSLuWbFmviYRIq2lboE8URncNi3Tp2TuqbciUOX44PGyTZPOEOYpEzkL2D9lRhUh2AOcZhfSRSPO6FPvoVRtfdIp9OA71OQxjnzqZxWjls+XKpA2+E1qf0dcSXfSRYMuZFxnEGXQKoLMUmysB0QZIHaBd+TqNjY12NYtkLBokrhCgYEsW0tXHaZxBkut86rFXMhaWWmHSkSnZdllyFMO8PKN1DVpFNT9UFmLGK2US7m728o4pKdQDhn72BiN/d3BsiXWofdBH3Ihx7DMk/WuI9l4Ao+hL431IOIUFAHAgMBAAECggEARJJX8poiFZmzbxLReBccHFrcjlyT7ihayyWoDL0cXGGkgpvSfhaZoNtQrAB/LeA5DrapHPnz8kmxQevRx9e1jliayonHIg0yGiuNJP3AQW+L07bqTapbSNbqalENJ8OIV3Pvnf7NgDmuvGBwHt+N/ka+qs0syoefqjAIlW5FTna6U90n1PTRoeHOVmJlT/10UHiN1202tAaw9ief6PJb/B7lW6M0O84i/ziR1Ua53AUtWL9gGpFCQqE1kQHolVD89W27sLpcvhS2ZkdY7w1a97Z/4Eh/RnhxWpKg5NKLQRBVut/zwGSK6wXoUi145Kybovy8E2lZBWnAykrl/XfVcQKBgQDk7qvyZQSey94ahiZOP44qjMtjlgZZO9UejoOtI99xG31dYGC9NAjuimGXZKbbBXXXLnCbNgsH4bzjh9dEK8EN9rTlEBePUk2DVCPfuX57KmecHM3sv6GLq3LQ0rCVH5EUGdr5Y1Vj+B7ZmLed91mBGHL0438wI7tlvhVgt30HPwKBgQC59vYaCC5XxJQ1bP4QOKVee7VAVQpQUsr+Srz/pfpACcIgABLFp9VwO9GRO7ir+rbnLxGb7MH9hK6O3T7oFFqf+O/HB5jcCzsbkZtLuvZlE/vq9e6yvqtBEFNrqWj4CREiaFMIaO0hs04tXGdjv9lMGWl8/R8OFmcHU2377ISNOQKBgQC6xCQvOk6MplFqXir+B0eaWwbLFefUAMYbibTPGcb+Zaje9vO1J7Bpuydm9UxSvp+mj1J6rZpOMdGB5p6uFOwI0k492eT+nexyycACkzgmjy+74pv1G1lVsueWiWxaHEill3pXnrxZNGEmsDqMevDgKuwN8VMgGOaXWH3kS42KzQKBgQCKMoQ7Xj8KTHUTqva3mVETgP12XHw4qv5dsa74kSEC+/1+iG9q4cnVCS85LdeUPtIzxZ/hbzXOsA6E7tgz4gRjA700/GlVhScc2r08rykyfoJk3vTPcrCTvo0v4gq24Q4RstOZ2Vf4BmcfgJ742vah/fSNxRGSB4XzRMXkRKVQyQKBgHrV9e+fZQMxvwrGIJyttNHJek5ehGhOBgaz1YKOr4zzRgviIAL3p0oXeNQuAL9EaNwXX4aWjR6TbfP5CvU3+UsY+QREYdRWPi9xss09Ms8czQ8hH799tmToag66qd9cJFkVNMVChVLQQ8iRd4aJehirzupg5g55jrBzCEJClCvc";
    /**
     * base64 publicKey
     */
    public final static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApk1TsAa2s6H+47SPE3ktIr+KxMYEGXecX3VW7Bt7ujgIwXz0i7lmxZr4mESKtpW6BPFEZ3DYt06dk7qm3IlDl+ODxsk2TzhDmKRM5C9g/ZUYVIdgDnGYX0kUjzuhT76FUbX3SKfTgO9TkMY586mcVo5bPlyqQNvhNan9HXEl30kWDLmRcZxBl0CqCzFJsrAdEGSB2gXfk6jY2NdjWLZCwaJK4QoGBLFtLVx2mcQZLrfOqxVzIWllph0pEp2XZZchTDvDyjdQ1aRTU/VBZixitlEu5u9vKOKSnUA4Z+9gYjf3dwbIl1qH3QR9yIcewzJP1riPZeAKPoS+N9SDiFBQBwIDAQAB";

    private final static RSAUtils instance = new RSAUtils();

    private final RSA rsa;

    @SneakyThrows
    private RSAUtils() {
        this.rsa = new RSA(Base64Utils.decodeFromString(privateKey),Base64Utils.decodeFromString(publicKey));
    }

    public static RSAUtils getInstance() {
        return instance;
    }

    public String encode(String input) throws Exception {
        return Base64Utils.encodeToString(rsa.encrypt(input.getBytes(StandardCharsets.UTF_8)));
    }

    public String decode(String input) throws Exception {
        return new String(rsa.decrypt(Base64Utils.decodeFromString(input)),StandardCharsets.UTF_8);
    }

    public byte[] encode(byte[] input) throws Exception {
        return rsa.encrypt(input);
    }

    public byte[] decode(byte[] input) throws Exception {
        return rsa.decrypt(input);
    }

}
