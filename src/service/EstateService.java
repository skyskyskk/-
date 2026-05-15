package service;

import dao.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import model.*;
import util.DBUtil;

public class EstateService {
    private HouseTypeDAO houseTypeDAO = new HouseTypeDAO();
    private LandlordDAO landlordDAO = new LandlordDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private HouseDAO houseDAO = new HouseDAO();
    private RentalRecordDAO rentalRecordDAO = new RentalRecordDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private UserDAO userDAO = new UserDAO();
    private Scanner scanner = new Scanner(System.in);
    private User currentUser; // 当前登录用户

    public void run() {
        while (true) {
            System.out.println("\n=== 二手房中介管理系统 ===");
            System.out.println("1. 房屋户型管理");
            System.out.println("2. 房东信息管理");
            System.out.println("3. 客户信息管理");
            System.out.println("4. 房屋信息管理");
            System.out.println("5. 房屋出租登记");
            System.out.println("6. 房屋归还登记");
            System.out.println("7. 租房收费管理");
            System.out.println("8. 统计户型出租数量");
            System.out.println("9. 查看房屋视图");
            System.out.println("0. 退出系统");
            System.out.print("请选择操作: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    manageHouseTypes();
                    break;
                case 2:
                    manageLandlords();
                    break;
                case 3:
                    manageCustomers();
                    break;
                case 4:
                    manageHouses();
                    break;
                case 5:
                    registerRental();
                    break;
                case 6:
                    registerReturn();
                    break;
                case 7:
                    managePayments();
                    break;
                case 8:
                    countRentalByType();
                    break;
                case 9:
                    viewHouseInfo();
                    break;
                case 0:
                    System.out.println("感谢使用，再见！");
                    return;
                default:
                    System.out.println("无效选择，请重新输入！");
            }
        }
    }

    private void manageHouseTypes() {
        System.out.println("\n=== 房屋户型管理 ===");
        System.out.println("1. 添加户型");
        System.out.println("2. 查看所有户型");
        System.out.print("请选择: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("输入户型名称: ");
            String typeName = scanner.nextLine();
            System.out.print("输入户型描述: ");
            String description = scanner.nextLine();

            HouseType type = new HouseType(typeName, description);
            if (houseTypeDAO.addHouseType(type)) {
                System.out.println("户型添加成功！");
            } else {
                System.out.println("户型添加失败！");
            }
        } else if (choice == 2) {
            List<HouseType> types = houseTypeDAO.getAllHouseTypes();
            System.out.println("\n户型列表:");
            for (HouseType type : types) {
                System.out.printf("ID: %d, 名称: %s, 描述: %s%n",
                        type.getTypeId(), type.getTypeName(), type.getDescription());
            }
        }
    }

    private void manageLandlords() {
        System.out.println("\n=== 房东信息管理 ===");
        System.out.println("1. 添加房东");
        System.out.println("2. 查看所有房东");
        System.out.print("请选择: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("输入房东姓名: ");
            String name = scanner.nextLine();
            System.out.print("输入电话: ");
            String phone = scanner.nextLine();
            System.out.print("输入身份证号: ");
            String idCard = scanner.nextLine();
            System.out.print("输入邮箱: ");
            String email = scanner.nextLine();
            System.out.print("输入地址: ");
            String address = scanner.nextLine();

            Landlord landlord = new Landlord(name, phone, idCard);
            landlord.setEmail(email);
            landlord.setAddress(address);

            if (landlordDAO.addLandlord(landlord)) {
                System.out.println("房东添加成功！");
            } else {
                System.out.println("房东添加失败！");
            }
        } else if (choice == 2) {
            List<Landlord> landlords = landlordDAO.getAllLandlords();
            System.out.println("\n房东列表:");
            for (Landlord landlord : landlords) {
                System.out.printf("ID: %d, 姓名: %s, 电话: %s, 身份证: %s%n",
                        landlord.getLandlordId(), landlord.getName(),
                        landlord.getPhone(), landlord.getIdCard());
            }
        }
    }

