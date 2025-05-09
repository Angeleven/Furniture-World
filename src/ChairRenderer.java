import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.*;
import java.awt.event.*;

public class ChairRenderer extends GLJPanel implements GLEventListener, MouseMotionListener {
    private FPSAnimator animator;
    private int lastX, lastY;
    private float rotateX = 0.0f;
    private float rotateY = 0.0f;
    private JButton chairColorButton;
    private JButton adjustSeatButton; // Button to adjust seat measurements
    private JButton adjustBackrestButton; // Button to adjust backrest measurements
    private JButton scaleUpButton;
    private JButton scaleDownButton;
    private JButton scaleXUpButton;
    private JButton scaleXDownButton;
    private JButton scaleYUpButton;
    private JButton scaleYDownButton;
    private JButton scaleZUpButton;
    private JButton scaleZDownButton;
    private JTextField widthField;
    private JTextField heightField;
    private JTextField lengthField;
    private JTextField backrestHeightField;
    private JTextField backrestWidthField;
    private JTextField backrestDepthField;
    private Color chairColor = Color.yellow;
    private Color backgroundColor = Color.white;
    private float[] normalizedChairColor;
    private float seatWidth = 4.0f;
    private float seatHeight = 2.0f;
    private float seatLength = 5.0f;
    private float backrestHeight = 7.0f; // Default height of the backrest
    private float backrestWidth = 4.0f; // Default width of the backrest
    private float backrestDepth = 0.5f; // Default depth of the backrest
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float scaleZ = 1.0f;
    private JPanel buttonPanel;

    public ChairRenderer() {
        super(new GLCapabilities(GLProfile.getDefault()));
        setBackground(Color.white);
        setLayout(new BorderLayout()); // Set layout to BorderLayout
        buttonPanel = new JPanel(new GridLayout(0, 1)); // Button panel on left side
        add(buttonPanel, BorderLayout.WEST); // Add button panel to west

        addGLEventListener(this);
        addMouseMotionListener(this);
        animator = new FPSAnimator(this, 60);
        animator.start();
        initColorPickers(buttonPanel);
        normalizeColors();
        initAdjustSeatForm(buttonPanel);
        initAdjustBackrestForm(buttonPanel);
        initScalingButtons();
    }

    private void initScalingButtons() {
        scaleUpButton = new JButton("Scale Up");
        scaleUpButton.addActionListener(e -> {
            scaleX += 0.1f;
            scaleY += 0.1f;
            scaleZ += 0.1f;
            repaint();
        });

        scaleDownButton = new JButton("Scale Down");
        scaleDownButton.addActionListener(e -> {
            scaleX -= 0.1f;
            scaleY -= 0.1f;
            scaleZ -= 0.1f;
            repaint();
        });

        scaleXUpButton = new JButton("Scale X Up");
        scaleXUpButton.addActionListener(e -> {
            scaleX += 0.1f;
            repaint();
        });

        scaleXDownButton = new JButton("Scale X Down");
        scaleXDownButton.addActionListener(e -> {
            scaleX -= 0.1f;
            repaint();
        });

        scaleYUpButton = new JButton("Scale Y Up");
        scaleYUpButton.addActionListener(e -> {
            scaleY += 0.1f;
            repaint();
        });

        scaleYDownButton = new JButton("Scale Y Down");
        scaleYDownButton.addActionListener(e -> {
            scaleY -= 0.1f;
            repaint();
        });

        scaleZUpButton = new JButton("Scale Z Up");
        scaleZUpButton.addActionListener(e -> {
            scaleZ += 0.1f;
            repaint();
        });

        scaleZDownButton = new JButton("Scale Z Down");
        scaleZDownButton.addActionListener(e -> {
            scaleZ -= 0.1f;
            repaint();
        });

        JPanel scalingPanel = new JPanel();
        scalingPanel.setLayout(new GridLayout(4, 2));
        scalingPanel.add(scaleUpButton);
        scalingPanel.add(scaleDownButton);
        scalingPanel.add(scaleXUpButton);
        scalingPanel.add(scaleXDownButton);
        scalingPanel.add(scaleYUpButton);
        scalingPanel.add(scaleYDownButton);
        scalingPanel.add(scaleZUpButton);
        scalingPanel.add(scaleZDownButton);
        buttonPanel.add(scalingPanel);
    }

