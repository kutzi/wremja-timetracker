package com.kemai.util;

import java.util.Collection;

public final class CollectionUtils {
    private CollectionUtils() {}
    
    public static boolean isEmpty(Collection<?> col) {
        return (col == null || col.isEmpty());
    }
    
    public static boolean isNotEmpty(Collection<?> col) {
        return !isEmpty(col);
    }
}
