import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class FurnitureWorld extends JFrame {

    public FurnitureWorld() {
        // Set the main frame title and layout
        setTitle("Welcome to Furniture World");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 600));

        // Set background color for the main window
        getContentPane().setBackground(new Color(250, 250, 250));

        // Top Panel for the welcome message
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(52, 152, 219));
        JLabel welcomeLabel = new JLabel("Welcome to Furniture World");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        topPanel.add(welcomeLabel);
        add(topPanel, BorderLayout.NORTH);

        // Right Panel (navigation menu)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(245, 245, 245));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        rightPanel.setPreferredSize(new Dimension(250, getHeight()));

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(Box.createVerticalStrut(20));

        // Add buttons with meaningful names for each functionality
        addButton("User Login", rightPanel, LoginFrame.class);
        addButton("Register as New Customer", rightPanel, CustomerRegistrationForm.class);
        addButton("Add New Chair", rightPanel, ChairRenderer.class);
        addButton("Add New Table", rightPanel, BoxTable.class);
        addButton("View Round Table", rightPanel, RoundTable.class);
        addButton("Add Stool", rightPanel, StoolRenderer.class);
        addButton("View Dining Table", rightPanel, TableRenderer.class);
        addButton("Designer Account Sign Up", rightPanel, SignUpForm.class);

        rightPanel.add(Box.createVerticalStrut(20));
        add(rightPanel, BorderLayout.EAST);

        // Adjust the frame size to fit the content
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Helper method to add a button with relevant text and associated action
    private void addButton(String buttonText, JPanel panel, Class<?> clazz) {
        JButton button = new JButton(buttonText);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorder(new RoundedBorder(15));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Corrected here
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect on buttons for a more interactive feel
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });

        // Action listener to create a new frame and show the corresponding UI
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JFrame frame = new JFrame(clazz.getSimpleName());
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setSize(800, 600);
                    frame.setLocationRelativeTo(null);
                    JComponent component = (JComponent) clazz.getDeclaredConstructor().newInstance();
                    frame.getContentPane().add(component);
                    frame.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Add button to the panel and add space between buttons
        panel.add(button);
        panel.add(Box.createVerticalStrut(15));
    }

    // Custom border class to round button corners
    static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 2, radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.BLACK);
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FurnitureWorld());
    }
}