    private void manageCustomers() {
        System.out.println("\n=== 客户信息管理 ===");
        System.out.println("1. 添加客户");
        System.out.println("2. 查看所有客户");
        System.out.print("请选择: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("输入客户姓名: ");
            String name = scanner.nextLine();
            System.out.print("输入电话: ");
            String phone = scanner.nextLine();
            System.out.print("输入身份证号: ");
            String idCard = scanner.nextLine();
            System.out.print("输入邮箱: ");
            String email = scanner.nextLine();
            System.out.print("输入工作单位: ");
            String workUnit = scanner.nextLine();

            Customer customer = new Customer(name, phone, idCard);
            customer.setEmail(email);
            customer.setWorkUnit(workUnit);

            if (customerDAO.addCustomer(customer)) {
                System.out.println("客户添加成功！");
            } else {
                System.out.println("客户添加失败！");
            }
        } else if (choice == 2) {
            List<Customer> customers = customerDAO.getAllCustomers();
            System.out.println("\n客户列表:");
            for (Customer customer : customers) {
                System.out.printf("ID: %d, 姓名: %s, 电话: %s, 身份证: %s%n",
                        customer.getCustomerId(), customer.getName(),
                        customer.getPhone(), customer.getIdCard());
            }
        }
    }

    private void manageHouses() {
        System.out.println("\n=== 房屋信息管理 ===");
        System.out.println("1. 添加房屋");
        System.out.println("2. 查看所有房屋");
        System.out.println("3. 查看空闲房屋");
        System.out.print("请选择: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("输入房号: ");
            String houseNumber = scanner.nextLine();
            System.out.print("输入地址: ");
            String address = scanner.nextLine();
            System.out.print("输入户型ID: ");
            int typeId = scanner.nextInt();
            System.out.print("输入房东ID: ");
            int landlordId = scanner.nextInt();
            System.out.print("输入面积: ");
            BigDecimal area = scanner.nextBigDecimal();
            System.out.print("输入价格: ");
            BigDecimal price = scanner.nextBigDecimal();
            scanner.nextLine();
            System.out.print("输入状态(空闲/已出租/维修中): ");
            String status = scanner.nextLine();
            System.out.print("输入描述: ");
            String description = scanner.nextLine();

            House house = new House(houseNumber, address, typeId, landlordId, area, price, status);
            house.setDescription(description);

            if (houseDAO.addHouse(house)) {
                System.out.println("房屋添加成功！");
            } else {
                System.out.println("房屋添加失败！");
            }
        } else if (choice == 2) {
            List<House> houses = houseDAO.getAllHouses();
            System.out.println("\n房屋列表:");
            for (House house : houses) {
                System.out.printf("ID: %d, 房号: %s, 地址: %s, 状态: %s%n",
                        house.getHouseId(), house.getHouseNumber(),
                        house.getAddress(), house.getStatus());
            }
        } else if (choice == 3) {
            List<House> houses = houseDAO.getAvailableHouses();
            System.out.println("\n空闲房屋列表:");
            for (House house : houses) {
                System.out.printf("ID: %d, 房号: %s, 地址: %s, 价格: %.2f元/月%n",
                        house.getHouseId(), house.getHouseNumber(),
                        house.getAddress(), house.getPrice());
            }
        }
    }

    private void registerRental() {
        System.out.println("\n=== 房屋出租登记 ===");

        List<House> availableHouses = houseDAO.getAvailableHouses();
        if (availableHouses.isEmpty()) {
            System.out.println("没有空闲房屋！");
            return;
        }

        System.out.println("空闲房屋列表:");
        for (House house : availableHouses) {
            System.out.printf("ID: %d, 房号: %s, 地址: %s, 价格: %.2f元/月%n",
                    house.getHouseId(), house.getHouseNumber(),
                    house.getAddress(), house.getPrice());
        }

        System.out.print("\n选择房屋ID: ");
        int houseId = scanner.nextInt();

        List<Customer> customers = customerDAO.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("请先添加客户！");
            return;
        }

        System.out.println("客户列表:");
        for (Customer customer : customers) {
            System.out.printf("ID: %d, 姓名: %s, 电话: %s%n",
                    customer.getCustomerId(), customer.getName(), customer.getPhone());
        }

