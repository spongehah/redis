package com.atguigu.redis7.iomultiplex.bio.accept;

import java.io.IOException;
import java.net.Socket;

/**
 * @auther zzyy
 * @create 2021-06-01 10:32
 */
public class RedisClient02
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("------RedisClient02 start");
        Socket socket = new Socket("127.0.0.1", 6379);
        System.out.println("------RedisClient02 connection over");
    }
}
