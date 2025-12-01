import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommunicationDAO {

    // Προσθήκη νέας επικοινωνίας
    public void addCommunication(Communication c) throws SQLException {
        String sql = "INSERT INTO communications(id, customer_id, title, description, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getCommunicationId().toString());
            pstmt.setString(2, c.getCustomerId().toString());
            pstmt.setString(3, c.getSubject());
            pstmt.setString(4, c.getMessage());
            pstmt.setString(5, c.getType().name());
            pstmt.setTimestamp(6, Timestamp.valueOf(c.getCreatedAt()));

            pstmt.executeUpdate();
        }
    }

    // Βρες επικοινωνία με βάση το ID
    public Communication findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM communications WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCommunication(rs);
            }
        }
        return null;
    }

    // Βρες όλες τις επικοινωνίες για έναν πελάτη
    public List<Communication> findByCustomerId(UUID customerId) throws SQLException {
        List<Communication> list = new ArrayList<>();
        String sql = "SELECT * FROM communications WHERE customer_id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCommunication(rs));
            }
        }
        return list;
    }

    // Ενημέρωση επικοινωνίας
    public void updateCommunication(Communication c) throws SQLException {
        String sql = "UPDATE communications SET title=?, description=?, status=? WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getSubject());
            pstmt.setString(2, c.getMessage());
            pstmt.setString(3, c.getType().name());
            pstmt.setString(4, c.getCommunicationId().toString());

            pstmt.executeUpdate();
        }
    }

    // Διαγραφή επικοινωνίας
    public void deleteCommunication(UUID id) throws SQLException {
        String sql = "DELETE FROM communications WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        }
    }

    // Φόρτωση όλων των επικοινωνιών
    public List<Communication> findAll() throws SQLException {
        List<Communication> list = new ArrayList<>();
        String sql = "SELECT * FROM communications";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCommunication(rs));
            }
        }
        return list;
    }

    // Χαρτογράφηση ResultSet σε Communication
    private Communication mapResultSetToCommunication(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID customerId = UUID.fromString(rs.getString("customer_id"));
        String title = rs.getString("title");
        String description = rs.getString("description");
        CommunicationType type = CommunicationType.valueOf(rs.getString("status"));
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        // Δημιουργούμε ένα αντικείμενο Communication
        Communication c = new Communication(customerId, title, description, type) {
            // Αφήνουμε abstract όπως είναι, δεν χρειαζόμαστε subclass
        };
        c.setCommunicationId(id);
        c.setCreatedAt(createdAt);

        return c;
    }
}
