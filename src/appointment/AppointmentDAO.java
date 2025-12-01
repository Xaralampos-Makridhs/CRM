import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppointmentDAO {

    public void addAppointment(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointments(id, customer_id, title, description, appointment_date, location, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, a.getAppointmentId().toString());
            pstmt.setString(2, a.getCustomerId().toString().replace("-", ""));
            pstmt.setString(3, a.getTitle());
            pstmt.setString(4, a.getDescription());
            pstmt.setTimestamp(5, a.getAppointmentDate() == null ? null : Timestamp.valueOf(a.getAppointmentDate()));
            pstmt.setString(6, a.getLocation());
            pstmt.setString(7, a.getStatus().name());
            pstmt.setTimestamp(8, Timestamp.valueOf(a.getCreatedAt()));

            pstmt.executeUpdate();
        }
    }

    public Appointment findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAppointment(rs);
            }
        }
        return null;
    }

    public List<Appointment> findByCustomerId(UUID customerId) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE customer_id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId.toString().replace("-", ""));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs));
            }
        }
        return list;
    }

    public void updateAppointment(Appointment a) throws SQLException {
        String sql = "UPDATE appointments SET title=?, description=?, appointment_date=?, location=?, status=? WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, a.getTitle());
            pstmt.setString(2, a.getDescription());
            pstmt.setTimestamp(3, a.getAppointmentDate() == null ? null : Timestamp.valueOf(a.getAppointmentDate()));
            pstmt.setString(4, a.getLocation());
            pstmt.setString(5, a.getStatus().name());
            pstmt.setString(6, a.getAppointmentId().toString());

            pstmt.executeUpdate();
        }
    }

    public void deleteAppointment(UUID id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        }
    }

    public List<Appointment> findAll() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs));
            }
        }
        return list;
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID customerId = UUID.fromString(rs.getString("customer_id"));
        String title = rs.getString("title");
        String description = rs.getString("description");
        Timestamp appTs = rs.getTimestamp("appointment_date");
        LocalDateTime appDate = appTs != null ? appTs.toLocalDateTime() : null;
        String location = rs.getString("location");
        AppointmentStatus status = AppointmentStatus.valueOf(rs.getString("status"));
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        Appointment a = new Appointment(customerId, title, description, appDate, location, status);
        a.setCreatedAt(createdAt);
        a.setAppointmentId(id);
        return a;
    }
}
