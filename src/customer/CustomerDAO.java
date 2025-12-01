import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerDAO {

    private List<Customer> customers;

    public CustomerDAO() {
        this.customers = new ArrayList<>();
    }

    // ----------------------------
    // CREATE
    // ----------------------------
    public void addCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO customers(id, full_name, phone, email, category, notes, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String idString = c.getCustomerId().toString().replace("-", "");

            pstmt.setString(1, idString);
            pstmt.setString(2, c.getFullName());
            pstmt.setString(3, c.getPhone());
            pstmt.setString(4, c.getEmail());
            pstmt.setString(5, c.getCategory().name());
            pstmt.setString(6, c.getNotes());
            pstmt.setTimestamp(7, Timestamp.valueOf(c.getCreatedAt()));

            pstmt.executeUpdate();
        }
    }

    // ----------------------------
    // READ
    // ----------------------------
    public Customer findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString().replace("-", ""));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        }
        return null;
    }

    public Customer findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM customers WHERE email=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        }
        return null;
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCustomer(rs));
            }
        }
        return list;
    }

    // ----------------------------
    // UPDATE
    // ----------------------------
    public void updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customers SET full_name=?, phone=?, email=?, category=?, notes=? " +
                "WHERE id=?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getFullName());
            pstmt.setString(2, c.getPhone());
            pstmt.setString(3, c.getEmail());
            pstmt.setString(4, c.getCategory().name());
            pstmt.setString(5, c.getNotes());
            pstmt.setString(6, c.getCustomerId().toString().replace("-", ""));

            pstmt.executeUpdate();
        }
    }

    // ----------------------------
    // DELETE
    // ----------------------------
    public void deleteCustomer(UUID id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id=?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString().replace("-", ""));
            pstmt.executeUpdate();
        }
    }

    // ----------------------------
    // HELPER METHOD
    // ----------------------------
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String fullName = rs.getString("full_name");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        Category category = Category.valueOf(rs.getString("category"));
        String notes = rs.getString("notes");
        Timestamp createdTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdTs.toLocalDateTime();

        Customer customer = new Customer(fullName, phone, email, category, notes);
        customer.setCustomerId(id);
        customer.setCreatedAt(createdAt);

        return customer;
    }
}
