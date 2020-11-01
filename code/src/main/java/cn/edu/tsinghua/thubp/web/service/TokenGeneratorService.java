package cn.edu.tsinghua.thubp.web.service;

import cn.edu.tsinghua.thubp.common.util.MathUtil;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 用于生成一个随机 token 字符串的服务.
 * 不负责查重.
 * @author Rhacoal
 */
@Service
public class TokenGeneratorService {
    private static final char[] alphabet = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private final Random random = new Random();

    public static final int MIN_TOKEN_LENGTH = 6;
    public static final int MAX_TOKEN_LENGTH = 64;

    /**
     * 生成一个指定长度的 token.
     * 长度会被限制在 {@link #MIN_TOKEN_LENGTH} 和 {@link #MAX_TOKEN_LENGTH} 之间.
     * @param tokenLength token 长度
     * @return 生成的 token.
     */
    public String generateToken(int tokenLength) {
        tokenLength = MathUtil.limitInRange(tokenLength, MIN_TOKEN_LENGTH, MAX_TOKEN_LENGTH);
        char[] array = new char[tokenLength];
        for (int i = 0; i < tokenLength; ++i) {
            array[i] = alphabet[random.nextInt(alphabet.length)];
        }
        return new String(array);
    }
}
