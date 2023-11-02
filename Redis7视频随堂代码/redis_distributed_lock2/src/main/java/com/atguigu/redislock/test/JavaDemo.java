package com.atguigu.redislock.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @auther zzyy
 * @create 2023-01-30 10:37
 */
public class JavaDemo
{
    public static void main(String[] args)
    {
        new HashSet<>().add("a");

        new ArrayList().add("a");
        new String();

        //redis结构

        /** redis6相关的底层模型和结构
         * String = SDS
         * Set = intset + hashtable
         * ZSet = skipList + zipList
         * List = quicklist + zipList
         * Hash = hashtable + zipList
         *
         * ===========================================
         *
         * redis7相关的底层模型和结构
         * String = SDS
         * Set = intset + hashtable
         * ZSet = skipList + listpack紧凑列表
         * List = quicklist
         * Hash = hashtable + listpack紧凑列表
         */
        new HashMap<>().put(1,"abc");

        new LinkedList<>().add(1);

        /**
         *
         * java List
         * 1 ArrayList  ===> Object[]
         * 2 LinkedList ===> 放入node节点的一个双端链表
         *
         * redis list
         *  都是双端链表结构，借鉴java的思想，redis也给用户新建了一个全新的数据结构，俗称
         * 1 redis6   ===》
         * 2 redis7   ===》   quicklist
         *
         * 总纲
         *
         * 分
         */

    }
}
