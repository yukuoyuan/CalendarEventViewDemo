package cn.yky.calendarevenview.utils;

import java.util.List;

/**
 * Created by yukoyuan on 16/7/8.
 * 这是一个集合的工具类
 */
public class ListUtils {
    /**
     * 这是一个判断集合是否为空的方法
     *
     * @param sourceList 穿进去一个集合
     * @return 是否是空的
     */
    public static <V> boolean isEmpty(List<V> sourceList) {
        return (sourceList == null || sourceList.size() == 0);
    }
}
