-- 二手房中介管理系统完整数据库重建脚本
-- 包含：创建数据库、创建所有表、添加必要字段、插入测试数据

-- =============================================================================
-- 1. 创建数据库
-- =============================================================================
CREATE DATABASE IF NOT EXISTS estate_management;
USE estate_management;

-- =============================================================================
-- 2. 创建所有表结构
-- =============================================================================

-- 2.1 房屋户型表
CREATE TABLE IF NOT EXISTS house_type (
    type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2.2 房东信息表
CREATE TABLE IF NOT EXISTS landlord (
    landlord_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    id_card VARCHAR(20) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2.3 客户信息表
CREATE TABLE IF NOT EXISTS customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    id_card VARCHAR(20) UNIQUE,
    work_unit VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2.4 房屋信息表
CREATE TABLE IF NOT EXISTS house (
    house_id INT PRIMARY KEY AUTO_INCREMENT,
    house_number VARCHAR(50) NOT NULL UNIQUE,
    address TEXT NOT NULL,
    type_id INT NOT NULL,
    landlord_id INT NOT NULL,
    area DECIMAL(10,2),
    price DECIMAL(10,2),
    status ENUM('空闲', '已出租', '维修中') DEFAULT '空闲',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES house_type(type_id),
    FOREIGN KEY (landlord_id) REFERENCES landlord(landlord_id)
);

-- 2.5 出租记录表
CREATE TABLE IF NOT EXISTS rental_record (
    record_id INT PRIMARY KEY AUTO_INCREMENT,
    house_id INT NOT NULL,
    customer_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    monthly_rent DECIMAL(10,2) NOT NULL,
    deposit DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (house_id) REFERENCES house(house_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- 2.6 收费记录表
CREATE TABLE IF NOT EXISTS payment (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    record_id INT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_type ENUM('租金', '押金', '其他') DEFAULT '租金',
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (record_id) REFERENCES rental_record(record_id)
);

-- 2.7 用户表
CREATE TABLE IF NOT EXISTS user (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('admin', 'tenant') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- 3. 添加必要的额外字段
-- =============================================================================

-- 在user表中添加customer_id字段（关联客户信息）
ALTER TABLE user ADD COLUMN customer_id INT DEFAULT NULL;

-- 创建索引
CREATE INDEX idx_user_customer_id ON user(customer_id);

-- 添加外键约束
ALTER TABLE user ADD FOREIGN KEY (customer_id) REFERENCES customer(customer_id);

-- =============================================================================
-- 4. 创建视图
-- =============================================================================

-- 创建房屋信息视图
CREATE VIEW HouseInfoView AS
SELECT 
    h.house_number,
    l.name as landlord_name,
    l.phone as landlord_phone,
    h.address,
    ht.type_name,
    h.area,
    h.price,
    h.status,
    h.created_at
FROM house h
JOIN landlord l ON h.landlord_id = l.landlord_id
JOIN house_type ht ON h.type_id = ht.type_id
ORDER BY h.house_number;

-- =============================================================================
-- 4. 插入测试数据（每个表至少5条）
-- =============================================================================

-- 4.1 房屋户型表（house_type）- 5条数据
INSERT INTO house_type (type_name, description) 
VALUES 
('一室一厅', '适合单身或情侣居住，面积40-60㎡'),
('两室一厅', '适合小家庭，带阳台，面积60-80㎡'),
('三室两厅', '适合三口之家，双卫，面积80-120㎡'),
('四室三厅', '适合多代同堂，南北通透，面积120-160㎡'),
('LOFT复式', '挑高5.2米，双层结构，适合年轻群体');

-- 4.2 房东信息表（landlord）- 5条数据
INSERT INTO landlord (name, phone, email, address, id_card) 
VALUES
('张三', '13800138001', 'zhangsan@test.com', '北京市海淀区中关村', '110101199001011111'),
('李四', '13800138002', 'lisi@test.com', '上海市徐汇区淮海路', '310104198805122222'),
('王五', '13800138003', 'wangwu@test.com', '广州市天河区珠江新城', '440106198508233333'),
('赵六', '13800138004', 'zhaoliu@test.com', '深圳市南山区科技园', '440305199211054444'),
('孙七', '13800138005', 'sunqi@test.com', '杭州市西湖区黄龙体育中心', '330106198903185555');

-- 4.3 客户信息表（customer）- 5条数据
INSERT INTO customer (name, phone, email, id_card, work_unit) 
VALUES
('周八', '13900139001', 'zhouba@test.com', '110102199302156666', '北京某互联网公司'),
('吴九', '13900139002', 'wujiu@test.com', '310105199507227777', '上海某金融机构'),
('郑十', '13900139003', 'zhengshi@test.com', '440104199109308888', '广州某事业单位'),
('钱十一', '13900139004', 'qianshiyi@test.com', '440306199412059999', '深圳某科技公司'),
('孙十二', '13900139005', 'sunshier@test.com', '330105199604120000', '杭州某电商平台');

-- 4.4 房屋信息表（house）- 5条数据
INSERT INTO house (house_number, address, type_id, landlord_id, area, price, status, description) 
VALUES
('A-101', '北京市朝阳区建国路88号', 1, 1, 55.50, 4200.00, '已出租', '朝南，带飘窗'),
('B-202', '上海市浦东新区张江路1500号', 2, 2, 78.30, 6500.00, '空闲', '电梯房，中间楼层'),
('C-303', '广州市天河区天河路385号', 3, 3, 105.00, 8800.00, '维修中', '刚装修完，通风中'),
('D-404', '深圳市南山区科技园路100号', 4, 4, 132.80, 12000.00, '空闲', '四室三卫，带储物间'),
('E-505', '杭州市西湖区文一路200号', 5, 5, 68.00, 7500.00, '已出租', 'LOFT双层，挑高5米');

-- 4.5 出租记录表（rental_record）- 5条数据
INSERT INTO rental_record (house_id, customer_id, start_date, end_date, monthly_rent, deposit) 
VALUES
(1, 1, '2024-01-15', '2025-01-14', 4200.00, 8400.00),  -- 关联A-101和周八
(5, 5, '2024-03-01', '2025-02-28', 7500.00, 15000.00),  -- 关联E-505和孙十二
(2, 2, '2024-05-20', '2024-11-19', 6500.00, 13000.00),  -- 关联B-202和吴九（已到期）
(3, 3, '2024-07-01', '2025-06-30', 8800.00, 17600.00),  -- 关联C-303和郑十
(4, 4, '2024-09-10', '2025-09-09', 12000.00, 24000.00);   -- 关联D-404和钱十一

-- 4.6 收费记录表（payment）- 5条数据
INSERT INTO payment (record_id, payment_date, amount, payment_type, remarks) 
VALUES
(1, '2024-01-15', 12600.00, '租金', '首月租金+押金'),
(1, '2024-02-15', 4200.00, '租金', '2月租金'),
(5, '2024-03-01', 22500.00, '押金', '押金+首月租金'),
(2, '2024-05-20', 19500.00, '租金', '首月+押金（半年期）'),
(4, '2024-07-01', 26400.00, '其他', '首月租金+押金+物业费');

-- 4.7 用户表（user）- 5条数据（1个管理员，4个租客）
INSERT INTO user (username, password, role, customer_id) 
VALUES
('admin', 'admin123', 'admin', NULL),  -- 管理员账号
('zhousan', 'zhousan123', 'tenant', 1),  -- 租客：周八
('wujing', 'wujing123', 'tenant', 2),  -- 租客：吴九
('zhengsi', 'zhengsi123', 'tenant', 3),  -- 租客：郑十
('qianshi', 'qianshi123', 'tenant', 4);  -- 租客：钱十一

-- =============================================================================
-- 5. 显示数据插入结果
-- =============================================================================

SELECT '数据库重建完成！各表数据量：' AS 提示;
SELECT 'house_type:', COUNT(*) FROM house_type UNION ALL
SELECT 'landlord:', COUNT(*) FROM landlord UNION ALL
SELECT 'customer:', COUNT(*) FROM customer UNION ALL
SELECT 'house:', COUNT(*) FROM house UNION ALL
SELECT 'rental_record:', COUNT(*) FROM rental_record UNION ALL
SELECT 'payment:', COUNT(*) FROM payment UNION ALL
SELECT 'user:', COUNT(*) FROM user;

-- 显示关联关系
SELECT '用户-客户关联关系：' AS 提示;
SELECT u.username, u.role, c.name AS customer_name, c.phone AS customer_phone 
FROM user u 
LEFT JOIN customer c ON u.customer_id = c.customer_id;
