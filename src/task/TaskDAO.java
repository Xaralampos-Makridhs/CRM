import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskDAO {

    public void addTask(Task task) throws SQLException {
        String sql = "INSERT INTO tasks(id, customer_id, title, description, status, due_date, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTaskId().toString());
            pstmt.setString(2, task.getCustomerId().toString().replace("-", ""));
            pstmt.setString(3, task.getTitle());
            pstmt.setString(4, task.getDescription());
            pstmt.setString(5, task.getStatus().name());
            pstmt.setTimestamp(6, task.getDueDate() == null ? null : Timestamp.valueOf(task.getDueDate()));
            pstmt.setTimestamp(7, Timestamp.valueOf(task.getCreatedAt()));

            pstmt.executeUpdate();
        }
    }

    public Task findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTask(rs);
            }
        }
        return null;
    }

    public List<Task> findByCustomerId(UUID customerId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE customer_id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId.toString().replace("-", ""));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
        }
        return tasks;
    }

    public void updateTask(Task task) throws SQLException {
        String sql = "UPDATE tasks SET title=?, description=?, status=?, due_date=? WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getStatus().name());
            pstmt.setTimestamp(4, task.getDueDate() == null ? null : Timestamp.valueOf(task.getDueDate()));
            pstmt.setString(5, task.getTaskId().toString());

            pstmt.executeUpdate();
        }
    }

    public void deleteTask(UUID id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        }
    }

    public List<Task> findAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
        }
        return tasks;
    }

    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        UUID taskId = UUID.fromString(rs.getString("id"));
        UUID customerId = UUID.fromString(rs.getString("customer_id"));
        String title = rs.getString("title");
        String description = rs.getString("description");
        TaskStatus status = TaskStatus.valueOf(rs.getString("status"));
        Timestamp dueTs = rs.getTimestamp("due_date");
        LocalDateTime dueDate = dueTs != null ? dueTs.toLocalDateTime() : null;
        Timestamp createdTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdTs.toLocalDateTime();

        Task task = new Task(customerId, title, description, status, dueDate);
        task.setTaskId(taskId);
        task.setCreatedAt(createdAt);
        return task;
    }
}
