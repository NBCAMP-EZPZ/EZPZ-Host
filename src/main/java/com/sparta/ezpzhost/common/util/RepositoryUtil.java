package com.sparta.ezpzhost.common.util;

public final class RepositoryUtil {

    public static long getTotal(Long totalCount) {
        return (totalCount == null) ? 0L : totalCount;
    }

}