    private void initColorPickers(JPanel buttonPanel) {
        chairColorButton = new JButton("Change Chair Color");
        chairColorButton.addActionListener(this::changeChairColor);
        buttonPanel.add(chairColorButton);
    }

    private void changeChairColor(ActionEvent e) {
        Color defaultColor = chairColor != null ? chairColor : Color.RED;
        Color newChairColor = JColorChooser.showDialog(null, "Choose Chair Color", defaultColor);
        if (newChairColor != null) {
            chairColor = newChairColor;
            normalizeColors();
            repaint();
        }
    }

    private void normalizeColors() {
        normalizedChairColor = normalizeColor(chairColor);
    }

    private float[] normalizeColor(Color color) {
        return new float[] { color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f };
    }

    private void initAdjustSeatForm(JPanel buttonPanel) {
        JLabel widthLabel = new JLabel("Width:");
        JLabel heightLabel = new JLabel("Height:");
        JLabel lengthLabel = new JLabel("Length:");
        widthField = new JTextField(5);
        heightField = new JTextField(5);
        lengthField = new JTextField(5);
        adjustSeatButton = new JButton("Adjust Seat");
        JPanel formPanel = new JPanel(new GridLayout(0, 2));
        formPanel.add(widthLabel);
        formPanel.add(widthField);
        formPanel.add(heightLabel);
        formPanel.add(heightField);
        formPanel.add(lengthLabel);
        formPanel.add(lengthField);
        formPanel.add(adjustSeatButton);
        buttonPanel.add(formPanel);
        adjustSeatButton.addActionListener(this::submitForm);
    }

    private void initAdjustBackrestForm(JPanel buttonPanel) {
        JLabel backrestHeightLabel = new JLabel("Backrest Height:");
        JLabel backrestWidthLabel = new JLabel("Backrest Width:");
        JLabel backrestDepthLabel = new JLabel("Backrest Depth:");
        backrestHeightField = new JTextField(5);
        backrestWidthField = new JTextField(5);
        backrestDepthField = new JTextField(5);
        adjustBackrestButton = new JButton("Adjust Backrest");
        JPanel formPanel = new JPanel(new GridLayout(0, 2));
        formPanel.add(backrestHeightLabel);
        formPanel.add(backrestHeightField);
        formPanel.add(backrestWidthLabel);
        formPanel.add(backrestWidthField);
        formPanel.add(backrestDepthLabel);
        formPanel.add(backrestDepthField);
        formPanel.add(adjustBackrestButton);
        buttonPanel.add(formPanel);
        adjustBackrestButton.addActionListener(this::submitBackrestForm);
    }

    private void submitForm(ActionEvent e) {
        try {
            float newWidth = Float.parseFloat(widthField.getText());
            float newHeight = Float.parseFloat(heightField.getText());
            float newLength = Float.parseFloat(lengthField.getText());
            seatWidth = newWidth;
            seatHeight = newHeight;
            seatLength = newLength;
            // Redraw the chair with updated seat measurements
            repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numerical values for the seat measurements.");
        }
    }

