
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Main {

    static String currentView = null;

    static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/cargo_system",
                "root",
                "dbms"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static void dashboard() {

        JFrame frame = new JFrame("Cargo Management System");
        frame.setSize(1000, 550);
        frame.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        // 🔝 TOP BUTTONS
        JButton showCustomer = new JButton("Customer");
        JButton showCargo = new JButton("Cargo");

        showCustomer.setBackground(new Color(0,153,76));
        showCargo.setBackground(new Color(255,140,0));

        showCustomer.setForeground(Color.WHITE);
        showCargo.setForeground(Color.WHITE);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(30,30,60));
        topPanel.add(showCustomer);
        topPanel.add(showCargo);

        // 🔽 BOTTOM BUTTONS
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");

        add.setBackground(new Color(0,120,215));
        update.setBackground(new Color(138,43,226));
        delete.setBackground(new Color(220,20,60));

        JButton[] bottomButtons = {add, update, delete};

        for(JButton b : bottomButtons){
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(20,20,40));

        for(JButton b : bottomButtons) bottomPanel.add(b);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // SHOW CUSTOMER
        showCustomer.addActionListener(e -> {
            currentView = "customer";

            model.setColumnIdentifiers(new String[]{"ID","Name","Phone","Address"});
            model.setRowCount(0);

            try {
                Connection c = getConnection();
                ResultSet rs = c.createStatement().executeQuery("SELECT * FROM Customer");

                while(rs.next()){
                    model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                    });
                }
            } catch(Exception ex){ ex.printStackTrace(); }
        });

        // SHOW CARGO
        showCargo.addActionListener(e -> {
            currentView = "cargo";

            model.setColumnIdentifiers(new String[]{
                "ID","Description","Weight","Source","Destination","Customer ID"
            });
            model.setRowCount(0);

            try {
                Connection c = getConnection();
                ResultSet rs = c.createStatement().executeQuery("SELECT * FROM Cargo");

                while(rs.next()){
                    model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(6)
                    });
                }
            } catch(Exception ex){ ex.printStackTrace(); }
        });

        // ADD
        add.addActionListener(e -> {

            if(currentView == null){
                JOptionPane.showMessageDialog(frame, "Select Customer or Cargo first!");
                return;
            }

            try {
                Connection c = getConnection();

                if(currentView.equals("customer")) {

                    JTextField id = new JTextField();
                    JTextField name = new JTextField();
                    JTextField phone = new JTextField();
                    JTextField address = new JTextField();

                    JPanel form = new JPanel(new GridLayout(4,2,15,15));
                    form.setPreferredSize(new Dimension(400,200));

                    form.add(new JLabel("Customer ID")); form.add(id);
                    form.add(new JLabel("Name")); form.add(name);
                    form.add(new JLabel("Phone")); form.add(phone);
                    form.add(new JLabel("Address")); form.add(address);

                    if(JOptionPane.showConfirmDialog(frame, form, "Add Customer",
                            JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){

                        PreparedStatement ps = c.prepareStatement(
                                "INSERT INTO Customer VALUES (?,?,?,?)"
                        );

                        ps.setInt(1, Integer.parseInt(id.getText()));
                        ps.setString(2, name.getText());
                        ps.setString(3, phone.getText());
                        ps.setString(4, address.getText());
                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(frame,"Customer Added!");
                        showCustomer.doClick();
                    }

                } else {

                    JTextField id = new JTextField();
                    JTextField desc = new JTextField();
                    JTextField weight = new JTextField();
                    JTextField source = new JTextField();
                    JTextField dest = new JTextField();
                    JTextField custId = new JTextField();

                    JPanel form = new JPanel(new GridLayout(6,2,15,15));
                    form.setPreferredSize(new Dimension(450,250));

                    form.add(new JLabel("Cargo ID")); form.add(id);
                    form.add(new JLabel("Description")); form.add(desc);
                    form.add(new JLabel("Weight")); form.add(weight);
                    form.add(new JLabel("Source")); form.add(source);
                    form.add(new JLabel("Destination")); form.add(dest);
                    form.add(new JLabel("Customer ID")); form.add(custId);

                    if(JOptionPane.showConfirmDialog(frame, form, "Add Cargo",
                            JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){

                        PreparedStatement ps = c.prepareStatement(
                                "INSERT INTO Cargo VALUES (?,?,?,?,?,?)"
                        );

                        ps.setInt(1, Integer.parseInt(id.getText()));
                        ps.setString(2, desc.getText());
                        ps.setInt(3, Integer.parseInt(weight.getText()));
                        ps.setString(4, source.getText());
                        ps.setString(5, dest.getText());
                        ps.setInt(6, Integer.parseInt(custId.getText()));
                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(frame,"Cargo Added!");
                        showCargo.doClick();
                    }
                }

            } catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,"Error: "+ex.getMessage());
            }
        });

        // UPDATE (FIXED)
        update.addActionListener(e -> {

            if(currentView == null){
                JOptionPane.showMessageDialog(frame, "Select Customer or Cargo first!");
                return;
            }

            try {
                Connection c = getConnection();

                if(currentView.equals("customer")) {

                    String id = JOptionPane.showInputDialog("Customer ID");

                    PreparedStatement ps = c.prepareStatement(
                            "UPDATE Customer SET name=?, phone=?, address=? WHERE customer_id=?"
                    );

                    ps.setString(1, JOptionPane.showInputDialog("Name"));
                    ps.setString(2, JOptionPane.showInputDialog("Phone"));
                    ps.setString(3, JOptionPane.showInputDialog("Address"));
                    ps.setInt(4, Integer.parseInt(id));

                    int rows = ps.executeUpdate();

                    if(rows > 0){
                        JOptionPane.showMessageDialog(frame,"Updated!");
                        showCustomer.doClick();
                    } else {
                        JOptionPane.showMessageDialog(frame,"ID not found!");
                    }

                } else {

                    String id = JOptionPane.showInputDialog("Cargo ID");

                    PreparedStatement ps = c.prepareStatement(
                            "UPDATE Cargo SET description=?, weight=?, source=?, destination=?, customer_id=? WHERE cargo_id=?"
                    );

                    ps.setString(1, JOptionPane.showInputDialog("Description"));
                    ps.setInt(2, Integer.parseInt(JOptionPane.showInputDialog("Weight")));
                    ps.setString(3, JOptionPane.showInputDialog("Source"));
                    ps.setString(4, JOptionPane.showInputDialog("Destination"));
                    ps.setInt(5, Integer.parseInt(JOptionPane.showInputDialog("Customer ID")));
                    ps.setInt(6, Integer.parseInt(id));

                    int rows = ps.executeUpdate();

                    if(rows > 0){
                        JOptionPane.showMessageDialog(frame,"Updated!");
                        showCargo.doClick();
                    } else {
                        JOptionPane.showMessageDialog(frame,"ID not found!");
                    }
                }

            } catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,"Error: "+ex.getMessage());
            }
        });

        // DELETE
        delete.addActionListener(e -> {

            if(currentView == null){
                JOptionPane.showMessageDialog(frame, "Select table first!");
                return;
            }

            try {
                Connection c = getConnection();

                if(currentView.equals("customer")) {

                    String id = JOptionPane.showInputDialog("Customer ID");

                    PreparedStatement ps = c.prepareStatement(
                            "DELETE FROM Customer WHERE customer_id=?"
                    );

                    ps.setInt(1, Integer.parseInt(id));
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(frame,"Deleted!");
                    showCustomer.doClick();

                } else {

                    String id = JOptionPane.showInputDialog("Cargo ID");

                    PreparedStatement ps = c.prepareStatement(
                            "DELETE FROM Cargo WHERE cargo_id=?"
                    );

                    ps.setInt(1, Integer.parseInt(id));
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(frame,"Deleted!");
                    showCargo.doClick();
                }

            } catch(Exception ex){
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showCustomer.doClick(); // auto load
    }

    public static void main(String[] args) {
        dashboard();
    }
}