        System.out.print("选择客户ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("输入开始日期(YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        System.out.print("输入结束日期(YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        System.out.print("输入月租金: ");
        BigDecimal monthlyRent = scanner.nextBigDecimal();
        System.out.print("输入押金: ");
        BigDecimal deposit = scanner.nextBigDecimal();
        scanner.nextLine();

        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            RentalRecord record = new RentalRecord(houseId, customerId, startDate, endDate, monthlyRent);
            record.setDeposit(deposit);

            if (this.addRentalRecord(record)) {
                System.out.println("出租登记成功！");
            } else {
                System.out.println("出租登记失败！");
            }
        } catch (Exception e) {
            System.out.println("日期格式错误！请使用YYYY-MM-DD格式。");
        }
    }

    private void registerReturn() {
        System.out.println("\n=== 房屋归还登记 ===");

        List<RentalRecord> activeRecords = rentalRecordDAO.getActiveRentalRecords();
        if (activeRecords.isEmpty()) {
            System.out.println("没有需要归还的房屋！");
            return;
        }

        System.out.println("当前出租记录:");
        for (RentalRecord record : activeRecords) {
            System.out.printf("记录ID: %d, 房屋ID: %d, 结束日期: %s%n",
                    record.getRecordId(), record.getHouseId(), record.getEndDate());
        }

        System.out.print("\n选择要归还的记录ID: ");
        int recordId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("是否确认归还？(y/n): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("y")) {
            System.out.println("归还功能将在数据库触发器自动处理");
            System.out.println("当出租记录结束时，触发器会自动将房屋状态改为空闲");
        }
    }

    private void managePayments() {
        System.out.println("\n=== 租房收费管理 ===");
        System.out.println("1. 添加收费记录");
        System.out.println("2. 查看所有收费记录");
        System.out.print("请选择: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            List<RentalRecord> records = rentalRecordDAO.getAllRentalRecords();
            if (records.isEmpty()) {
                System.out.println("没有出租记录！");
                return;
            }

            System.out.println("出租记录列表:");
            for (RentalRecord record : records) {
                System.out.printf("记录ID: %d, 房屋ID: %d, 租金: %.2f元/月%n",
                        record.getRecordId(), record.getHouseId(), record.getMonthlyRent());
            }

            System.out.print("选择记录ID: ");
            int recordId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("输入收费日期(YYYY-MM-DD): ");
            String dateStr = scanner.nextLine();
            System.out.print("输入金额: ");
            BigDecimal amount = scanner.nextBigDecimal();
            scanner.nextLine();
            System.out.print("输入收费类型(租金/押金/其他): ");
            String paymentType = scanner.nextLine();
            System.out.print("输入备注: ");
            String remarks = scanner.nextLine();

            try {
                LocalDate paymentDate = LocalDate.parse(dateStr);
                Payment payment = new Payment(recordId, paymentDate, amount, paymentType);
                payment.setRemarks(remarks);

                if (paymentDAO.addPayment(payment)) {
                    System.out.println("收费记录添加成功！");
                } else {
                    System.out.println("收费记录添加失败！");
                }
            } catch (Exception e) {
                System.out.println("日期格式错误！");
            }
        } else if (choice == 2) {
            List<Payment> payments = paymentDAO.getAllPayments();
            System.out.println("\n收费记录列表:");
            for (Payment payment : payments) {
                System.out.printf("ID: %d, 记录ID: %d, 金额: %.2f, 类型: %s, 日期: %s%n",
                        payment.getPaymentId(), payment.getRecordId(),
                        payment.getAmount(), payment.getPaymentType(),
                        payment.getPaymentDate());
            }
        }
    }

    private void countRentalByType() {
        System.out.println("\n=== 户型出租统计 ===");

        String sql = "CALL GetRentalCountByType()";

        try (Connection conn = DBUtil.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {

            System.out.println("户型统计结果:");
            System.out.println("--------------------------------------------");
            System.out.printf("%-15s %-10s %-10s %-10s%n",
                    "户型名称", "总房屋数", "已出租数", "空闲数");
            System.out.println("--------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-15s %-10d %-10d %-10d%n",
                        rs.getString("type_name"),
                        rs.getInt("total_houses"),
                        rs.getInt("rented_houses"),
                        rs.getInt("vacant_houses"));
            }
            System.out.println("--------------------------------------------");

        } catch (SQLException e) {
            System.out.println("调用存储过程失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewHouseInfo() {
        System.out.println("\n=== 房屋信息视图 ===");

        String sql = "SELECT * FROM HouseInfoView";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("房屋详细信息:");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-10s %-10s %-15s %-15s %-10s %-10s%n",
                    "房号", "房东", "电话", "户型", "面积", "状态");
            System.out.println("--------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10s %-10s %-15s %-15s %-10.2f %-10s%n",
                        rs.getString("house_number"),
                        rs.getString("landlord_name"),
                        rs.getString("landlord_phone"),
                        rs.getString("type_name"),
                        rs.getBigDecimal("area"),
                        rs.getString("status"));
            }
            System.out.println("--------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("查询视图失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== 供Swing界面调用的公共方法（补全缺失的房东相关方法） ==========
    public List<HouseType> getAllHouseTypes() {
        return houseTypeDAO.getAllHouseTypes();
    }

    public boolean addHouseType(HouseType type) {
        return houseTypeDAO.addHouseType(type);
    }
    
    public boolean deleteHouseType(int typeId) {
        return houseTypeDAO.deleteHouseType(typeId);
    }

    // 新增：房东相关公共方法（修复找不到符号错误）
    public List<Landlord> getAllLandlords() {
        return landlordDAO.getAllLandlords();
    }

    public boolean addLandlord(Landlord landlord) {
        return landlordDAO.addLandlord(landlord);
    }
    
    public boolean deleteLandlord(int landlordId) {
        return landlordDAO.deleteLandlord(landlordId);
    }
    
    public boolean updateLandlord(Landlord landlord) {
        return landlordDAO.updateLandlord(landlord);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public boolean addCustomer(Customer customer) {
        return customerDAO.addCustomer(customer);
    }
    
    public boolean deleteCustomer(int customerId) {
        return customerDAO.deleteCustomer(customerId);
    }
    
    public boolean updateCustomer(Customer customer) {
        return customerDAO.updateCustomer(customer);
    }
    
    public Customer getCustomerByUserName(String username) {
        return customerDAO.getCustomerByUserName(username);
    }

    public List<House> getAllHouses() {
        return houseDAO.getAllHouses();
    }

    public List<House> getAvailableHouses() {
        return houseDAO.getAvailableHouses();
    }
    
    // 新增：根据ID获取房屋信息
    public House getHouseById(int houseId) {
        return houseDAO.getHouseById(houseId);
    }

    public boolean addHouse(House house) {
        return houseDAO.addHouse(house);
    }
    
    public boolean deleteHouse(int houseId) {
        return houseDAO.deleteHouse(houseId);
    }
    
    public boolean updateHouse(House house) {
        return houseDAO.updateHouse(house);
    }

    public List<RentalRecord> getAllRentalRecords() {
        return rentalRecordDAO.getAllRentalRecords();
    }

    public List<RentalRecord> getActiveRentalRecords() {
        return rentalRecordDAO.getActiveRentalRecords();
    }
    
    public List<RentalRecord> getRentalRecordsByCustomerId(int customerId) {
        return rentalRecordDAO.getRentalRecordsByCustomerId(customerId);
    }
    
    // 删除出租记录
    public boolean deleteRentalRecord(int recordId) {
        return rentalRecordDAO.deleteRentalRecord(recordId);
    }

    public boolean addRentalRecord(RentalRecord record) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // 开始事务
            
            try {
                // 添加出租记录并获取生成的记录ID
                int recordId = rentalRecordDAO.addRentalRecord(record, conn);
                if (recordId <= 0) {
                    conn.rollback();
                    return false;
                }
                
                // 更新房屋状态为"已出租"
                String updateHouseSql = "UPDATE house SET status = '已出租' WHERE house_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateHouseSql)) {
                    pstmt.setInt(1, record.getHouseId());
                    pstmt.executeUpdate();
                }
                
                // 自动创建押金收费记录
                Payment depositPayment = new Payment(
                    recordId,
                    record.getStartDate(),
                    record.getDeposit(),
                    "押金"
                );
                depositPayment.setRemarks("房屋出租押金");
                paymentDAO.addPayment(depositPayment, conn);
                
                // 自动创建首月租金收费记录
                Payment rentPayment = new Payment(
                    recordId,
                    record.getStartDate(),
                    record.getMonthlyRent(),
                    "租金"
                );
                rentPayment.setRemarks("首月租金");
                paymentDAO.addPayment(rentPayment, conn);
                
                conn.commit(); // 提交事务
                return true;
            } catch (SQLException e) {
                conn.rollback(); // 回滚事务
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean returnHouse(int recordId) {
        // 实现房屋归还逻辑：更新出租记录的结束日期为当前日期，并将房屋状态改为"空闲"
        String getHouseIdSql = "SELECT house_id FROM rental_record WHERE record_id = ?";
        String updateRecordSql = "UPDATE rental_record SET end_date = CURDATE() WHERE record_id = ?";
        String updateHouseSql = "UPDATE house SET status = '空闲' WHERE house_id = ?";
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // 开始事务
            
            // 1. 获取出租记录对应的房屋ID
            int houseId = -1;
            try (PreparedStatement pstmt1 = conn.prepareStatement(getHouseIdSql)) {
                pstmt1.setInt(1, recordId);
                try (ResultSet rs = pstmt1.executeQuery()) {
                    if (rs.next()) {
                        houseId = rs.getInt("house_id");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            // 2. 更新出租记录的结束日期为当前日期
            try (PreparedStatement pstmt2 = conn.prepareStatement(updateRecordSql)) {
                pstmt2.setInt(1, recordId);
                int affectedRows = pstmt2.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // 3. 更新房屋状态为"空闲"
            try (PreparedStatement pstmt3 = conn.prepareStatement(updateHouseSql)) {
                pstmt3.setInt(1, houseId);
                pstmt3.executeUpdate();
            }
            
            conn.commit(); // 提交事务
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }

    public boolean addPayment(Payment payment) {
        return paymentDAO.addPayment(payment);
    }

    // 用户登录验证
    public User login(String username, String password) {
        currentUser = userDAO.login(username, password);
        return currentUser;
    }
    
    // 用户注册功能
    public boolean register(String username, String password, String realName, String phone, String idCard, String email, String workUnit) {
        // 检查用户名是否已存在
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false; // 用户名已存在
            }
        }
        
        // 创建用户记录
        User user = new User(username, password, "tenant");
        boolean userAdded = userDAO.addUser(user);
        if (!userAdded) {
            return false; // 用户添加失败
        }
        
        // 创建客户记录
        Customer customer = new Customer();
        customer.setName(realName);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setIdCard(idCard);
        customer.setWorkUnit(workUnit);
        
        boolean customerAdded = customerDAO.addCustomer(customer);
        return customerAdded;
    }

    // 检查当前用户是否为管理员
    public boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }

    // 检查当前用户是否为租客
    public boolean isTenant() {
        return currentUser != null && "tenant".equals(currentUser.getRole());
    }

    // 获取当前登录用户
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * 租客注册
     * @param username 用户名
     * @param password 密码
     * @return 注册成功返回true，否则返回false
     */
    public boolean registerTenant(String username, String password) {
        // 检查用户名是否已存在
        if (userDAO.checkUsernameExists(username)) {
            return false;
        }
        
        // 创建租客用户
        User user = new User(username, password, "tenant");
        
        // 添加用户到数据库
        return userDAO.addUser(user);
    }
    
    /**
     * 根据用户名删除用户
     * @param username 用户名
     * @return 删除成功返回true，否则返回false
     */
    public boolean deleteUserByUsername(String username) {
        // 检查用户是否存在
        if (!userDAO.checkUsernameExists(username)) {
            return false;
        }
        
        // 获取用户对应的客户信息
        Customer customer = customerDAO.getCustomerByUserName(username);
        
        // 如果有客户信息，检查是否有租房记录
        if (customer != null) {
            List<RentalRecord> rentalRecords = rentalRecordDAO.getRentalRecordsByCustomerId(customer.getCustomerId());
            if (!rentalRecords.isEmpty()) {
                // 有租房记录，不能删除
                return false;
            }
        }
        
        // 删除用户
        boolean userDeleted = userDAO.deleteUserByUsername(username);
        
        // 如果用户删除成功，且有客户信息，同步删除客户信息
        if (userDeleted && customer != null) {
            customerDAO.deleteCustomer(customer.getCustomerId());
        }
        
        return userDeleted;
    }
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    public java.util.List<model.User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    /**
     * 获取所有客户
     * @return 客户列表
     */
    
    /**
     * 管理员添加用户
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @param customerId 对应客户ID（可选）
     * @return 添加成功返回true，否则返回false
     */
    public boolean addUserByAdmin(String username, String password, String role, Integer customerId) {
        // 检查用户名是否已存在
        if (userDAO.checkUsernameExists(username)) {
            return false;
        }
        
        // 创建用户并设置客户ID
        User user = new User(username, password, role);
        if (customerId != null) {
            user.setCustomerId(customerId);
        }
        
        // 添加用户到数据库
        return userDAO.addUser(user);
    }
    
    /**
     * 通过客户ID查找对应的用户名
     * @param customerId 客户ID
     * @return 对应的用户名，如果没有找到返回null
     */
    public String getUsernameByCustomerId(int customerId) {
        // 获取客户信息
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            return null;
        }
        
        // 获取所有用户
        List<User> users = userDAO.getAllUsers();
        
        // 根据创建时间查找对应的用户
        for (User user : users) {
            // 检查用户创建时间是否在客户创建时间的合理范围内
            if (user.getCreatedAt() != null && customer.getCreatedAt() != null) {
                // 允许1小时内的时间差（考虑注册流程可能的延迟）
                long hoursDiff = java.time.Duration.between(
                    user.getCreatedAt(),
                    customer.getCreatedAt()
                ).toHours();
                
                if (hoursDiff >= 0 && hoursDiff <= 1) {
                    return user.getUsername();
                }
            }
        }
        
        return null;
    }
    
    /**
     * 根据用户ID删除用户
     * @param userId 用户ID
     * @return 删除成功返回true，否则返回false
     */
    public boolean deleteUser(int userId) {
        // 获取用户信息
        List<User> users = userDAO.getAllUsers();
        User targetUser = null;
        for (User user : users) {
            if (user.getUserId() == userId) {
                targetUser = user;
                break;
            }
        }
        
        if (targetUser == null) {
            return false;
        }
        
        // 检查用户是否有对应的客户
        Customer customer = customerDAO.getCustomerByUserName(targetUser.getUsername());
        
        // 如果有客户，检查客户是否有租房订单
        if (customer != null) {
            // 检查是否有未归还的租房记录
            List<RentalRecord> records = rentalRecordDAO.getRentalRecordsByCustomerId(customer.getCustomerId());
            for (RentalRecord record : records) {
                if (!"已归还".equals(record.getStatus())) {
                    return false; // 有未归还的租房记录，不能删除
                }
            }
        }
        
        // 删除用户
        return userDAO.deleteUser(userId);
    }
    
    /**
     * 同时删除用户和客户记录
     * @param username 用户名
     * @return 完全删除成功返回true，否则返回false
     */
    public boolean deleteUserAndCustomer(String username) {
        // 先获取客户信息
        Customer customer = customerDAO.getCustomerByUserName(username);
        
        // 如果有客户记录，先检查是否可以删除客户
        if (customer != null) {
            // 检查客户是否有相关出租记录
            boolean canDelete = deleteCustomer(customer.getCustomerId());
            if (!canDelete) {
                // 客户不能删除，直接返回false
                return false;
            }
        }
        
        // 然后删除用户
        boolean userDeleted = deleteUserByUsername(username);
        
        return userDeleted;
    }
}