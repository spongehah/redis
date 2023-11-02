CREATE TABLE `t_customer` (
    `id` int(20) NOT NULL AUTO_INCREMENT,
    `cname` varchar(50) NOT NULL,
    `age` int(10) NOT NULL,
    `phone` varchar(20) NOT NULL,
    `sex` tinyint(4) NOT NULL,
    `birth` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_cname` (`cname`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4