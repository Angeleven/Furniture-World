import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class CustomerRegistrationForm extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField addressField;
    private JTextField cityField;
    private JTextField postalCodeField;
    private JTextField phoneNumberField;
    private JTextField emailField;
    private JTextArea noteArea; // Adding note field
    private JButton registerButton;

    public CustomerRegistrationForm() {
        setTitle("Customer Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a panel for the content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create a panel for the form
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10)); // Increased row count to accommodate the note field
        formPanel.setBackground(Color.WHITE);

        // Define font for the labels and fields
        Font font = new Font("Helvetica", Font.PLAIN, 14);

        // Create components for bio details
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(font);
        firstNameField = new JTextField();
        firstNameField.setFont(font);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(font);
        lastNameField = new JTextField();
        lastNameField.setFont(font);

        // Create components for address
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(font);
        addressField = new JTextField();
        addressField.setFont(font);

        JLabel cityLabel = new JLabel("City:");
        cityLabel.setFont(font);
        cityField = new JTextField();
        cityField.setFont(font);

        JLabel postalCodeLabel = new JLabel("Postal Code:");
        postalCodeLabel.setFont(font);
        postalCodeField = new JTextField();
        postalCodeField.setFont(font);

        // Create components for contact info
        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        phoneNumberLabel.setFont(font);
        phoneNumberField = new JTextField();
        phoneNumberField.setFont(font);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(font);
        emailField = new JTextField();
        emailField.setFont(font);

        // Create component for note
        JLabel noteLabel = new JLabel("Note:");
        noteLabel.setFont(font);
        noteArea = new JTextArea();
        noteArea.setFont(font);
        noteArea.setRows(3); // Set the number of visible rows
        noteArea.setLineWrap(true); // Enable line wrapping
        JScrollPane noteScrollPane = new JScrollPane(noteArea); // Add a scroll pane for long notes

        // Add components to form panel
        formPanel.add(firstNameLabel);
        formPanel.add(firstNameField);
        formPanel.add(lastNameLabel);
        formPanel.add(lastNameField);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        formPanel.add(cityLabel);
        formPanel.add(cityField);
        formPanel.add(postalCodeLabel);
        formPanel.add(postalCodeField);
        formPanel.add(phoneNumberLabel);
        formPanel.add(phoneNumberField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(noteLabel);
        formPanel.add(noteScrollPane); // Add the note area with scroll pane

        // Create a panel for the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(52, 152, 219)); // Blue color
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(font);
        buttonPanel.add(registerButton);

        // Add form panel and button panel to content panel
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add content panel to the frame
        add(contentPanel);

        // Add action listener to register button
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerCustomer();
            }
        });
        pack();
        setSize(700, 500); // Increased height to accommodate the note field
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    private void registerCustomer() {
        // Retrieve field values
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String address = addressField.getText();
        String city = cityField.getText();
        String postalCode = postalCodeField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();
        String note = noteArea.getText(); // Retrieve note text

        // Perform customer registration process (e.g., send data to server, store in database, etc.)
        // For demonstration, we'll simply print the collected information
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Address: " + address);
        System.out.println("City: " + city);
        System.out.println("Postal Code: " + postalCode);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Email: " + email);
        System.out.println("Note: " + note); // Print the note

        // Show success message
        JOptionPane.showMessageDialog(this, "Customer registered successfully!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CustomerRegistrationForm();
            }
        });
    }
}