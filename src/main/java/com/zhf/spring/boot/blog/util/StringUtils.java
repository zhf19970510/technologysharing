package com.zhf.spring.boot.blog.util;

import java.util.UUID;

/**
 * @author ZengHongFa
 * @create 2020/3/10 0010 13:35
 */
public class StringUtils {
    public static String randomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "").toUpperCase();
    }

}
