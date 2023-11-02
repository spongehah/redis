# 	1、Redis安装、配置和启动

> 参考视频：B站尚硅谷周阳Redis7.0
>
> 只包含Redis基础、事务持久化RDB和AOF、主从、哨兵、集群部分
>
> **推荐参考新版Redis笔记**，即其它Redis笔记



在官网下载tar.gz包后，拷贝到/opt中，进行解压，然后进入解压出的redis文件中，执行命令make && make install,默认安装到/usr/local/bin目录下

在根目录下创建一个目录/myredis，并将/opt/redis-7.0.10/redis.conf文件复制到myredis目录下，保留原文件

配置文件的修改：

​	1.将daemonize no 改为 daemonize yes

​	2.protected-mode yes 改为protected-mode no

​	3.注释掉bind 127.0.0.1 -::1

​	4.requirepass设置密码

主从复制的配置：

![image-20230408143127271](image/Redis笔记旧版.assets/image-20230408143127271.webp)

redis可使用config get xxx 来查看对应的配置



redis卸载：rm -rf /usr/local/bin/redis-*

启动：

​	redis-server /myredis/redis7.conf

​	redis-cli -a 密码 [-p 6379] [--raw]

​			redis-cli  --> auth 密码

​	可使用ps -ef | grep redis | grep -v grep 查看进程

​	若输入ping，输出PONG，则启动成功

退出cli：quit

退出cli和server：

​	在redis命令行中：shutdown

​	不在redis命令行中（远程关闭）：单实例关闭：redis -cli -a 密码 shutdown

​																多实例关闭（分别指定端口号）：redis-cli -p 6379 shutdown

# 2、Redis十大数据类型

## 2.1 总体概述

<img src="image/Redis笔记旧版.assets/image-20230403225207494.webp" alt="image-20230403225207494" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230403225251521.webp" alt="image-20230403225251521" style="zoom:50%;" />

![image-20230403225449055](image/Redis笔记旧版.assets/image-20230403225449055.webp)

![image-20230403225621007](image/Redis笔记旧版.assets/image-20230403225621007.webp)

![image-20230403225657133](image/Redis笔记旧版.assets/image-20230403225657133.webp)

![image-20230403225720684](image/Redis笔记旧版.assets/image-20230403225720684.webp)

![image-20230403225805567](image/Redis笔记旧版.assets/image-20230403225805567.webp)

![image-20230403231640906](image/Redis笔记旧版.assets/image-20230403231640906.webp)

![image-20230403231714477](image/Redis笔记旧版.assets/image-20230403231714477.webp)

![image-20230403231900839](image/Redis笔记旧版.assets/image-20230403231900839.webp)

![image-20230403231949615](image/Redis笔记旧版.assets/image-20230403231949615.webp)

![image-20230403232046006](image/Redis笔记旧版.assets/image-20230403232046006.webp)

## 2.2 获取redis常见数据类型操作命令

