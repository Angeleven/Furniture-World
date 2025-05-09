import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;

    public LoginFrame() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));
        contentPanel.setBackground(new Color(240, 248, 255));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        formPanel.setOpaque(false);
        Font font = new Font("Segoe UI", Font.PLAIN, 16);

        JLabel usernameLabel = new JLabel("Email:");
        usernameLabel.setFont(font);
        usernameField = new JTextField(15);
        usernameField.setFont(font);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(font);
        passwordField = new JPasswordField(15);
        passwordField.setFont(font);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 30));
        buttonPanel.setOpaque(false);

        loginButton = new JButton("Login");
        loginButton.setFont(font);
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        signUpButton = new JButton("Sign Up");
        signUpButton.setFont(font);
        signUpButton.setBackground(new Color(33, 150, 243));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel);

        loginButton.addActionListener(e -> login());
        signUpButton.addActionListener(e -> new SignUpForm());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                // Replace this with your main app screen
                dispose();
                new FurnitureWorld();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading user data.");
            ex.printStackTrace();
        }
    }
}
