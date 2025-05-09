import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SignUpForm extends JFrame {
    private JTextField firstNameField, lastNameField, addressField, cityField, postalCodeField, phoneField, emailField;
    private JPasswordField passwordField;
    private JTextArea noteArea;
    private JButton registerButton;

    public SignUpForm() {
        setTitle("Customer Registration Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 250));

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 12, 12));
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        formPanel.setBackground(new Color(245, 245, 250));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        firstNameField = createLabeledField("First Name", formPanel, labelFont, inputFont);
        lastNameField = createLabeledField("Last Name", formPanel, labelFont, inputFont);
        addressField = createLabeledField("Address", formPanel, labelFont, inputFont);
        cityField = createLabeledField("City", formPanel, labelFont, inputFont);
        postalCodeField = createLabeledField("Postal Code", formPanel, labelFont, inputFont);
        phoneField = createLabeledField("Phone Number", formPanel, labelFont, inputFont);
        emailField = createLabeledField("Email", formPanel, labelFont, inputFont);

        // Add Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordField = new JPasswordField();
        passwordField.setFont(inputFont);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        // Note field
        JLabel noteLabel = new JLabel("Note:");
        noteLabel.setFont(labelFont);
        noteArea = new JTextArea(3, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setFont(inputFont);
        noteArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        JScrollPane noteScroll = new JScrollPane(noteArea);
        formPanel.add(noteLabel);
        formPanel.add(noteScroll);

        add(formPanel, BorderLayout.CENTER);

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerButton.setBackground(new Color(33, 150, 243));
        registerButton.setForeground(Color.WHITE);
        registerButton.setPreferredSize(new Dimension(140, 40));
        registerButton.addActionListener(e -> register());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 250));
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JTextField createLabeledField(String label, JPanel panel, Font labelFont, Font inputFont) {
        JLabel jLabel = new JLabel(label + ":");
        jLabel.setFont(labelFont);
        JTextField field = new JTextField();
        field.setFont(inputFont);
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        panel.add(jLabel);
        panel.add(field);
        return field;
    }

    private void register() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password are required.");
            return;
        }

        try (FileWriter fw = new FileWriter("users.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(email + "," + password);
            bw.newLine();
            JOptionPane.showMessageDialog(this, "Registration successful!");
            dispose();
            new LoginFrame();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