官网英文：[Commands | Redis](https://redis.io/commands/)

中文：[Redis命令中心（Redis commands） -- Redis中国用户组（CRUG）](http://redis.cn/commands.html)

## 2.3 key操作命令

![image-20230404144959263](image/Redis笔记旧版.assets/image-20230404144959263.webp)

![image-20230404145353394](image/Redis笔记旧版.assets/image-20230404145353394.webp)

**获取对应数据类型的命令：help @数据类型**

**redis可使用config get xxx 来查看对应的配置**

**命令不区分大小写，但是key使区分的**

## 2.4.1 String

![image-20230404164432416](image/Redis笔记旧版.assets/image-20230404164432416.webp)

![image-20230404150507417](image/Redis笔记旧版.assets/image-20230404150507417.webp)

![image-20230404151626311](image/Redis笔记旧版.assets/image-20230404151626311.webp)

**若msetnx k1 v1 k2 v2：k1已经存在，k4不存在，则不会执行成功，全部都失败**



![image-20230404151754634](image/Redis笔记旧版.assets/image-20230404151754634.webp)

<img src="image/Redis笔记旧版.assets/image-20230404152137413.webp" alt="image-20230404152137413" style="zoom:50%;" />

![image-20230404152335659](image/Redis笔记旧版.assets/image-20230404152335659.webp)

<img src="image/Redis笔记旧版.assets/image-20230404152346946.webp" alt="image-20230404152346946" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404152551652.webp" alt="image-20230404152551652" style="zoom: 67%;" />

![image-20230404152759900](image/Redis笔记旧版.assets/image-20230404152759900.webp)

<img src="image/Redis笔记旧版.assets/image-20230404152901807.webp" alt="image-20230404152901807" style="zoom:50%;" />

![image-20230404153014235](image/Redis笔记旧版.assets/image-20230404153014235.webp)

<img src="image/Redis笔记旧版.assets/image-20230404153237570.webp" alt="image-20230404153237570" style="zoom:33%;" />

**setnx == set nx的合并**

**setex == set + expire 的合并 == set ex**

![image-20230404153757944](image/Redis笔记旧版.assets/image-20230404153757944.webp)

<img src="image/Redis笔记旧版.assets/image-20230404153827366.webp" alt="image-20230404153827366" style="zoom:50%;" />

**getset k v == set k v get**





**string应用场景：点赞，访问次数**



## 2.4.2 List

![image-20230404155852728](image/Redis笔记旧版.assets/image-20230404155852728.webp)

![image-20230404155935050](image/Redis笔记旧版.assets/image-20230404155935050.webp)

![image-20230404160240517](image/Redis笔记旧版.assets/image-20230404160240517.webp)

<img src="image/Redis笔记旧版.assets/image-20230404160220307.webp" alt="image-20230404160220307" style="zoom:50%;" />

**lrange list1 0 -1 相当于遍历**

<img src="image/Redis笔记旧版.assets/image-20230404160446068.webp" alt="image-20230404160446068" style="zoom:50%;" />

![image-20230404160507965](image/Redis笔记旧版.assets/image-20230404160507965.webp)

<img src="image/Redis笔记旧版.assets/image-20230404160754814.webp" alt="image-20230404160754814" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404160808059.webp" alt="image-20230404160808059" style="zoom:50%;" />

![image-20230404160825227](image/Redis笔记旧版.assets/image-20230404160825227.webp)

<img src="image/Redis笔记旧版.assets/image-20230404161120028.webp" alt="image-20230404161120028" style="zoom: 50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404161301771.webp" alt="image-20230404161301771" style="zoom:50%;" />

![image-20230404161627474](image/Redis笔记旧版.assets/image-20230404161627474.webp)

<img src="image/Redis笔记旧版.assets/image-20230404161641455.webp" alt="image-20230404161641455" style="zoom:33%;" />

<img src="image/Redis笔记旧版.assets/image-20230404161732069.webp" alt="image-20230404161732069" style="zoom:33%;" />

![image-20230404161745126](image/Redis笔记旧版.assets/image-20230404161745126.webp)

<img src="image/Redis笔记旧版.assets/image-20230404161818052.webp" alt="image-20230404161818052" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404161917970.webp" alt="image-20230404161917970" style="zoom:50%;" />

**应用场景：微信公众号多关注订阅的文章**



## 2.4.3 Hash

![image-20230404163215501](image/Redis笔记旧版.assets/image-20230404163215501.webp)

![image-20230404163255796](image/Redis笔记旧版.assets/image-20230404163255796.webp)

<img src="image/Redis笔记旧版.assets/image-20230404163657688.webp" alt="image-20230404163657688" style="zoom:50%;" />

**现版本hset == hmset，hmset已经被弃用**

<img src="image/Redis笔记旧版.assets/image-20230404163752325.webp" alt="image-20230404163752325" style="zoom:50%;" />

**hgetall相当于遍历**

![image-20230404164031407](image/Redis笔记旧版.assets/image-20230404164031407.webp)

<img src="image/Redis笔记旧版.assets/image-20230404164110523.webp" alt="image-20230404164110523" style="zoom:50%;" />

![image-20230404164122328](image/Redis笔记旧版.assets/image-20230404164122328.webp)

<img src="image/Redis笔记旧版.assets/image-20230404164307732.webp" alt="image-20230404164307732" style="zoom:50%;" />

![image-20230404164316473](image/Redis笔记旧版.assets/image-20230404164316473.webp)

<img src="image/Redis笔记旧版.assets/image-20230404164500816.webp" alt="image-20230404164500816" style="zoom:50%;" />

![image-20230404164513482](image/Redis笔记旧版.assets/image-20230404164513482.webp)

<img src="image/Redis笔记旧版.assets/image-20230404164646512.webp" alt="image-20230404164646512" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404164705975.webp" alt="image-20230404164705975" style="zoom:50%;" />

![image-20230404165057936](image/Redis笔记旧版.assets/image-20230404165057936.webp)

<img src="image/Redis笔记旧版.assets/image-20230404165134941.webp" alt="image-20230404165134941" style="zoom:50%;" />

![image-20230404170156044](image/Redis笔记旧版.assets/image-20230404170156044.webp)

<img src="image/Redis笔记旧版.assets/image-20230404170224054.webp" alt="image-20230404170224054" style="zoom: 33%;" />

<img src="image/Redis笔记旧版.assets/image-20230404170246406.webp" alt="image-20230404170246406" style="zoom:33%;" />

## 2.4.4 Set（无序且不重复）

**![](image/Redis笔记旧版.assets/image-20230404171056476.webp)*

![image-20230404171209171](image/Redis笔记旧版.assets/image-20230404171209171.webp)

<img src="image/Redis笔记旧版.assets/image-20230404171217306.webp" alt="image-20230404171217306" style="zoom:50%;" />

**smembers key 相当于遍历**

![image-20230404171306420](image/Redis笔记旧版.assets/image-20230404171306420.webp)

<img src="image/Redis笔记旧版.assets/image-20230404171256573.webp" alt="image-20230404171256573" style="zoom:50%;" />

![image-20230404171510161](image/Redis笔记旧版.assets/image-20230404171510161.webp)

<img src="image/Redis笔记旧版.assets/image-20230404171736588.webp" alt="image-20230404171736588" style="zoom:50%;" />

![image-20230404171844719](image/Redis笔记旧版.assets/image-20230404171844719.webp)

<img src="image/Redis笔记旧版.assets/image-20230404171956252.webp" alt="image-20230404171956252" style="zoom:33%;" />

<img src="image/Redis笔记旧版.assets/image-20230404172014454.webp" alt="image-20230404172014454" style="zoom:33%;" />

![image-20230404172023254](image/Redis笔记旧版.assets/image-20230404172023254.webp)

<img src="image/Redis笔记旧版.assets/image-20230404172121348.webp" alt="image-20230404172121348" style="zoom:50%;" />

**![image-20230404172246329](image/Redis笔记旧版.assets/image-20230404172246329.webp)*

![image-20230404172344442](image/Redis笔记旧版.assets/image-20230404172344442.webp)

<img src="image/Redis笔记旧版.assets/image-20230404172351483.webp" alt="image-20230404172351483" style="zoom: 50%;" />

![image-20230404172404918](image/Redis笔记旧版.assets/image-20230404172404918.webp)

<img src="image/Redis笔记旧版.assets/image-20230404172417627.webp" alt="image-20230404172417627" style="zoom:50%;" />

![image-20230404172428866](image/Redis笔记旧版.assets/image-20230404172428866.webp)

![image-20230404172620915](image/Redis笔记旧版.assets/image-20230404172620915.webp)

<img src="image/Redis笔记旧版.assets/image-20230404172756269.webp" alt="image-20230404172756269" style="zoom:50%;" />

![image-20230404173028170](image/Redis笔记旧版.assets/image-20230404173028170.webp)

<img src="image/Redis笔记旧版.assets/image-20230404173040632.webp" alt="image-20230404173040632" style="zoom: 25%;" />

<img src="image/Redis笔记旧版.assets/image-20230404173548221.webp" alt="image-20230404173548221" style="zoom:25%;" />

<img src="image/Redis笔记旧版.assets/image-20230404173644203.webp" alt="image-20230404173644203" style="zoom:33%;" />

## 2.4.5 Zset(sorted set)

![image-20230404174827874](image/Redis笔记旧版.assets/image-20230404174827874.webp)

![image-20230404182213058](image/Redis笔记旧版.assets/image-20230404182213058.webp)



![image-20230404175144751](image/Redis笔记旧版.assets/image-20230404175144751.webp)

<img src="image/Redis笔记旧版.assets/image-20230404175009934.webp" alt="image-20230404175009934" style="zoom: 33%;" />

<img src="image/Redis笔记旧版.assets/image-20230404175122275.webp" alt="image-20230404175122275" style="zoom: 50%;" />

![image-20230404175321184](image/Redis笔记旧版.assets/image-20230404175321184.webp)

<img src="image/Redis笔记旧版.assets/image-20230404175334432.webp" alt="image-20230404175334432" style="zoom:33%;" />

<img src="image/Redis笔记旧版.assets/image-20230404175749802.webp" alt="image-20230404175749802" style="zoom: 33%;" />

![image-20230404180040229](image/Redis笔记旧版.assets/image-20230404180040229.webp)

<img src="image/Redis笔记旧版.assets/image-20230404180050914.webp" alt="image-20230404180050914" style="zoom:50%;" />

![image-20230404180059802](image/Redis笔记旧版.assets/image-20230404180059802.webp)

<img src="image/Redis笔记旧版.assets/image-20230404180154881.webp" alt="image-20230404180154881" style="zoom:50%;" />

![image-20230404180204980](image/Redis笔记旧版.assets/image-20230404180204980.webp)

<img src="image/Redis笔记旧版.assets/image-20230404180252285.webp" alt="image-20230404180252285" style="zoom:50%;" />

![image-20230404180335624](image/Redis笔记旧版.assets/image-20230404180335624.webp)

<img src="image/Redis笔记旧版.assets/image-20230404180441994.webp" alt="image-20230404180441994" style="zoom:33%;" />

![image-20230404180530928](image/Redis笔记旧版.assets/image-20230404180530928.webp)

<img src="image/Redis笔记旧版.assets/image-20230404180542323.webp" alt="image-20230404180542323" style="zoom:50%;" />

![image-20230404180602183](image/Redis笔记旧版.assets/image-20230404180602183.webp)

<img src="image/Redis笔记旧版.assets/image-20230404180840261.webp" alt="image-20230404180840261" style="zoom:33%;" />

![image-20230404180917807](image/Redis笔记旧版.assets/image-20230404180917807.webp)

<img src="image/Redis笔记旧版.assets/image-20230404181107403.webp" alt="image-20230404181107403" style="zoom: 50%;" />

![image-20230404181333071](image/Redis笔记旧版.assets/image-20230404181333071.webp)

<img src="image/Redis笔记旧版.assets/image-20230404181315571.webp" alt="image-20230404181315571" style="zoom: 25%;" />

## 2.4.6 bitmap（位图）--string

![image-20230404193548268](image/Redis笔记旧版.assets/image-20230404193548268.webp)

**bitmap 相当于string的子类，type bit 的返回值为string**

<img src="image/Redis笔记旧版.assets/image-20230404193729538.webp" alt="image-20230404193729538" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404193706412.webp" alt="image-20230404193706412" style="zoom:50%;" />

![image-20230404193815527](image/Redis笔记旧版.assets/image-20230404193815527.webp)

<img src="image/Redis笔记旧版.assets/image-20230404193918746.webp" alt="image-20230404193918746" style="zoom: 33%;" />

![image-20230404194130392](image/Redis笔记旧版.assets/image-20230404194130392.webp)

<img src="image/Redis笔记旧版.assets/image-20230404194243814.webp" alt="image-20230404194243814" style="zoom:50%;" />

![image-20230404194221167](image/Redis笔记旧版.assets/image-20230404194221167.webp)

<img src="image/Redis笔记旧版.assets/image-20230404194317163.webp" alt="image-20230404194317163" style="zoom: 33%;" />

**每八位一组，一组是一个字节，strlen返回的是字节数**

![image-20230404194520508](image/Redis笔记旧版.assets/image-20230404194520508.webp)

<img src="image/Redis笔记旧版.assets/image-20230404194542795.webp" alt="image-20230404194542795" style="zoom: 50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404194808845.webp" alt="image-20230404194808845" style="zoom: 80%;" />

![image-20230404195616077](image/Redis笔记旧版.assets/image-20230404195616077.webp)

![image-20230404195544962](image/Redis笔记旧版.assets/image-20230404195544962.webp)

<img src="image/Redis笔记旧版.assets/image-20230404195416531.webp" alt="image-20230404195416531" style="zoom: 33%;" />

![image-20230404195952350](image/Redis笔记旧版.assets/image-20230404195952350.webp)

**签到天数**

<img src="image/Redis笔记旧版.assets/image-20230404200137404.webp" alt="image-20230404200137404" style="zoom:33%;" />



## 2.4.7 HyperLogLog（基数统计）--string

<img src="image/Redis笔记旧版.assets/image-20230404202309445.webp" alt="image-20230404202309445" style="zoom: 50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404201722609.webp" alt="image-20230404201722609" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404201729369.webp" alt="image-20230404201729369" style="zoom: 50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404202514808.webp" alt="image-20230404202514808" style="zoom: 33%;" />

<img src="image/Redis笔记旧版.assets/image-20230404202707642.webp" alt="image-20230404202707642" style="zoom: 67%;" />

<img src="image/Redis笔记旧版.assets/image-20230404202750880.webp" alt="image-20230404202750880" style="zoom:50%;" />

![image-20230404202919105](image/Redis笔记旧版.assets/image-20230404202919105.webp)

## 2.4.8 GEO（地理空间）--zset

![image-20230404204809187](image/Redis笔记旧版.assets/image-20230404204809187.webp)

![image-20230404205224307](image/Redis笔记旧版.assets/image-20230404205224307.webp)

<img src="image/Redis笔记旧版.assets/image-20230404205234321.webp" alt="image-20230404205234321" style="zoom: 33%;" />

![image-20230404205529983](image/Redis笔记旧版.assets/image-20230404205529983.webp)

<img src="image/Redis笔记旧版.assets/image-20230404205633619.webp" alt="image-20230404205633619"  />

![image-20230404205732280](image/Redis笔记旧版.assets/image-20230404205732280.webp)

![image-20230404210047316](image/Redis笔记旧版.assets/image-20230404210047316.webp)

![image-20230404211347133](image/Redis笔记旧版.assets/image-20230404211347133.webp)

![image-20230404211518786](image/Redis笔记旧版.assets/image-20230404211518786.webp)

## 2.4.9 Stream（redis流）用于MQ

<img src="image/Redis笔记旧版.assets/image-20230404213312159.webp" alt="image-20230404213312159" style="zoom: 50%;" />

![image-20230404213756806](image/Redis笔记旧版.assets/image-20230404213756806.webp)

<img src="image/Redis笔记旧版.assets/image-20230404213817188.webp" alt="image-20230404213817188" style="zoom: 33%;" />

<img src="image/Redis笔记旧版.assets/image-20230404213852193.webp" alt="image-20230404213852193" style="zoom: 33%;" />

![image-20230404214147552](image/Redis笔记旧版.assets/image-20230404214147552.webp)

<img src="image/Redis笔记旧版.assets/image-20230404214230187.webp" alt="image-20230404214230187" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404214641212.webp" alt="image-20230404214641212" style="zoom:50%;" />

### 生产相关命令

<img src="image/Redis笔记旧版.assets/image-20230404214802556.webp" alt="image-20230404214802556" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230404221030749.webp" alt="image-20230404221030749" style="zoom: 50%;" />

![image-20230405133621236](image/Redis笔记旧版.assets/image-20230405133621236.webp)

<img src="image/Redis笔记旧版.assets/image-20230405133851521.webp" alt="image-20230405133851521" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230405134309238.webp" alt="image-20230405134309238" style="zoom:50%;" />

![image-20230405134411435](image/Redis笔记旧版.assets/image-20230405134411435.webp)

<img src="image/Redis笔记旧版.assets/image-20230405134539497.webp" alt="image-20230405134539497" style="zoom:33%;" />

![image-20230405134615310](image/Redis笔记旧版.assets/image-20230405134615310.webp)

<img src="image/Redis笔记旧版.assets/image-20230405134629566.webp" alt="image-20230405134629566" style="zoom:33%;" />

![image-20230405134736417](image/Redis笔记旧版.assets/image-20230405134736417.webp)

<img src="image/Redis笔记旧版.assets/image-20230405134800206.webp" alt="image-20230405134800206" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230405134813680.webp" alt="image-20230405134813680" style="zoom: 80%;" />

![image-20230405134836892](image/Redis笔记旧版.assets/image-20230405134836892.webp)

<img src="image/Redis笔记旧版.assets/image-20230405135127611.webp" alt="image-20230405135127611" style="zoom: 33%;" />

<img src="image/Redis笔记旧版.assets/image-20230405135019850.webp" alt="image-20230405135019850" style="zoom: 33%;" />

![image-20230405135150439](image/Redis笔记旧版.assets/image-20230405135150439.webp)

<img src="image/Redis笔记旧版.assets/image-20230405135516758.webp" alt="image-20230405135516758" style="zoom: 33%;" />

<img src="image/Redis笔记旧版.assets/image-20230405135613818.webp" alt="image-20230405135613818" style="zoom:33%;" />

<img src="image/Redis笔记旧版.assets/image-20230405135813755.webp" alt="image-20230405135813755" style="zoom:33%;" />



### 消费相关命令

![image-20230405140112329](image/Redis笔记旧版.assets/image-20230405140112329.webp)

![image-20230405140209314](image/Redis笔记旧版.assets/image-20230405140209314.webp)

<img src="image/Redis笔记旧版.assets/image-20230405140238202.webp" alt="image-20230405140238202" style="zoom:33%;" />

![image-20230405140353249](image/Redis笔记旧版.assets/image-20230405140353249.webp)

<img src="image/Redis笔记旧版.assets/image-20230405140736329.webp" alt="image-20230405140736329" style="zoom:33%;" />

<img src="image/Redis笔记旧版.assets/image-20230405140922357.webp" alt="image-20230405140922357" style="zoom: 33%;" />

![image-20230405141112822](image/Redis笔记旧版.assets/image-20230405141112822.webp)

![image-20230405141506009](image/Redis笔记旧版.assets/image-20230405141506009.webp)

<img src="image/Redis笔记旧版.assets/image-20230405141551701.webp" alt="image-20230405141551701" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230405142023606.webp" alt="image-20230405142023606" style="zoom:50%;" />

![image-20230405141818015](image/Redis笔记旧版.assets/image-20230405141818015.webp)

<img src="image/Redis笔记旧版.assets/image-20230405142000116.webp" alt="image-20230405142000116" style="zoom:50%;" />

![image-20230405142050646](image/Redis笔记旧版.assets/image-20230405142050646.webp)

<img src="image/Redis笔记旧版.assets/image-20230405142109446.webp" alt="image-20230405142109446" style="zoom:50%;" />

![image-20230405142211049](image/Redis笔记旧版.assets/image-20230405142211049.webp)



## 2.4.10 bitfield（位域）

<img src="image/Redis笔记旧版.assets/image-20230405144707422.webp" alt="image-20230405144707422" style="zoom:50%;" />

![image-20230405144851639](image/Redis笔记旧版.assets/image-20230405144851639.webp)

![image-20230405145226605](image/Redis笔记旧版.assets/image-20230405145226605.webp)

![image-20230405145236375](image/Redis笔记旧版.assets/image-20230405145236375.webp)

<img src="image/Redis笔记旧版.assets/image-20230405145600229.webp" alt="image-20230405145600229" style="zoom: 33%;" />

<img src="image/Redis笔记旧版.assets/image-20230405153436277.webp" alt="image-20230405153436277" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230405153853768.webp" alt="image-20230405153853768" style="zoom:50%;" />

![image-20230405154832889](image/Redis笔记旧版.assets/image-20230405154832889.webp)

<img src="image/Redis笔记旧版.assets/image-20230405155213731.webp" alt="image-20230405155213731" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230405161058796.webp" alt="image-20230405161058796" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230405161127243.webp" alt="image-20230405161127243" style="zoom: 50%;" />



# 3、Redis持久化

![image-20230405161743958](image/Redis笔记旧版.assets/image-20230405161743958.webp)

![image-20230405161628794](image/Redis笔记旧版.assets/image-20230405161628794.webp)



## ==3.1 RDB（Redis Database）==

### 是什么（含6 7 自动触发区别）

![image-20230405161830350](image/Redis笔记旧版.assets/image-20230405161830350.webp)

<img src="image/Redis笔记旧版.assets/image-20230405161835676.webp" alt="image-20230405161835676" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230405162918429.webp" alt="image-20230405162918429" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230405163104062.webp" alt="image-20230405163104062" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230405163445841.webp" alt="image-20230405163445841" style="zoom:50%;" />





### 自动触发

<img src="image/Redis笔记旧版.assets/image-20230405163627189.webp" alt="image-20230405163627189" style="zoom:50%;" />

**按上图修改配置**

save 5 2（现在已经设置为5秒两次）

dir /myredis/dumpfiles

dbfilename dump6379.rdb

![image-20230405195656003](image/Redis笔记旧版.assets/image-20230405195656003.webp)

![image-20230405195612851](image/Redis笔记旧版.assets/image-20230405195612851.webp)

**执行shutdown命令且没有开启AOF持久化也会生成dump文件**

![image-20230405195924064](image/Redis笔记旧版.assets/image-20230405195924064.webp)





### 手动触发（默认推荐使用DBSAVE，因为不会阻塞redis服务器）

![image-20230405201853548](image/Redis笔记旧版.assets/image-20230405201853548.webp)

**严禁使用save手动触发！！！**

![image-20230405202047053](image/Redis笔记旧版.assets/image-20230405202047053.webp)

![image-20230405202414292](image/Redis笔记旧版.assets/image-20230405202414292.webp)





### 优缺点

![image-20230405203410922](image/Redis笔记旧版.assets/image-20230405203410922.webp)

![image-20230405203520365](image/Redis笔记旧版.assets/image-20230405203520365.webp)

<img src="image/Redis笔记旧版.assets/image-20230405203606972.webp" alt="image-20230405203606972" style="zoom:50%;" />

![image-20230405203827514](image/Redis笔记旧版.assets/image-20230405203827514.webp)





### 修复RDB文件

使用在/usr/local/bin目录下的check-rdb，修复rdb文件

![image-20230405205319503](image/Redis笔记旧版.assets/image-20230405205319503.webp)





### RDB触发情况和快照禁用

![image-20230405205742570](image/Redis笔记旧版.assets/image-20230405205742570.webp)

![image-20230405210011878](image/Redis笔记旧版.assets/image-20230405210011878.webp)

<img src="image/Redis笔记旧版.assets/image-20230405210056378.webp" alt="image-20230405210056378" style="zoom: 50%;" />

**将save后写成空串，但还是可以手动触发生成rdb文件**





### redis数据库RDB和AOF配置，数据库备份的区别：

**一句话：AOF的优先级高于RDB**

**情况1：整个redis默认情况下，redis为不设置save参数且未开启了AOF持久化时，当shutdown时会生成一个有效dump文件，整个过程只能在shutdown时保存一个有效的dump文件，以至于下一次打开redis时可恢复数据**

**情况2：不设置save参数但是开启了AOF持久化时，以AOF为主，当shutdown时也会生成一个无效dump文件，备份数据库由AOF完成，若将AOF文件删除，数据库将无法完成备份**

**情况3：但是若将save 后写成空串，则是禁用所有dump文件的自动生成方式，shutdown时连空文件也不会生成，若开启了AOF，则可由AOF完成备份，若未开启AOF，则是纯缓存模式，则redis无法自动完成备份**

**情况4：设置了save参数以开启自动触发RDB，若未开启AOF，则由RDB独自完成备份，若开启了AOF且开启了混合模式，则由RDB和AOF混合完成备份，生成的AOF文件包括RDB头部和AOF混写，若未开启混合模式，则以AOF为主，AOF优先级高**





### 优化配置项（后四者都推荐默认）

![image-20230405210439775](image/Redis笔记旧版.assets/image-20230405210439775.webp)

<img src="image/Redis笔记旧版.assets/image-20230405210531817.webp" alt="image-20230405210531817" style="zoom:50%;" />

**若为yes，则数据一致性很高（建议默认yes）**



![image-20230405210654149](image/Redis笔记旧版.assets/image-20230405210654149.webp)

**推荐默认yes**



![image-20230405211009684](image/Redis笔记旧版.assets/image-20230405211009684.webp)

**推荐默认yes**



![image-20230405211123785](image/Redis笔记旧版.assets/image-20230405211123785.webp)

**推荐默认no**



### 小总结

<img src="image/Redis笔记旧版.assets/image-20230405211326775.webp" alt="image-20230405211326775" style="zoom:50%;" />





## ==3.2 AOF（Append Only File）==

### 是什么

![image-20230405211935077](image/Redis笔记旧版.assets/image-20230405211935077.webp)

![image-20230405213547778](image/Redis笔记旧版.assets/image-20230405213547778.webp)

**默认为关闭，可通过yes开启**





### AOF持久工作流程和三种写回策略（默认为everysec）

<img src="image/Redis笔记旧版.assets/image-20230405212518162.webp" alt="image-20230405212518162" style="zoom:50%;" />

**先保存在AOF缓存区，等达到一定数量后再写入AOF文件，减少IO次数**

![image-20230405213502389](image/Redis笔记旧版.assets/image-20230405213502389.webp)

![image-20230405213754559](image/Redis笔记旧版.assets/image-20230405213754559.webp)

![image-20230405213843436](image/Redis笔记旧版.assets/image-20230405213843436.webp)

![image-20230405213857279](image/Redis笔记旧版.assets/image-20230405213857279.webp)

<img src="image/Redis笔记旧版.assets/image-20230405213638513.webp" alt="image-20230405213638513" style="zoom:50%;" />

![image-20230405214050241](image/Redis笔记旧版.assets/image-20230405214050241.webp)





### 配置开启（含6 7aof保存路径、保存名称的区别）

![image-20230405215504057](image/Redis笔记旧版.assets/image-20230405215504057.webp)

![image-20230405215214769](image/Redis笔记旧版.assets/image-20230405215214769.webp)

![image-20230405215316036](image/Redis笔记旧版.assets/image-20230405215316036.webp)



++++





![image-20230405215521608](image/Redis笔记旧版.assets/image-20230405215521608.webp)



++++





![image-20230405215545871](image/Redis笔记旧版.assets/image-20230405215545871.webp)

![image-20230405215808682](image/Redis笔记旧版.assets/image-20230405215808682.webp)

![image-20230405220437485](image/Redis笔记旧版.assets/image-20230405220437485.webp)

**redis6：和RDB保存的位置相同**

**==redis7：会在RAB文件保存的位置上加上一个自己设定的appenddirname目录，保存在其中==**



++++



![image-20230405220449448](image/Redis笔记旧版.assets/image-20230405220449448.webp)

![image-20230405220900536](image/Redis笔记旧版.assets/image-20230405220900536.webp)

![image-20230405220601888](image/Redis笔记旧版.assets/image-20230405220601888.webp)

**redis6：只有一个aof文件**

**==redis7：aof文件被拆分为三个==**



<img src="image/Redis笔记旧版.assets/image-20230405220920558.webp" alt="image-20230405220920558" style="zoom:50%;" />





### 正常恢复

![image-20230405221343030](image/Redis笔记旧版.assets/image-20230405221343030.webp)

![image-20230406191559461](image/Redis笔记旧版.assets/image-20230406191559461.webp)

**不看rdb文件，把rdb文件全部删掉，可以证明只用aof文件也可以对redis数据库进行恢复**

**对于写操作，==是aof.incr在默默记录==，对于读操作，不会记录，大小不变**

<img src="image/Redis笔记旧版.assets/image-20230406192008533.webp" alt="image-20230406192008533"  />





### 异常恢复

![image-20230406192332377](image/Redis笔记旧版.assets/image-20230406192332377.webp)

![image-20230406194408184](image/Redis笔记旧版.assets/image-20230406194408184.webp)

**在aof.incr文件中故意写入乱码：**

**![image-20230406194651594](image/Redis笔记旧版.assets/image-20230406194651594.webp)**

**redis服务器启动不了**

**使用/usr/local/bin目录下的==redis-check-aof --fix==修复incr文件**

![image-20230406195245468](image/Redis笔记旧版.assets/image-20230406195245468.webp)





### 优缺点

![image-20230406195543939](image/Redis笔记旧版.assets/image-20230406195543939.webp)

![image-20230406200028100](image/Redis笔记旧版.assets/image-20230406200028100.webp)

![image-20230406195839056](image/Redis笔记旧版.assets/image-20230406195839056.webp)

![image-20230406195926135](image/Redis笔记旧版.assets/image-20230406195926135.webp)





### 重写机制

![image-20230406200319735](image/Redis笔记旧版.assets/image-20230406200319735.webp)

![image-20230406200337939](image/Redis笔记旧版.assets/image-20230406200337939.webp)

**当aof文件达到阈值时，自动触发**

**使用bgrewriteaof手动触发**



![image-20230406200549216](image/Redis笔记旧版.assets/image-20230406200549216.webp)

![image-20230406200450221](image/Redis笔记旧版.assets/image-20230406200450221.webp)





案例：

![image-20230406205204119](image/Redis笔记旧版.assets/image-20230406205204119.webp)

![image-20230406205256049](image/Redis笔记旧版.assets/image-20230406205256049.webp)

![image-20230406205311350](image/Redis笔记旧版.assets/image-20230406205311350.webp)

![image-20230406205340698](image/Redis笔记旧版.assets/image-20230406205340698.webp)





### 小总结

![image-20230406205535287](image/Redis笔记旧版.assets/image-20230406205535287.webp)

![image-20230406205656264](image/Redis笔记旧版.assets/image-20230406205656264.webp)





## 3.3 RDB-AOF混合持久化

![image-20230406210824682](image/Redis笔记旧版.assets/image-20230406210824682.webp)

![image-20230406210206899](image/Redis笔记旧版.assets/image-20230406210206899.webp)

![image-20230406210125216](image/Redis笔记旧版.assets/image-20230406210125216.webp)

**可以共存，但AOF优先级高于RDB**

![image-20230406210220048](image/Redis笔记旧版.assets/image-20230406210220048.webp)

![image-20230406210429725](image/Redis笔记旧版.assets/image-20230406210429725.webp)

![image-20230406210504894](image/Redis笔记旧版.assets/image-20230406210504894.webp)

![image-20230406210608955](image/Redis笔记旧版.assets/image-20230406210608955.webp)



![image-20230406210617243](image/Redis笔记旧版.assets/image-20230406210617243.webp)

![image-20230406210925750](image/Redis笔记旧版.assets/image-20230406210925750.webp)

![image-20230406210834433](image/Redis笔记旧版.assets/image-20230406210834433.webp)

### redis数据库RDB和AOF配置，数据库备份的区别：

==**一句话：AOF的优先级高于RDB==**

**情况1：整个redis默认情况下，redis为不设置save参数且未开启了AOF持久化时，当shutdown时会生成一个有效dump文件，整个过程只能在shutdown时保存一个有效的dump文件，以至于下一次打开redis时可恢复数据**

**情况2：不设置save参数但是开启了AOF持久化时，以AOF为主，当shutdown时也会生成一个无效dump文件，备份数据库由AOF完成，若将AOF文件删除，数据库将无法完成备份**

**情况3：但是若将save 后写成空串，则是禁用所有dump文件的自动生成方式，shutdown时连空文件也不会生成，若开启了AOF，则可由AOF完成备份，若未开启AOF，则是纯缓存模式，则redis无法自动完成备份**

**情况4：设置了save参数以开启自动触发RDB，若未开启AOF，则由RDB独自完成备份，若开启了AOF且开启了混合模式，则由RDB和AOF混合完成备份，生成的AOF文件包括RDB头部和AOF混写，若未开启混合模式，则以AOF为主，AOF优先级高**





## 3.4 纯缓存模式

![image-20230406211459344](image/Redis笔记旧版.assets/image-20230406211459344.webp)

![image-20230406211639486](image/Redis笔记旧版.assets/image-20230406211639486.webp)

**不影响手动备份！！！！！！！！！！！！！！！！**





# ==4、Redis事务==

![image-20230406213545815](image/Redis笔记旧版.assets/image-20230406213545815.webp)





## 4.1 Redis事务是什么，与数据库事务的对比	

**==数据库事务：在一次跟数据库的连接会话当中，所有执行的SQL，要么一起成功，要么一起失败==**

**==原子性：一个事务中的所有操作要么全部成功，要么全部失败回滚，不能只执行其中的一部分操作==**

![image-20230406214148888](image/Redis笔记旧版.assets/image-20230406214148888.webp)

![image-20230406214330200](image/Redis笔记旧版.assets/image-20230406214330200.webp)

![image-20230406214351875](image/Redis笔记旧版.assets/image-20230406214351875.webp)

**Redis的事务只是能够==保证一组命令能够连续独占的执行，不会被其他命令插入，也不会被阻塞==，事务提交前任何命令都==不会被实际执行，即不涉及数据库事务的回滚操作，所以没有隔离级别，不保证原子性==(即不保证同时成功或同时失败，冤头债主)，只决定是否开始执行一组的全部指令**





## 4.2 怎么使用Redis事务

![image-20230406215728790](image/Redis笔记旧版.assets/image-20230406215728790.webp)





### 常用命令

![image-20230406215751030](image/Redis笔记旧版.assets/image-20230406215751030.webp)

### 正常执行

![image-20230406215854707](image/Redis笔记旧版.assets/image-20230406215854707.webp)

<img src="image/Redis笔记旧版.assets/image-20230406220100279.webp" alt="image-20230406220100279" style="zoom:50%;" />

### 放弃执行

![image-20230406220134672](image/Redis笔记旧版.assets/image-20230406220134672.webp)

<img src="image/Redis笔记旧版.assets/image-20230406220206888.webp" alt="image-20230406220206888" style="zoom:50%;" />

### 全体连坐（语法编译错误，该组命令全部被舍弃）

![image-20230406220552344](image/Redis笔记旧版.assets/image-20230406220552344.webp)

![image-20230406220526376](image/Redis笔记旧版.assets/image-20230406220526376.webp)

### 冤头债主（语法没错，编译时没检查出错误，对的命令执行，不对的不执行）

![image-20230406221001476](image/Redis笔记旧版.assets/image-20230406221001476.webp)

<img src="image/Redis笔记旧版.assets/image-20230406220856366.webp" alt="image-20230406220856366" style="zoom:50%;" />

<img src="image/Redis笔记旧版.assets/image-20230406221021739.webp" alt="image-20230406221021739" style="zoom:50%;" />

![image-20230406220829205](image/Redis笔记旧版.assets/image-20230406220829205.webp)

### watch监控（开启乐观锁）

![](image/Redis笔记旧版.assets/image-20230406221419392.webp)

![image-20230406221224677](image/Redis笔记旧版.assets/image-20230406221224677.webp)

![image-20230406221248353](image/Redis笔记旧版.assets/image-20230406221248353.webp)

**==Redis采用乐观锁，但必须要提交的版本大于当前版本才能执行==**

**watch key时，若其他客户端已对key进行修改，当前事务将会被打断，无效**

![image-20230406221547634](image/Redis笔记旧版.assets/image-20230406221547634.webp)

### unwatch

![image-20230406221911990](image/Redis笔记旧版.assets/image-20230406221911990.webp)





![image-20230406222014243](image/Redis笔记旧版.assets/image-20230406222014243.webp)

![image-20230406222325070](image/Redis笔记旧版.assets/image-20230406222325070.webp)



### 小总结

![image-20230406222053893](image/Redis笔记旧版.assets/image-20230406222053893.webp)





# 5、Redis管道

![image-20230408121329331](image/Redis笔记旧版.assets/image-20230408121329331.webp)

## 5.1 管道的由来，是什么

![image-20230408122015366](image/Redis笔记旧版.assets/image-20230408122015366.webp)

![image-20230408120938690](image/Redis笔记旧版.assets/image-20230408120938690.webp)

![image-20230408121039056](image/Redis笔记旧版.assets/image-20230408121039056.webp)

![image-20230408121112824](image/Redis笔记旧版.assets/image-20230408121112824.webp)





## ==5.2 管道的操作==

![image-20230408122353571](image/Redis笔记旧版.assets/image-20230408122353571.webp)

**在Linux窗口下，写好一个txt文件，用管道符执行该文件中的所有命令**



## 5.3 管道与原生批处理命令和事务的对比，使用注意事项

![image-20230408122448993](image/Redis笔记旧版.assets/image-20230408122448993.webp)

pipeline与原生批处理命令对比：

![image-20230408122740607](image/Redis笔记旧版.assets/image-20230408122740607.webp)

pipeline与事务对比：

![image-20230408122831546](image/Redis笔记旧版.assets/image-20230408122831546.webp)

使用pipeline注意事项：

![image-20230408123247953](image/Redis笔记旧版.assets/image-20230408123247953.webp)





# 6、Redis发布订阅（了解即可）

![image-20230408133106050](image/Redis笔记旧版.assets/image-20230408133106050.webp)

## 6.1 是什么

![image-20230408133137417](image/Redis笔记旧版.assets/image-20230408133137417.webp)

是stream的前身

## 6.2 能干嘛

![image-20230408133423825](image/Redis笔记旧版.assets/image-20230408133423825.webp)

![image-20230408133450828](image/Redis笔记旧版.assets/image-20230408133450828.webp)

## 6.3 常用命令

![image-20230408133534728](image/Redis笔记旧版.assets/image-20230408133534728.webp)

![image-20230408133710677](image/Redis笔记旧版.assets/image-20230408133710677.webp)

![image-20230408133853118](image/Redis笔记旧版.assets/image-20230408133853118.webp)

![image-20230408134016908](image/Redis笔记旧版.assets/image-20230408134016908.webp)

![image-20230408134039254](image/Redis笔记旧版.assets/image-20230408134039254.webp)

![image-20230408134105040](image/Redis笔记旧版.assets/image-20230408134105040.webp)

![image-20230408134158868](image/Redis笔记旧版.assets/image-20230408134158868.webp)

![image-20230408134400720](image/Redis笔记旧版.assets/image-20230408134400720.webp)



**演示：**

![image-20230408135504653](image/Redis笔记旧版.assets/image-20230408135504653.webp)

**psubscribe是订阅带有通配符*的频道**



**小总结：**

![image-20230408135955937](image/Redis笔记旧版.assets/image-20230408135955937.webp)





# 7、Redis主从复制

![image-20230408140117380](image/Redis笔记旧版.assets/image-20230408140117380.webp)

## 7.1 是什么、能干嘛

![image-20230408140207933](image/Redis笔记旧版.assets/image-20230408140207933.webp)

**==master以写为主，slave以读为主==**

![image-20230408140527803](image/Redis笔记旧版.assets/image-20230408140527803.webp)

<img src="image/Redis笔记旧版.assets/image-20230408140537631.webp" alt="image-20230408140537631" style="zoom:50%;" />





## 7.2 操作与操作命令

![image-20230408140713081](image/Redis笔记旧版.assets/image-20230408140713081.webp)

![image-20230408140751191](image/Redis笔记旧版.assets/image-20230408140751191.webp)



权限细节：

![image-20230408140848125](image/Redis笔记旧版.assets/image-20230408140848125.webp)

**==从机要设置masterauth来获得主机的许可==**



![image-20230408141303037](image/Redis笔记旧版.assets/image-20230408141303037.webp)

![image-20230408141419076](image/Redis笔记旧版.assets/image-20230408141419076.webp)

**手动给自己临时换一个主机**



![image-20230408141429591](image/Redis笔记旧版.assets/image-20230408141429591.webp)





## ==7.3 实操案例演示==

![image-20230408141558903](image/Redis笔记旧版.assets/image-20230408141558903.webp)

![image-20230408211647900](image/Redis笔记旧版.assets/image-20230408211647900.webp)



### 准备工作

<img src="image/Redis笔记旧版.assets/image-20230408141742401.webp" alt="image-20230408141742401" style="zoom:33%;" />

![image-20230408141618892](image/Redis笔记旧版.assets/image-20230408141618892.webp)

![image-20230408142229016](image/Redis笔记旧版.assets/image-20230408142229016.webp)







![image-20230408142241404](image/Redis笔记旧版.assets/image-20230408142241404.webp)

![image-20230408142459814](image/Redis笔记旧版.assets/image-20230408142459814.webp)

![image-20230408142533115](image/Redis笔记旧版.assets/image-20230408142533115.webp)

**==配主不配从==**



### 配置细节

![image-20230408143002113](image/Redis笔记旧版.assets/image-20230408143002113.webp)

![image-20230408143127271](image/Redis笔记旧版.assets/image-20230408143127271.webp)



**==主机只配前十步，从机需要配第十一步==**

从机配置replicaof和masterauth



dump文件名为dump6379.rdb

log文件名为6379.log





### ==一主二仆实现==

#### 配置文件写死情况

![image-20230408155758562](image/Redis笔记旧版.assets/image-20230408155758562.webp)

**==先开主机，再开从机==**

**==从机启动时，一定要指明端口，否则默认为6379==**

![image-20230410192559543](image/Redis笔记旧版.assets/image-20230410192559543.webp)



![image-20230408160528137](image/Redis笔记旧版.assets/image-20230408160528137.webp)

主机日志

![image-20230408160204938](image/Redis笔记旧版.assets/image-20230408160204938.webp)

备机日志

![image-20230408160401834](image/Redis笔记旧版.assets/image-20230408160401834.webp)



**使用info  replication命令查看主从关系**

<img src="image/Redis笔记旧版.assets/image-20230408160618114.webp" alt="image-20230408160618114" style="zoom:50%;" />



#### 主从问题演示（配置写死）

![image-20230408161138030](image/Redis笔记旧版.assets/image-20230408161138030.webp)

![image-20230408160949114](image/Redis笔记旧版.assets/image-20230408160949114.webp)

**==从机不可以执行写命令，读写分离==**



![image-20230408161017451](image/Redis笔记旧版.assets/image-20230408161017451.webp)

**==可以跟上，首次开机全部复制==**



![image-20230408161536521](image/Redis笔记旧版.assets/image-20230408161536521.webp)

**==主机shutdown过后，从机原地待命==**



![image-20230408161735149](image/Redis笔记旧版.assets/image-20230408161735149.webp)

**==关系依旧==**



![image-20230408161801434](image/Redis笔记旧版.assets/image-20230408161801434.webp)

**==和问题2一样，可以跟上==**



#### 命令操作手动指定情况

![image-20230408161947054](image/Redis笔记旧版.assets/image-20230408161947054.webp)

**==从机重启过后，关系不在了==**



![image-20230408162719965](image/Redis笔记旧版.assets/image-20230408162719965.webp)



### 薪火相传实现

**![image-20230408211712851](image/Redis笔记旧版.assets/image-20230408211712851.webp)*

<img src="image/Redis笔记旧版.assets/image-20230408211751697.webp" alt="image-20230408211751697" style="zoom: 33%;" />

**==slave6380还是不能写操作==**





### 反客为主实现

![image-20230408212547621](image/Redis笔记旧版.assets/image-20230408212547621.webp)





## ==7.4 复制原理和工作流程总结==

![image-20230408212807487](image/Redis笔记旧版.assets/image-20230408212807487.webp)

![image-20230408212925378](image/Redis笔记旧版.assets/image-20230408212925378.webp)

![image-20230408212951325](image/Redis笔记旧版.assets/image-20230408212951325.webp)



**首次连接，全量复制：**

![](image/Redis笔记旧版.assets/image-20230408213021098.webp)



![image-20230408213457007](image/Redis笔记旧版.assets/image-20230408213457007.webp)

![image-20230408213503623](image/Redis笔记旧版.assets/image-20230408213503623.webp)



![image-20230408213546586](image/Redis笔记旧版.assets/image-20230408213546586.webp)



![image-20230408213617020](image/Redis笔记旧版.assets/image-20230408213617020.webp)





## 7.5 主从复制缺点

![image-20230408214049921](image/Redis笔记旧版.assets/image-20230408214049921.webp)

![image-20230408214109882](image/Redis笔记旧版.assets/image-20230408214109882.webp)



![image-20230408214339860](image/Redis笔记旧版.assets/image-20230408214339860.webp)

![image-20230408214647487](image/Redis笔记旧版.assets/image-20230408214647487.webp)



引入哨兵和集群进行改进





# 8、Redis哨兵监控

![image-20230408214830459](image/Redis笔记旧版.assets/image-20230408214830459.webp)

## 8.1 是什么、能干嘛

![image-20230408215731885](image/Redis笔记旧版.assets/image-20230408215731885.webp)

![image-20230408215802828](image/Redis笔记旧版.assets/image-20230408215802828.webp)

![image-20230408220023357](image/Redis笔记旧版.assets/image-20230408220023357.webp)





## ==8.2 操作与案例==

![image-20230408220452407](image/Redis笔记旧版.assets/image-20230408220452407.webp)



### 准备工作

**Redis Sentinel架构，前提说明**

![image-20230408221206785](image/Redis笔记旧版.assets/image-20230408221206785.webp)

![image-20230408224401749](image/Redis笔记旧版.assets/image-20230408224401749.webp)

**至少需要三台哨兵，因为网络抖动等等原因，可能造成哨兵down机，并且需要多台哨兵进行投票选取新的master**



### 配置细节

![image-20230408224809355](image/Redis笔记旧版.assets/image-20230408224809355.webp)



<img src="image/Redis笔记旧版.assets/image-20230408225425792.webp" alt="image-20230408225425792" style="zoom: 25%;" />

黑字和之前的一样配置



![image-20230409153923243](image/Redis笔记旧版.assets/image-20230409153923243.webp)

**==quorum参数：法定票数==**

![image-20230408225558891](image/Redis笔记旧版.assets/image-20230408225558891.webp)

![image-20230409153549805](image/Redis笔记旧版.assets/image-20230409153549805.webp)

![image-20230409153638060](image/Redis笔记旧版.assets/image-20230409153638060.webp)

**==至少要有quorum个sentinel认为master有故障，才会进行下线和故障转移==**



**设置哨兵连接master的密码**

![image-20230409153950260](image/Redis笔记旧版.assets/image-20230409153950260.webp)



其他参数：（用默认即可）

![image-20230409154146355](image/Redis笔记旧版.assets/image-20230409154146355.webp)





### 本次案例sentinel 文件通用配置

![image-20230409154450472](image/Redis笔记旧版.assets/image-20230409154450472.webp)

![image-20230409154511552](image/Redis笔记旧版.assets/image-20230409154511552.webp)



**将三个sentinel端口.conf配置文件如下格式写好**

![image-20230409154740426](image/Redis笔记旧版.assets/image-20230409154740426.webp)

![image-20230409155150040](image/Redis笔记旧版.assets/image-20230409155150040.webp)

自己的：

![image-20230409160326358](image/Redis笔记旧版.assets/image-20230409160326358.webp)





### 哨兵集群启动

#### 一主二从启动

![image-20230409160502282](image/Redis笔记旧版.assets/image-20230409160502282.webp)

**==从机启动时，一定要指明端口，否则默认为6379==**

![image-20230410192559543](image/Redis笔记旧版.assets/image-20230410192559543.webp)

![image-20230409160517691](image/Redis笔记旧版.assets/image-20230409160517691.webp)

![image-20230409160537725](image/Redis笔记旧版.assets/image-20230409160537725.webp)

**==前面主从复制的时候，master的redis6379.conf不用设置masterauth，但是这里要设置新主机的密码，因为此时主机可能变从机，推荐所有密码都设为一致，避免报错==**

![image-20230409161059750](image/Redis笔记旧版.assets/image-20230409161059750.webp)

![image-20230409161732939](image/Redis笔记旧版.assets/image-20230409161732939.webp)



![image-20230409161948018](image/Redis笔记旧版.assets/image-20230409161948018.webp)



#### 哨兵启动

![image-20230409162303680](image/Redis笔记旧版.assets/image-20230409162303680.webp)

**==在同一个终端下启动三个哨兵（实际应用都是一台主机一个哨兵）==**

![image-20230409162317618](image/Redis笔记旧版.assets/image-20230409162317618.webp)

![image-20230409162408724](image/Redis笔记旧版.assets/image-20230409162408724.webp)

![image-20230409162915334](image/Redis笔记旧版.assets/image-20230409162915334.webp)





==sentinel日志文件：==

![image-20230410195557845](image/Redis笔记旧版.assets/image-20230410195557845.webp)

![image-20230410195522975](image/Redis笔记旧版.assets/image-20230410195522975.webp)





**==哨兵启动后，会在各自的sentinel.conf配置文件中自动追加重写一些信息，与其生成的log文件相对应==**

例如自己的id，监控的master是谁，slave有哪些，哨兵集群中其他的哨兵是谁

![image-20230410200005543](image/Redis笔记旧版.assets/image-20230410200005543.webp)





### master挂了，含问题思考

![image-20230409164303693](image/Redis笔记旧版.assets/image-20230409164303693.webp)

![image-20230409164637286](image/Redis笔记旧版.assets/image-20230409164637286.webp)

**==1、从机数据OK，只是要等sentinel进行投票选举，master刚挂时可能会提醒断开连接==**

**==2、会从剩下的两台机器上选出新的master==**

**==3、重启后，将会变成slave，6379下位后，会自动向其redis6379.conf最后追加重写一些内容==**

![image-20230409170647296](image/Redis笔记旧版.assets/image-20230409170647296.webp)

<img src="image/Redis笔记旧版.assets/image-20230409170826523.webp" alt="image-20230409170826523" style="zoom: 33%;" />

![image-20230409170655715](image/Redis笔记旧版.assets/image-20230409170655715.webp)

![image-20230409171113649](image/Redis笔记旧版.assets/image-20230409171113649.webp)



### ==master挂后配置文件的改变==

![image-20230409173948430](image/Redis笔记旧版.assets/image-20230409173948430.webp)

![image-20230409174348692](image/Redis笔记旧版.assets/image-20230409174348692.webp)

**==哨兵启动后，会在各自的sentinel.conf配置文件中自动追加重写一些信息，与其生成的log文件相对应==**

例如自己的id，监控的master是谁，slave有哪些，哨兵集群中其他的哨兵是谁

![image-20230410200005543](image/Redis笔记旧版.assets/image-20230410200005543.webp)



**==6379下位后开机，会自动向其redis6379.conf最后追加重写一些内容==**

![image-20230409171519355](image/Redis笔记旧版.assets/image-20230409171519355.webp)



**==主从切换后，之前哨兵启动时往sentinel追加的信息也会相应的发生该变，因为主从关系变了==**



![image-20230409205846099](image/Redis笔记旧版.assets/image-20230409205846099.webp)



## ==8.3 哨兵故障转移运行流程==

![image-20230409205940971](image/Redis笔记旧版.assets/image-20230409205940971.webp)

**==建议sentinel采取奇数台，防止某一台sentinel无法连接到master导致误切换==**

![image-20230409210441156](image/Redis笔记旧版.assets/image-20230409210441156.webp)

### Sdown主观下线

![image-20230409210637243](image/Redis笔记旧版.assets/image-20230409210637243.webp)

![image-20230409210809009](image/Redis笔记旧版.assets/image-20230409210809009.webp)

### Odown客观下线

![image-20230409211028803](image/Redis笔记旧版.assets/image-20230409211028803.webp)

![image-20230409211042076](image/Redis笔记旧版.assets/image-20230409211042076.webp)



### Raft算法选举出领导者哨兵（兵王）

![image-20230409212402031](image/Redis笔记旧版.assets/image-20230409212402031.webp)

![image-20230409213142631](image/Redis笔记旧版.assets/image-20230409213142631.webp)

**简单了解Raft算法**

![image-20230409220651126](image/Redis笔记旧版.assets/image-20230409220651126.webp)



### ==兵王选出新master（含选举算法）==

![image-20230409221052860](image/Redis笔记旧版.assets/image-20230409221052860.webp)

**新主登基**

![image-20230409221634107](image/Redis笔记旧版.assets/image-20230409221634107.webp)

![image-20230409221858246](image/Redis笔记旧版.assets/image-20230409221858246.webp)

<img src="image/Redis笔记旧版.assets/image-20230409221927353.webp" alt="image-20230409221927353" style="zoom:33%;" />

**==选举算发：优先级--> offset--> RunID==**

<img src="image/Redis笔记旧版.assets/image-20230409221724624.webp" alt="image-20230409221724624" style="zoom: 25%;" />



**群臣俯首**

![image-20230409222227063](image/Redis笔记旧版.assets/image-20230409222227063.webp)



**旧主拜服**

![image-20230409222345404](image/Redis笔记旧版.assets/image-20230409222345404.webp)

**小总结**

![image-20230409222418931](image/Redis笔记旧版.assets/image-20230409222418931.webp)

![image-20230409222446305](image/Redis笔记旧版.assets/image-20230409222446305.webp)



## 8.4 哨兵使用建议与缺点

![image-20230409222956031](image/Redis笔记旧版.assets/image-20230409222956031.webp)

**==建议sentinel采取奇数台，防止某一台sentinel无法连接到master导致误切换==**

**主从 + 哨兵也不能保证数据零丢失，所以引出集群**





# ==9、Redis集群分片==

![image-20230410212906598](image/Redis笔记旧版.assets/image-20230410212906598.webp)

![image-20230410212814102](image/Redis笔记旧版.assets/image-20230410212814102.webp)

## 9.1 是什么、能干嘛

![image-20230410213205105](image/Redis笔记旧版.assets/image-20230410213205105.webp)

![image-20230410213216449](image/Redis笔记旧版.assets/image-20230410213216449.webp)

![image-20230410213403087](image/Redis笔记旧版.assets/image-20230410213403087.webp)

**==不用再使用哨兵，只需链接任意一个集群中的可用2节点即可，master之间数据共享，但是不保证强一致性==**





## ==9.2 集群算法-分片-槽位slot==

![image-20230410214145186](image/Redis笔记旧版.assets/image-20230410214145186.webp)

![image-20230410214309164](image/Redis笔记旧版.assets/image-20230410214309164.webp)

**==被分为16384个槽，有效设置了16384个主节点的集群大小上限，但是建议最大节点大小约为1000个节点==**



### 槽位slot

![image-20230410214525181](image/Redis笔记旧版.assets/image-20230410214525181.webp)

### 分片和它的优势

![image-20230410214751714](image/Redis笔记旧版.assets/image-20230410214751714.webp)

![image-20230410215136036](image/Redis笔记旧版.assets/image-20230410215136036.webp)





### 槽位映射算法

![image-20230410215439039](image/Redis笔记旧版.assets/image-20230410215439039.webp)

#### 哈希取余分区

![image-20230410220439769](image/Redis笔记旧版.assets/image-20230410220439769.webp)

![image-20230410220754453](image/Redis笔记旧版.assets/image-20230410220754453.webp)

![image-20230410220747234](image/Redis笔记旧版.assets/image-20230410220747234.webp)



#### 一致性哈希算法分区

<img src="image/Redis笔记旧版.assets/image-20230410220940292.webp" alt="image-20230410220940292" style="zoom:33%;" />

![image-20230410220841456](image/Redis笔记旧版.assets/image-20230410220841456.webp)

![image-20230410220905305](image/Redis笔记旧版.assets/image-20230410220905305.webp)

![image-20230410220924182](image/Redis笔记旧版.assets/image-20230410220924182.webp)



**算法构建一致性哈希环**

![image-20230410221104902](image/Redis笔记旧版.assets/image-20230410221104902.webp)



**服务器IP节点映射**

节点：redis服务器

![image-20230410221419152](image/Redis笔记旧版.assets/image-20230410221419152.webp)



**key落到服务器的落键规则**

![image-20230410221816579](image/Redis笔记旧版.assets/image-20230410221816579.webp)

![image-20230410221710199](image/Redis笔记旧版.assets/image-20230410221710199.webp)



![image-20230410221938760](image/Redis笔记旧版.assets/image-20230410221938760.webp)

![image-20230410222014396](image/Redis笔记旧版.assets/image-20230410222014396.webp)

![image-20230410222039801](image/Redis笔记旧版.assets/image-20230410222039801.webp)

![image-20230410222146248](image/Redis笔记旧版.assets/image-20230410222146248.webp)





![image-20230410222213891](image/Redis笔记旧版.assets/image-20230410222213891.webp)

![image-20230410222156798](image/Redis笔记旧版.assets/image-20230410222156798.webp)

![image-20230410222256716](image/Redis笔记旧版.assets/image-20230410222256716.webp)





#### ==哈希槽分区==

![image-20230410222419545](image/Redis笔记旧版.assets/image-20230410222419545.webp)

![image-20230410222814597](image/Redis笔记旧版.assets/image-20230410222814597.webp)

![image-20230410222915337](image/Redis笔记旧版.assets/image-20230410222915337.webp)





### ==为什么最大槽数是16384==

![image-20230410223424858](image/Redis笔记旧版.assets/image-20230410223424858.webp)

![image-20230410223513872](image/Redis笔记旧版.assets/image-20230410223513872.webp)

![image-20230410223538678](image/Redis笔记旧版.assets/image-20230410223538678.webp)

![image-20230410223650056](image/Redis笔记旧版.assets/image-20230410223650056.webp)

**==（1）65536消息头太大==**

![image-20230410223821799](image/Redis笔记旧版.assets/image-20230410223821799.webp)

**==（2）1000以内节点16384个槽够用了，不易造成网络拥堵==**

![image-20230410223913265](image/Redis笔记旧版.assets/image-20230410223913265.webp)

**==（3）在节点少的情况下，即小型集群中，因为填充率为slots/N，若采用65536的话，填充率将会很高，压缩比将会很低，不容易传输，但是采用16384的话，填充率低一些，压缩比将会高很多，容易传输些==**





### 不保证强一致性

![image-20230410230449517](image/Redis笔记旧版.assets/image-20230410230449517.webp)





## ==9.3 三主三从集群搭建==

![image-20230411155840013](image/Redis笔记旧版.assets/image-20230411155840013.webp)

### ==3主3从redis集群配置==

![image-20230411160032970](image/Redis笔记旧版.assets/image-20230411160032970.webp)



#### 新建6台独立的redis实例服务

![image-20230411160402336](image/Redis笔记旧版.assets/image-20230411160402336.webp)

 

![image-20230411160420264](image/Redis笔记旧版.assets/image-20230411160420264.webp)



![image-20230411160526569](image/Redis笔记旧版.assets/image-20230411160526569.webp)

**==相比主从复制，只写了masterauth，没有写replicaof==**

```
bind 0.0.0.0
daemonize yes
protected-mode no
port 6381
logfile "/myredis/cluster/cluster6381.log"
pidfile /myredis/cluster6381.pid
dir /myredis/cluster
dbfilename dump6381.rdb
appendonly yes
appendfilename "appendonly6381.aof"
requirepass xxxxxxxxxxxxxx@
masterauth xxxxxxxxxxxxxx@
cluster-enabled yes
cluster-config-file nodes-6381.conf
cluster-node-timeout 5000
```

![image-20230411161810076](image/Redis笔记旧版.assets/image-20230411161810076.webp)

**==这里只需启动redis-server==**



####  redis-cli为6台机器构建集群关系

![image-20230411185514494](image/Redis笔记旧版.assets/image-20230411185514494.webp)

![image-20230411185526464](image/Redis笔记旧版.assets/image-20230411185526464.webp)

**==redis-cli语句如下==**，并没有打开客户端，只是构建了集群

```
redis-cli -a 111111 --cluster create --cluster-replicas 1 192.168.111.185:6381 192.168.111.185:6382 192.168.111.172:6383 192.168.111.172:6384 192.168.111.184:6385 192.168.111.184:6386
```

![image-20230411190221954](image/Redis笔记旧版.assets/image-20230411190221954.webp)

**==主从关系是随机分配的==**

![image-20230411190310678](image/Redis笔记旧版.assets/image-20230411190310678.webp)

**集群启动后会产生nodes开头的文件**

![image-20230411191114952](image/Redis笔记旧版.assets/image-20230411191114952.webp)



#### redis-cli打开6381客户端为切入点，查看并检验集群的状态

![image-20230411191320016](image/Redis笔记旧版.assets/image-20230411191320016.webp)

**==再使用redis-cli打开6381的客服端，记得指明端口==**



**==根据下面的读写测试，这里加一个-c，表示路由==**

```
redis-cli -a 111111 -p 6381 -c
```

![image-20230411191329641](image/Redis笔记旧版.assets/image-20230411191329641.webp)

**可以使用==info replication==和==cluster nodes== 命令来查看**

![image-20230411191816166](image/Redis笔记旧版.assets/image-20230411191816166.webp)





### 3主3从集群读写

![image-20230411192632504](image/Redis笔记旧版.assets/image-20230411192632504.webp)



![image-20230411192718591](image/Redis笔记旧版.assets/image-20230411192718591.webp)

![image-20230411192618705](image/Redis笔记旧版.assets/image-20230411192618705.webp)



![image-20230411192824959](image/Redis笔记旧版.assets/image-20230411192824959.webp)

![image-20230411192808854](image/Redis笔记旧版.assets/image-20230411192808854.webp)



![image-20230411192820642](image/Redis笔记旧版.assets/image-20230411192820642.webp)

```
redis-cli -a 111111 -p 6381 -c
```

![image-20230411193152962](image/Redis笔记旧版.assets/image-20230411193152962.webp)

![image-20230411193554765](image/Redis笔记旧版.assets/image-20230411193554765.webp)

![image-20230411192830986](image/Redis笔记旧版.assets/image-20230411192830986.webp)

![image-20230411193252458](image/Redis笔记旧版.assets/image-20230411193252458.webp)

![image-20230411192836243](image/Redis笔记旧版.assets/image-20230411192836243.webp)

```
cluster keyslot k1
```

![image-20230411193328906](image/Redis笔记旧版.assets/image-20230411193328906.webp)





### 主从容错切换迁移案例(即一个master宕机)、不保证强一致性、节点从属调整(手动恢复6381master身份)

![image-20230411193727209](image/Redis笔记旧版.assets/image-20230411193727209.webp)



**容错切换迁移**

![image-20230411193812792](image/Redis笔记旧版.assets/image-20230411193812792.webp)

![image-20230411193846054](image/Redis笔记旧版.assets/image-20230411193846054.webp)

![image-20230411194103431](image/Redis笔记旧版.assets/image-20230411194103431.webp)

**==master宕机后，其真实从机上位==**

![image-20230411194132853](image/Redis笔记旧版.assets/image-20230411194132853.webp)



![image-20230411194731132](image/Redis笔记旧版.assets/image-20230411194731132.webp)

**==6381变成slave==**

![image-20230411194738722](image/Redis笔记旧版.assets/image-20230411194738722.webp)





**redis集群不保证强一致性**

![image-20230411194830000](image/Redis笔记旧版.assets/image-20230411194830000.webp)

![image-20230411194936604](image/Redis笔记旧版.assets/image-20230411194936604.webp)





**手动故障转移（节点从属调整：恢复6381master身份）**

![image-20230411195121396](image/Redis笔记旧版.assets/image-20230411195121396.webp)

![image-20230411195833357](image/Redis笔记旧版.assets/image-20230411195833357.webp)

```
cluster failover
```

![image-20230411195821023](image/Redis笔记旧版.assets/image-20230411195821023.webp)





### ==主从扩容==

![image-20230411200141308](image/Redis笔记旧版.assets/image-20230411200141308.webp)



**主从扩容全部命令总结：**

```shell
redis-server /myredis/cluster/redisCluster6387.conf		#启动6387server

redis-server /myredis/cluster/redisCluster6388.conf		#启动6388server

redis-cli -a 111111 --cluster add-node 192.168.111.100:6387 192.168.111.100:6381	
#将6387作为新的master加入集群，6381为引荐人

redis-cli -a 111111 --cluster check 192.168.111.100:6381	#第一次检查

redis-cli -a 111111 --cluster reshard 192.168.111.100:6381		#给6387分配slot

redis-cli -a 111111 --cluster check 192.168.111.100:6381		#第二次检查

redis-cli -a 111111 --cluster add-node 192.168.111.100:6388 192.168.111.100:6387 --cluster-slave --cluster-master-id xxxxxxxxxxxxxxxxxxxxx(6387id)
#让6388成为6387的从节点

redis-cli -a 111111 --cluster check 192.168.111.100:6381		#第三c
```



![image-20230411200238386](image/Redis笔记旧版.assets/image-20230411200238386.webp)



![image-20230411200258446](image/Redis笔记旧版.assets/image-20230411200258446.webp)

![image-20230411200308296](image/Redis笔记旧版.assets/image-20230411200308296.webp)



![image-20230411200329974](image/Redis笔记旧版.assets/image-20230411200329974.webp)

**==redis-cli -a 111111 --cluster add-node 192.168.111.100:6387 192.168.111.100:6381==**

![image-20230411200344808](image/Redis笔记旧版.assets/image-20230411200344808.webp)



![image-20230411200528594](image/Redis笔记旧版.assets/image-20230411200528594.webp)

**==redis-cli -a 111111 --cluster check 192.168.111.100:6381==**

![image-20230411200541581](image/Redis笔记旧版.assets/image-20230411200541581.webp)



![image-20230411200801940](image/Redis笔记旧版.assets/image-20230411200801940.webp)

**==redis-cli -a 111111 --cluster reshard 192.168.111.100:6381==**

![image-20230411200820289](image/Redis笔记旧版.assets/image-20230411200820289.webp)

![image-20230411201047651](image/Redis笔记旧版.assets/image-20230411201047651.webp)



![image-20230411201336969](image/Redis笔记旧版.assets/image-20230411201336969.webp)

**==6387的slot槽是从其他三家匀过来的==**

![image-20230411201409810](image/Redis笔记旧版.assets/image-20230411201409810.webp)



![image-20230411201212167](image/Redis笔记旧版.assets/image-20230411201212167.webp)

**==redis-cli -a 111111 --cluster check 192.168.111.100:6381==**

![image-20230411201236370](image/Redis笔记旧版.assets/image-20230411201236370.webp)



![image-20230411201435700](image/Redis笔记旧版.assets/image-20230411201435700.webp)

**==redis-cli -a 111111 --cluster add-node 192.168.111.100:6388 192.168.111.100:6387 --cluster-slave --cluster-master-id xxxxxxxxxxxxxxxxx(6387的id)==**

![image-20230411201451758](image/Redis笔记旧版.assets/image-20230411201451758.webp)



![image-20230411201754433](image/Redis笔记旧版.assets/image-20230411201754433.webp)

**==redis-cli -a 111111 --cluster check 192.168.111.100:6381==**

![image-20230411201804991](image/Redis笔记旧版.assets/image-20230411201804991.webp)





### ==主从缩容==

![image-20230411212355577](image/Redis笔记旧版.assets/image-20230411212355577.webp)

![image-20230411212210424](image/Redis笔记旧版.assets/image-20230411212210424.webp)



**主从缩容全部命令总结：**

```shell
redis-cli -a 111111 --cluster check 192.168.111.100:6388	#获得6388的ID
redis-cli -a 111111 --cluster del-node 192.168.111.100:6388 xxxxxxxx(6388id)  #删除6388
redis-cli -a 111111 --cluster reshard 192.168.111.100:6381    #把6387的slot都给6381
redis-cli -a 111111 --cluster check 192.168.111.100:6381    #第二次检查
redis-cli -a 111111 --cluster del-node 192.168.111.100:6387 xxxxxxxx(6387id)  #删除6387
redis-cli -a 111111 --cluster check 192.168.111.100:6381	#第三次检查
```





![image-20230411212229950](image/Redis笔记旧版.assets/image-20230411212229950.webp)

```
redis-cli -a 111111 --cluster check 192.168.111.100:6388
```

![image-20230411212239192](image/Redis笔记旧版.assets/image-20230411212239192.webp)



![image-20230411212325608](image/Redis笔记旧版.assets/image-20230411212325608.webp)

```
redis-cli -a 111111 --cluster del-node 192.168.111.100:6388 xxxxxxxxxxxxxxxxx(6388id)
```

![image-20230411212426831](image/Redis笔记旧版.assets/image-20230411212426831.webp)



![image-20230411212527893](image/Redis笔记旧版.assets/image-20230411212527893.webp)

```
redis-cli -a 111111 --cluster reshard 192.168.111.100:6381
```

![image-20230411212614682](image/Redis笔记旧版.assets/image-20230411212614682.webp)

![image-20230411212639959](image/Redis笔记旧版.assets/image-20230411212639959.webp)



![image-20230411212825431](image/Redis笔记旧版.assets/image-20230411212825431.webp)

```
redis-cli -a 111111 --cluster check 192.168.111.100:6381
```

![image-20230411212837370](image/Redis笔记旧版.assets/image-20230411212837370.webp)



![image-20230411212936781](image/Redis笔记旧版.assets/image-20230411212936781.webp)

```
redis-cli -a 111111 --cluster del-node 192.168.111.100:6387 xxxxxxxxxxx(6387id)
```

![image-20230411212946012](image/Redis笔记旧版.assets/image-20230411212946012.webp)



![image-20230411213022252](image/Redis笔记旧版.assets/image-20230411213022252.webp)

```
redis-cli -a 111111 --cluster check 192.168.111.100:6381
```

![image-20230411213028972](image/Redis笔记旧版.assets/image-20230411213028972.webp)





## 9.4 总结：集群常用操作命令和CRC16算法分析

 ![image-20230411213444797](image/Redis笔记旧版.assets/image-20230411213444797.webp)



![image-20230411213935213](image/Redis笔记旧版.assets/image-20230411213935213.webp)

**==用{}设定key的分组，可让其存入同一个slot==**

![image-20230411213641606](image/Redis笔记旧版.assets/image-20230411213641606.webp)

![image-20230411213754047](image/Redis笔记旧版.assets/image-20230411213754047.webp)



![image-20230411213943972](image/Redis笔记旧版.assets/image-20230411213943972.webp)

![image-20230411214051864](image/Redis笔记旧版.assets/image-20230411214051864.webp)





![image-20230411214241003](image/Redis笔记旧版.assets/image-20230411214241003.webp)

![image-20230411214315210](image/Redis笔记旧版.assets/image-20230411214315210.webp)

![image-20230411214322712](image/Redis笔记旧版.assets/image-20230411214322712.webp)



![image-20230411215209114](image/Redis笔记旧版.assets/image-20230411215209114.webp)

查看当前槽是否有key

![image-20230411215101016](image/Redis笔记旧版.assets/image-20230411215101016.webp)



![image-20230411215221913](image/Redis笔记旧版.assets/image-20230411215221913.webp)

![image-20230411215233700](image/Redis笔记旧版.assets/image-20230411215233700.webp)





# 10、SpringBoot整合Redis

![image-20230412153753021](image/Redis笔记旧版.assets/image-20230412153753021.webp)

![image-20230412154146207](image/Redis笔记旧版.assets/image-20230412154146207.webp)



## 10.1 集成jedis

![image-20230412154225636](image/Redis笔记旧版.assets/image-20230412154225636.webp)

![image-20230412154253292](image/Redis笔记旧版.assets/image-20230412154253292.webp)

![image-20230412161522114](image/Redis笔记旧版.assets/image-20230412161522114.webp)

```xml
		<!--导入redis:jedis-->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
        </dependency>
```

写application.properties/yml

```
server.port=7777
spring.application.name=redis7_study
```

![image-20230412161525251](image/Redis笔记旧版.assets/image-20230412161525251.webp)

**==记得关闭linux防火墙：systemctl stop firewalld==**

**==还是连不上注意是不是linux不能上网，检查win + R，输入：services.msc ，启动VMware NAT Servise服务==**

![image-20230412162425540](image/Redis笔记旧版.assets/image-20230412162425540.webp)





## 10.2 集成lettuce

![image-20230412162452175](image/Redis笔记旧版.assets/image-20230412162452175.webp)

![image-20230412162616934](image/Redis笔记旧版.assets/image-20230412162616934.webp)

是线程安全的



![image-20230412163006935](image/Redis笔记旧版.assets/image-20230412163006935.webp)

```xml
		<!--导入redis：lettuce-->
        <dependency>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
        </dependency>
```

![image-20230412164148555](image/Redis笔记旧版.assets/image-20230412164148555.webp)



## ==10.3 集成RedisTemplate==

![image-20230412164318846](image/Redis笔记旧版.assets/image-20230412164318846.webp)





### 连接单机

![image-20230412164355654](image/Redis笔记旧版.assets/image-20230412164355654.webp)

![image-20230412164445454](image/Redis笔记旧版.assets/image-20230412164445454.webp)



**改POM（最后附全部pom.xml内容）**

![image-20230412165525170](image/Redis笔记旧版.assets/image-20230412165525170.webp)

```xml
		<!--springboot与Redis整合依赖-->
        <!--redis：RedisTemplate(下面四个)-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <!--swagger2-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>

        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
```



**写application.properties/yml**

![image-20230412171044640](image/Redis笔记旧版.assets/image-20230412171044640.webp)

```properties
server.port=7777
spring.application.name=redis7_study

#========================logging=========================
logging.level.root=info
logging.level.com.spongehah.redis7=info
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger- %msg%n 

logging.file.name=D:/mylogs2023/redis7_study.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger- %msg%n

#=========================swagger==========================
#控制SwaggerConfig的enable的值
spring.swagger2.enabled=true
#springboot.2.6.X结合swagger2.9.X会提小documentationPluginsBootstrapper空指针异常，
#原因是在springboot2.6,X中将springMVC默认路径匹配策略从AntPathMatcher.更改为PathPatternParser,
#导致出错，解决办法是matching-strategy切换回，之前ant_path_matcher
spring.mvc.pathmatch.matching-strategy=ant_path_matcher

#==========================redis单机===========================
spring.redis.database=0
#修改为自己的真实IP
spring.redis.host=192.168.111.100
spring.redis.port=6379
spring.redis.password=xxxxxxxxxxxxxx@
spring.redis.lettuce.pool.max-active=8
spring.redis.lettuce.pool.max-wait=-1ms
spring.redis.lettuce.pool.max-idle=8
spring.redis.lettuce.pool.min-idle=0
```



**主启动类默认**

![image-20230412171555368](image/Redis笔记旧版.assets/image-20230412171555368.webp)



**业务类**

![image-20230412171528300](image/Redis笔记旧版.assets/image-20230412171528300.webp)



config.RedisConfig（解决了后面测试的序列化问题）

```java
@Configuration
public class RedisConfig
{
    /**
     * redis序列化的工具配置类，下面这个请一定开启配置
     * 127.0.0.1:6379> keys *
     * 1) "ord:102"  序列化过
     * 2) "\xac\xed\x00\x05t\x00\aord:102"   野生，没有序列化过
     * this.redisTemplate.opsForValue(); //提供了操作string类型的所有方法
     * this.redisTemplate.opsForList(); // 提供了操作list类型的所有方法
     * this.redisTemplate.opsForSet(); //提供了操作set的所有方法
     * this.redisTemplate.opsForHash(); //提供了操作hash表的所有方法
     * this.redisTemplate.opsForZSet(); //提供了操作zset的所有方法
     * @param lettuceConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory)
    {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        //设置key序列化方式string
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置value的序列化方式json，使用GenericJackson2JsonRedisSerializer替换默认序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
```

config.SwaggerConfig

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig
{
    @Value("${spring.swagger2.enabled}")
    private Boolean enabled;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enabled)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.spongehah.redis7")) //你自己的package
                .paths(PathSelectors.any())
                .build();
    }
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("springboot利用swagger2构建api接口文档 "+"\t"+ DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()))
                .description("springboot+redis整合,有问题给管理员阳哥邮件:zzyybs@126.com")
                .version("1.0")
                .termsOfServiceUrl("https://www.atguigu.com/")
                .build();
    }
}
```



service.OrderService

```java
@Service
@Slf4j
public class OrderService {
    public static final String ORDER_KEY = "ord:";
    
