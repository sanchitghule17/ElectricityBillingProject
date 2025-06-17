import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class ElectricityBillApp extends JFrame {
    JTextArea area = new JTextArea();
    JLabel summary = new JLabel("Summary:");
    static final String USER = "admin", PASS = "1234";

    public ElectricityBillApp() {
        setTitle("Electricity Billing System");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(area);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JPanel top = new JPanel();
        JButton load = new JButton("Load CSV"), save = new JButton("Save Output");
        load.addActionListener(e -> loadCSV());
        save.addActionListener(e -> saveOutput());
        top.add(load); top.add(save);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(summary, BorderLayout.SOUTH);
    }

    void loadCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            processFile(fc.getSelectedFile());
    }

    void processFile(File file) {
        try (Scanner sc = new Scanner(file)) {
            area.setText("");
            int count = 0; double revenue = 0;
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(",");
                if (p.length < 6 || p[0].toLowerCase().contains("name")) continue;
                try {
                    int units = Integer.parseInt(p[3].trim());
                    double amt = calc(units); count++; revenue += amt;
                    area.append("Name: " + p[0] + "\nMeter: " + p[1] + "\nAddress: " + p[2] +
                            "\nPhone: " + p[4] + "\nUnits: " + units +
                            String.format("\nAmount: ₹%.2f\n", amt) +
                            "------------------------------\n");
                } catch (Exception ex) {
                    area.append("Skipping invalid line: " + String.join(",", p) + "\n");
                }
            }
            summary.setText("Summary: Customers = " + count + ", Total = ₹" + String.format("%.2f", revenue));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void saveOutput() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                fw.write(area.getText());
                JOptionPane.showMessageDialog(this, "Saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Save error.");
            }
    }

    static double calc(int u) {
        if (u <= 100) return u * 1.5;
        if (u <= 300) return 100 * 1.5 + (u - 100) * 2.5;
        if (u <= 500) return 100 * 1.5 + 200 * 2.5 + (u - 300) * 4;
        return 100 * 1.5 + 200 * 2.5 + 200 * 4 + (u - 500) * 6;
    }

    public static void main(String[] args) {
        JTextField u = new JTextField(); JPasswordField p = new JPasswordField();
        if (JOptionPane.showConfirmDialog(null, new Object[]{"Username:", u, "Password:", p},
                "Login", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION &&
                u.getText().equals(USER) && new String(p.getPassword()).equals(PASS)) {
            SwingUtilities.invokeLater(() -> new ElectricityBillApp().setVisible(true));
        } else System.exit(0);
    }
}