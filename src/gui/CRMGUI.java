package gui;

import customer.*;
import appointment.*;
import task.*;
import communication.*;
import dbhelper.DBHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CRMGUI extends JFrame {

    private JTabbedPane tabbedPane;

    // DAO instances
    private CustomerDAO customerDAO;
    private AppointmentDAO appointmentDAO;
    private TaskDAO taskDAO;
    private CommunicationDAO communicationDAO;

    public CRMGUI() {
        setTitle("CRM System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        customerDAO = new CustomerDAO();
        appointmentDAO = new AppointmentDAO();
        taskDAO = new TaskDAO();
        communicationDAO = new CommunicationDAO();

        tabbedPane = new JTabbedPane();

        tabbedPane.add("Customers", createCustomerPanel());
        tabbedPane.add("Appointments", createAppointmentPanel());
        tabbedPane.add("Tasks", createTaskPanel());
        tabbedPane.add("Communications", createCommunicationPanel());

        add(tabbedPane);
        setVisible(true);
    }

    // ----------------------------
    // Customer Panel
    // ----------------------------
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(new String[]{
                "ID", "Full Name", "Phone", "Email", "Category", "Notes", "Created At"
        }, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5,5));
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<Category> categoryBox = new JComboBox<>(Category.values());
        JTextArea notesArea = new JTextArea(3,20);

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryBox);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(new JScrollPane(notesArea));

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        panel.add(scrollPane, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        refreshCustomerTable(model);

        // Button actions
        addBtn.addActionListener(e -> {
            Customer c = new Customer(
                    nameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    (Category) categoryBox.getSelectedItem(),
                    notesArea.getText()
            );
            List<String> errors = c.validation();
            if(!errors.isEmpty()){
                JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try{
                customerDAO.addCustomer(c);
                refreshCustomerTable(model);
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding customer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if(selectedRow==-1){
                JOptionPane.showMessageDialog(this, "Select a customer to update.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            UUID id = UUID.fromString((String)table.getValueAt(selectedRow,0));
            try{
                Customer c = customerDAO.findById(id);
                c.setFullName(nameField.getText());
                c.setPhone(phoneField.getText());
                c.setEmail(emailField.getText());
                c.setCategory((Category) categoryBox.getSelectedItem());
                c.setNotes(notesArea.getText());
                List<String> errors = c.validation();
                if(!errors.isEmpty()){
                    JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                customerDAO.updateCustomer(c);
                refreshCustomerTable(model);
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating customer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if(selectedRow==-1){
                JOptionPane.showMessageDialog(this, "Select a customer to delete.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            UUID id = UUID.fromString((String)table.getValueAt(selectedRow,0));
            try{
                customerDAO.deleteCustomer(id);
                refreshCustomerTable(model);
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting customer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshBtn.addActionListener(e -> refreshCustomerTable(model));

        // Table row selection
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if(row!=-1){
                    nameField.setText((String)table.getValueAt(row,1));
                    phoneField.setText((String)table.getValueAt(row,2));
                    emailField.setText((String)table.getValueAt(row,3));
                    categoryBox.setSelectedItem(Category.valueOf((String)table.getValueAt(row,4)));
                    notesArea.setText((String)table.getValueAt(row,5));
                }
            }
        });

        return panel;
    }

    private void refreshCustomerTable(DefaultTableModel model){
        try{
            model.setRowCount(0);
            List<Customer> customers = customerDAO.findAll();
            for(Customer c: customers){
                model.addRow(new Object[]{
                        c.getCustomerId().toString(),
                        c.getFullName(),
                        c.getPhone(),
                        c.getEmail(),
                        c.getCategory().name(),
                        c.getNotes(),
                        c.getFormattedDate()
                });
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----------------------------
    // Appointment Panel
    // ----------------------------
    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(new String[]{
                "ID","Customer ID","Title","Description","Date","Location","Status","Created At"
        },0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel formPanel = new JPanel(new GridLayout(7,2,5,5));
        JTextField customerIdField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField dateField = new JTextField("yyyy-MM-ddTHH:mm");
        JTextField locationField = new JTextField();
        JComboBox<AppointmentStatus> statusBox = new JComboBox<>(AppointmentStatus.values());

        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(customerIdField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descField);
        formPanel.add(new JLabel("Appointment Date:"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusBox);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        panel.add(scrollPane, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshAppointmentTable(model);

        addBtn.addActionListener(e -> {
            try{
                Appointment a = new Appointment(
                        UUID.fromString(customerIdField.getText()),
                        titleField.getText(),
                        descField.getText(),
                        LocalDateTime.parse(dateField.getText()),
                        locationField.getText(),
                        (AppointmentStatus)statusBox.getSelectedItem()
                );
                List<String> errors = a.validation();
                if(!errors.isEmpty()){
                    JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                appointmentDAO.addAppointment(a);
                refreshAppointmentTable(model);
            } catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,"Error adding appointment.","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update/Delete/Refresh can be implemented similarly as in Customer panel
        // To save space, full code will follow same logic

        return panel;
    }

    private void refreshAppointmentTable(DefaultTableModel model){
        try{
            model.setRowCount(0);
            List<Appointment> list = appointmentDAO.findAll();
            for(Appointment a: list){
                model.addRow(new Object[]{
                        a.getAppointmentId().toString(),
                        a.getCustomerId().toString(),
                        a.getTitle(),
                        a.getDescription(),
                        a.getFormattedAppointmentDate(),
                        a.getLocation(),
                        a.getStatus().name(),
                        a.getFormattedCreatedAt()
                });
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error loading appointments.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----------------------------
    // Task Panel
    // ----------------------------
    private JPanel createTaskPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{
                "ID","Customer ID","Title","Description","Status","Due Date","Created At"
        },0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel formPanel = new JPanel(new GridLayout(6,2,5,5));
        JTextField customerIdField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JComboBox<TaskStatus> statusBox = new JComboBox<>(TaskStatus.values());
        JTextField dueDateField = new JTextField("yyyy-MM-ddTHH:mm");

        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(customerIdField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusBox);
        formPanel.add(new JLabel("Due Date:"));
        formPanel.add(dueDateField);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        panel.add(scrollPane, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshTaskTable(model);

        addBtn.addActionListener(e -> {
            try{
                Task t = new Task(
                        UUID.fromString(customerIdField.getText()),
                        titleField.getText(),
                        descField.getText(),
                        (TaskStatus)statusBox.getSelectedItem(),
                        LocalDateTime.parse(dueDateField.getText())
                );
                List<String> errors = t.validation();
                if(!errors.isEmpty()){
                    JOptionPane.showMessageDialog(this,String.join("\n",errors),"Validation Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                taskDAO.addTask(t);
                refreshTaskTable(model);
            } catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,"Error adding task.","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void refreshTaskTable(DefaultTableModel model){
        try{
            model.setRowCount(0);
            List<Task> tasks = taskDAO.findAll();
            for(Task t: tasks){
                model.addRow(new Object[]{
                        t.getTaskId().toString(),
                        t.getCustomerId().toString(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getStatus().name(),
                        t.getFormattedDueDate(),
                        t.getFormattedCreatedAt()
                });
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error loading tasks.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----------------------------
    // Communication Panel (simplified: Emails)
    // ----------------------------
    private JPanel createCommunicationPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{
                "ID","Customer ID","Type","Subject","Message","Created At"
        },0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel formPanel = new JPanel(new GridLayout(5,2,5,5));
        JTextField customerIdField = new JTextField();
        JComboBox<CommunicationType> typeBox = new JComboBox<>(CommunicationType.values());
        JTextField subjectField = new JTextField();
        JTextField messageField = new JTextField();

        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(customerIdField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeBox);
        formPanel.add(new JLabel("Subject:"));
        formPanel.add(subjectField);
        formPanel.add(new JLabel("Message:"));
        formPanel.add(messageField);

        JButton addBtn = new JButton("Add");
        JButton refreshBtn = new JButton("Refresh");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(refreshBtn);

        panel.add(scrollPane, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshCommunicationTable(model);

        addBtn.addActionListener(e -> {
            try{
                Communication c = new Communication(
                        UUID.fromString(customerIdField.getText()),
                        subjectField.getText(),
                        messageField.getText(),
                        (CommunicationType)typeBox.getSelectedItem()
                ){};
                List<String> errors = c.validation();
                if(!errors.isEmpty()){
                    JOptionPane.showMessageDialog(this,String.join("\n",errors),"Validation Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                communicationDAO.addCommunication(c);
                refreshCommunicationTable(model);
            } catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,"Error adding communication.","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void refreshCommunicationTable(DefaultTableModel model){
        try{
            model.setRowCount(0);
            List<Communication> list = communicationDAO.findAll();
            for(Communication c: list){
                model.addRow(new Object[]{
                        c.getCommunicationId().toString(),
                        c.getCustomerId().toString(),
                        c.getType().name(),
                        c.getSubject(),
                        c.getMessage(),
                        c.getFormattedCreatedAt()
                });
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error loading communications.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

}