    @Resource
    private RedisTemplate redisTemplate;
//    private StringRedisTemplate stringRedisTemplate;
    
    public void addOrder(){
        int keyId = ThreadLocalRandom.current().nextInt(1000)+1;
        String serialNo = UUID.randomUUID().toString();
        
        String key = ORDER_KEY+keyId;
        String value = "京东订单" + serialNo;
        
        redisTemplate.opsForValue().set(key,value);
//        stringRedisTemplate.opsForValue().set(key,value);
        
        log.info("***key:{}",key);
        log.info("***value:{}",value);
    }
    
    public String getOrderById(Integer keyId){
        return (String) redisTemplate.opsForValue().get(ORDER_KEY + keyId);
//        return  stringRedisTemplate.opsForValue().get(ORDER_KEY + keyId);
    }
}

```



controller.OrderController

```java
@RestController
@Slf4j
@Api(tags = "订单接口")
public class OrderController {
    @Resource
    private OrderService orderService;
    
    @ApiOperation("新增订单")
    @RequestMapping(value = "/order/add",method = RequestMethod.POST)
    public void addOrder(){
        orderService.addOrder();
    }
    
    @ApiOperation("按照keyId查询订单")
    @RequestMapping(value = "/order/{keyId}",method = RequestMethod.GET)
    public void getOrderById(@PathVariable Integer keyId){
        orderService.getOrderById(keyId);
    }
}
```



**测试**

![image-20230412195519310](image/Redis笔记旧版.assets/image-20230412195519310.webp)

http://localhost:7777/swagger-ui.html#/

**序列化问题**
![image-20230412194149191](image/Redis笔记旧版.assets/image-20230412194149191.webp)

第一种方案：

将OrderService类中的RedisTemplate改为其子类StringRedisTemplate,此时swagger和浏览器中显示正常，若要在redis客户端中正常显示中文，redis-cli命令需要加上--raw



==第二种方案：（推荐）==

配置好RedisConfig类



### 连接集群

![image-20230412195742047](image/Redis笔记旧版.assets/image-20230412195742047.webp)





























































# POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.atguigu.redis7</groupId>
    <artifactId>redis7_study</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.10</version>
        <relativePath/>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <junit.version>4.12</junit.version>
        <log4j.version>1.2.17</log4j.version>
        <lombok.version>1.16.18</lombok.version>
    </properties>

    <dependencies>
        <!--guava Google 开源的 Guava 中自带的布隆过滤器-->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>23.0</version>
        </dependency>
        <!--SpringBoot通用依赖模块-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--jedis-->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>4.3.1</version>
        </dependency>
        <!--lettuce-->
        <!--<dependency>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
            <version>6.2.1.RELEASE</version>
        </dependency>-->
        <!--SpringBoot与Redis整合依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
        <!--swagger2-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
        <!--Mysql数据库驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <!--SpringBoot集成druid连接池-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.16</version>
        </dependency>
        <!--mybatis和springboot整合-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.0</version>
        </dependency>
        <!--hutool-->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.2.3</version>
        </dependency>
        <!--persistence-->
        <dependency>
            <groupId>javax.persistence</groupId>
            <artifactId>persistence-api</artifactId>
            <version>1.0.2</version>
        </dependency>
        <!--通用Mapper-->
        <dependency>
            <groupId>tk.mybatis</groupId>
            <artifactId>mapper</artifactId>
            <version>4.1.5</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
        </dependency>
        <!--通用基础配置junit/devtools/test/log4j/lombok/-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${junit.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>${log4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>


```