    private void submitBackrestForm(ActionEvent e) {
        try {
            // Get the text from the input fields
            String heightText = backrestHeightField.getText();
            String widthText = backrestWidthField.getText();
            String depthText = backrestDepthField.getText();
            // Check if any of the fields are empty
            if (heightText.isEmpty() || widthText.isEmpty() || depthText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter values for all backrest measurements.");
                return;
            }
            // Parse the input values
            float newHeight = Float.parseFloat(heightText);
            float newWidth = Float.parseFloat(widthText);
            float newDepth = Float.parseFloat(depthText);
            // Validate input values
            if (newHeight <= 0 || newWidth <= 0 || newDepth <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter positive numerical values for the backrest measurements.");
                return;
            }
            // Update class-level variables
            backrestHeight = newHeight;
            backrestWidth = newWidth;
            backrestDepth = newDepth;
            // Redraw the chair with updated backrest measurements
            repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numerical values for the backrest measurements.");
        }
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        // Set the clear color using the background color
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glColor3f(normalizedChairColor[0], normalizedChairColor[1], normalizedChairColor[2]);
        gl.glTranslatef(0.0f, 0.0f, -20.0f); // Move the chair back to be visible
        gl.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
        gl.glScalef(scaleX, scaleY, scaleZ); //apply scaling
        drawSeat(gl); // Draw the chair seat
        drawBackrest(gl);
        // Draw the four legs
        gl.glPushMatrix();
        gl.glTranslatef(-1.5f, -6.0f, 1.5f); // Position the first leg
        drawTower(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(1.5f, -6.0f, 1.5f); // Position the second leg
        drawTower(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(-1.5f, -6.0f, -1.5f); // Position the third leg
        drawTower(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(1.5f, -6.0f, -1.5f); // Position the fourth leg
        drawTower(gl);
        gl.glPopMatrix();
        gl.glFlush();
    }

    private void drawBackrest(GL2 gl) {
        // Calculate the position of the backrest based on the seat length
        float backrestZPosition = 2;
        // Draw backrest front face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-backrestWidth / 2, seatHeight, backrestZPosition);
        gl.glVertex3f(-backrestWidth / 2, seatHeight + backrestHeight, backrestZPosition);
        gl.glVertex3f(backrestWidth / 2, seatHeight + backrestHeight, backrestZPosition);
        gl.glVertex3f(backrestWidth / 2, seatHeight, backrestZPosition);
        gl.glEnd();
        // Draw backrest top face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-backrestWidth / 2, seatHeight + backrestHeight, backrestZPosition);
        gl.glVertex3f(-backrestWidth / 2, seatHeight + backrestHeight, backrestZPosition + backrestDepth);
        gl.glVertex3f(backrestWidth / 2, seatHeight + backrestHeight, backrestZPosition + backrestDepth);
        gl.glVertex3f(backrestWidth / 2, seatHeight + backrestHeight, backrestZPosition);
        gl.glEnd();
        // Draw backrest left face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-backrestWidth / 2, seatHeight, backrestZPosition);
        gl.glVertex3f(-backrestWidth / 2, seatHeight + backrestHeight, backrestZPosition);
        gl.glVertex3f(-backrestWidth / 2, seatHeight + backrestHeight, backrestZPosition + backrestDepth);
        gl.glVertex3f(-backrestWidth / 2, seatHeight, backrestZPosition + backrestDepth);
        gl.glEnd();
        // Draw backrest right face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(backrestWidth / 2, seatHeight, backrestZPosition);
        gl.glVertex3f(backrestWidth / 2, seatHeight + backrestHeight, backrestZPosition);
        gl.glVertex3f(backrestWidth / 2, seatHeight + backrestHeight, backrestZPosition + backrestDepth);
        gl.glVertex3f(backrestWidth / 2, seatHeight, backrestZPosition + backrestDepth);
        gl.glEnd();
        // Draw backrest bottom face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-backrestWidth / 2, seatHeight, backrestZPosition);
        gl.glVertex3f(-backrestWidth / 2, seatHeight, backrestZPosition + backrestDepth);
        gl.glVertex3f(backrestWidth / 2, seatHeight, backrestZPosition + backrestDepth);
        gl.glVertex3f(backrestWidth / 2, seatHeight, backrestZPosition);
        gl.glEnd();
    }

    private void drawSeat(GL2 gl) {
        // Front face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-seatWidth / 2, 0, seatLength / 2);
        gl.glVertex3f(seatWidth / 2, 0, seatLength / 2);
        gl.glVertex3f(seatWidth / 2, seatHeight, seatLength / 2);
        gl.glVertex3f(-seatWidth / 2, seatHeight, seatLength / 2);
        gl.glEnd();
        // Back face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-seatWidth / 2, 0, -seatLength / 2);
        gl.glVertex3f(-seatWidth / 2, seatHeight, -seatLength / 2);
        gl.glVertex3f(seatWidth / 2, seatHeight, -seatLength / 2);
        gl.glVertex3f(seatWidth / 2, 0, -seatLength / 2);
        gl.glEnd();
        // Left face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-seatWidth / 2, 0, seatLength / 2);
        gl.glVertex3f(-seatWidth / 2, seatHeight, seatLength / 2);
        gl.glVertex3f(-seatWidth / 2, seatHeight, -seatLength / 2);
        gl.glVertex3f(-seatWidth / 2, 0, -seatLength / 2);
        gl.glEnd();
        // Right face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(seatWidth / 2, 0, seatLength / 2);
        gl.glVertex3f(seatWidth / 2, 0, -seatLength / 2);
        gl.glVertex3f(seatWidth / 2, seatHeight, -seatLength / 2);
        gl.glVertex3f(seatWidth / 2, seatHeight, seatLength / 2);
        gl.glEnd();
        // Top face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-seatWidth / 2, seatHeight, seatLength / 2);
        gl.glVertex3f(seatWidth / 2, seatHeight, seatLength / 2);
        gl.glVertex3f(seatWidth / 2, seatHeight, -seatLength / 2);
        gl.glVertex3f(-seatWidth / 2, seatHeight, -seatLength / 2);
        gl.glEnd();
        // Bottom face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-seatWidth / 2, 0, seatLength / 2);
        gl.glVertex3f(-seatWidth / 2, 0, -seatLength / 2);
        gl.glVertex3f(seatWidth / 2, 0, -seatLength / 2);
        gl.glVertex3f(seatWidth / 2, 0, seatLength / 2);
        gl.glEnd();
    }

    private void drawTower(GL2 gl) {
        float width = 1.0f;
        float length = 1.0f;
        float height = 6.0f;
        // Front face
        // Width of the leg
        // Length of the leg
        // Height of the leg
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-width / 2, 0, length / 2);
        gl.glVertex3f(width / 2, 0, length / 2);
        gl.glVertex3f(width / 2, height, length / 2);
        gl.glVertex3f(-width / 2, height, length / 2);
        gl.glEnd();
        // Back face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-width / 2, 0, -length / 2);
        gl.glVertex3f(-width / 2, height, -length / 2);
        gl.glVertex3f(width / 2, height, -length / 2);
        gl.glVertex3f(width / 2, 0, -length / 2);
        gl.glEnd();
        // Left face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-width / 2, 0, length / 2);
        gl.glVertex3f(-width / 2, height, length / 2);
        gl.glVertex3f(-width / 2, height, -length / 2);
        gl.glVertex3f(-width / 2, 0, -length / 2);
        gl.glEnd();
        // Right face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(width / 2, 0, length / 2);
        gl.glVertex3f(width / 2, 0, -length / 2);
        gl.glVertex3f(width / 2, height, -length / 2);
        gl.glVertex3f(width / 2, height, length / 2);
        gl.glEnd();
        // Top face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-width / 2, height, length / 2);
        gl.glVertex3f(width / 2, height, length / 2);
        gl.glVertex3f(width / 2, height, -length / 2);
        gl.glVertex3f(-width / 2, height, -length / 2);
        gl.glEnd();
        // Bottom face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-width / 2, 0, length / 2);
        gl.glVertex3f(-width / 2, 0, -length / 2);
        gl.glVertex3f(width / 2, 0, -length / 2);
        gl.glVertex3f(width / 2, 0, length / 2);
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        double aspect = (double) width / (double) height;
        gl.glFrustum(-1.0, 1.0, -aspect, aspect, 1.0, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int dx = x - lastX;
        int dy = y - lastY;
        rotateX += dy;
        rotateY += dx;
        lastX = x;
        lastY = y;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("3D Chair Renderer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            ChairRenderer renderer = new ChairRenderer();
            frame.getContentPane().add(renderer);
            frame.setVisible(true);
        });
    }
}
