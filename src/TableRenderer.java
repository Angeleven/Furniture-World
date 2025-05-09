import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.*;
import java.awt.event.*;

public class TableRenderer extends GLJPanel implements GLEventListener, MouseMotionListener {
    private FPSAnimator animator;
    private int lastX, lastY;
    private float rotateX = 0.0f;
    private float rotateY = 0.0f;
    private JButton tableColorButton;
    private JButton scaleUpButton;
    private JButton scaleDownButton;
    private JTextField widthField;
    private JTextField heightField;
    private JTextField lengthField;
    private Color tableColor = Color.blue;  // Default blue color as shown in the image
    private Color legColor = new Color(139, 69, 19);  // Brown color for legs
    private float[] normalizedTableColor;
    private float[] normalizedLegColor;
    private float width = 10.0f;
    private float height = 8.0f;
    private float length = 20.0f;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private JPanel buttonPanel;
    private float legWidth = 1.0f;
    private float tableTopThickness = 1.0f;

    public TableRenderer() {
        super(new GLCapabilities(GLProfile.getDefault()));
        setBackground(Color.white);
        setLayout(new BorderLayout());
        buttonPanel = new JPanel(new GridLayout(0, 1));
        add(buttonPanel, BorderLayout.WEST);

        addGLEventListener(this);
        addMouseMotionListener(this);
        animator = new FPSAnimator(this, 60);
        animator.start();
        initColorPicker(buttonPanel);
        normalizeColors();
        initScalingButtons();
        initTableDimensionsForm(buttonPanel);
    }

    private void initColorPicker(JPanel buttonPanel) {
        tableColorButton = new JButton("Change Table Color");
        tableColorButton.addActionListener(this::changeTableColor);
        buttonPanel.add(tableColorButton);

        JButton legColorButton = new JButton("Change Leg Color");
        legColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Leg Color", legColor);
            if (newColor != null) {
                legColor = newColor;
                normalizeColors();
                repaint();
            }
        });
        buttonPanel.add(legColorButton);
    }

    private void changeTableColor(ActionEvent e) {
        Color newColor = JColorChooser.showDialog(this, "Choose Table Color", tableColor);
        if (newColor != null) {
            tableColor = newColor;
            normalizeColors();
            repaint();
        }
    }

    private void normalizeColors() {
        normalizedTableColor = new float[]{
                tableColor.getRed() / 255.0f,
                tableColor.getGreen() / 255.0f,
                tableColor.getBlue() / 255.0f
        };

        normalizedLegColor = new float[]{
                legColor.getRed() / 255.0f,
                legColor.getGreen() / 255.0f,
                legColor.getBlue() / 255.0f
        };
    }

    private void initScalingButtons() {
        scaleUpButton = new JButton("Scale Up");
        scaleDownButton = new JButton("Scale Down");

        scaleUpButton.addActionListener(e -> {
            scaleX *= 1.1f;
            scaleY *= 1.1f;
            repaint();
        });

        scaleDownButton.addActionListener(e -> {
            scaleX *= 0.9f;
            scaleY *= 0.9f;
            repaint();
        });

        buttonPanel.add(scaleUpButton);
        buttonPanel.add(scaleDownButton);
    }

    private void initTableDimensionsForm(JPanel buttonPanel) {
        widthField = new JTextField(Float.toString(width), 5);
        heightField = new JTextField(Float.toString(height), 5);
        lengthField = new JTextField(Float.toString(length), 5);
        JButton applyButton = new JButton("Adjust Table");

        applyButton.addActionListener(e -> {
            try {
                width = Float.parseFloat(widthField.getText());
                height = Float.parseFloat(heightField.getText());
                length = Float.parseFloat(lengthField.getText());
                repaint();
            } catch (NumberFormatException ignored) {}
        });

        buttonPanel.add(new JLabel("Width:"));
        buttonPanel.add(widthField);
        buttonPanel.add(new JLabel("Height:"));
        buttonPanel.add(heightField);
        buttonPanel.add(new JLabel("Length:"));
        buttonPanel.add(lengthField);
        buttonPanel.add(applyButton);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0f, 0f, 0f, 1f);  // Black background
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -50f);
        gl.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
        gl.glScalef(scaleX, scaleY, scaleX);

        drawTable(gl);
    }

    private void drawTable(GL2 gl) {
        // Draw table top
        drawTableTop(gl);

        // Draw four legs
        float legX1 = -width/2 + legWidth/2;
        float legX2 = width/2 - legWidth/2;
        float legZ1 = -length/2 + legWidth/2;
        float legZ2 = length/2 - legWidth/2;

        // Draw legs at the corners
        drawLeg(gl, legX1, legZ1);
        drawLeg(gl, legX1, legZ2);
        drawLeg(gl, legX2, legZ1);
        drawLeg(gl, legX2, legZ2);
    }

    private void drawTableTop(GL2 gl) {
        // Draw table top as a rectangular prism
        // Set table top color to blue
        gl.glColor3fv(normalizedTableColor, 0);

        float halfWidth = width/2;
        float halfLength = length/2;
        float topY = height;
        float bottomY = height - tableTopThickness;

        // Top face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-halfWidth, topY, -halfLength);
        gl.glVertex3f(-halfWidth, topY, halfLength);
        gl.glVertex3f(halfWidth, topY, halfLength);
        gl.glVertex3f(halfWidth, topY, -halfLength);
        gl.glEnd();

        // Bottom face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-halfWidth, bottomY, -halfLength);
        gl.glVertex3f(-halfWidth, bottomY, halfLength);
        gl.glVertex3f(halfWidth, bottomY, halfLength);
        gl.glVertex3f(halfWidth, bottomY, -halfLength);
        gl.glEnd();

        // Front face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-halfWidth, bottomY, halfLength);
        gl.glVertex3f(-halfWidth, topY, halfLength);
        gl.glVertex3f(halfWidth, topY, halfLength);
        gl.glVertex3f(halfWidth, bottomY, halfLength);
        gl.glEnd();

        // Back face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-halfWidth, bottomY, -halfLength);
        gl.glVertex3f(-halfWidth, topY, -halfLength);
        gl.glVertex3f(halfWidth, topY, -halfLength);
        gl.glVertex3f(halfWidth, bottomY, -halfLength);
        gl.glEnd();

        // Left face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-halfWidth, bottomY, -halfLength);
        gl.glVertex3f(-halfWidth, topY, -halfLength);
        gl.glVertex3f(-halfWidth, topY, halfLength);
        gl.glVertex3f(-halfWidth, bottomY, halfLength);
        gl.glEnd();

        // Right face
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(halfWidth, bottomY, -halfLength);
        gl.glVertex3f(halfWidth, topY, -halfLength);
        gl.glVertex3f(halfWidth, topY, halfLength);
        gl.glVertex3f(halfWidth, bottomY, halfLength);
        gl.glEnd();
    }

    private void drawLeg(GL2 gl, float x, float z) {
        // Draw a leg as a rectangular prism
        float halfLegWidth = legWidth/2;

        // Set leg color to brown
        gl.glColor3fv(normalizedLegColor, 0);

        gl.glBegin(GL2.GL_QUADS);

        // Front face
        gl.glVertex3f(x - halfLegWidth, 0, z + halfLegWidth);
        gl.glVertex3f(x + halfLegWidth, 0, z + halfLegWidth);
        gl.glVertex3f(x + halfLegWidth, height - tableTopThickness, z + halfLegWidth);
        gl.glVertex3f(x - halfLegWidth, height - tableTopThickness, z + halfLegWidth);

        // Back face
        gl.glVertex3f(x - halfLegWidth, 0, z - halfLegWidth);
        gl.glVertex3f(x + halfLegWidth, 0, z - halfLegWidth);
        gl.glVertex3f(x + halfLegWidth, height - tableTopThickness, z - halfLegWidth);
        gl.glVertex3f(x - halfLegWidth, height - tableTopThickness, z - halfLegWidth);

        // Left face
        gl.glVertex3f(x - halfLegWidth, 0, z - halfLegWidth);
        gl.glVertex3f(x - halfLegWidth, 0, z + halfLegWidth);
        gl.glVertex3f(x - halfLegWidth, height - tableTopThickness, z + halfLegWidth);
        gl.glVertex3f(x - halfLegWidth, height - tableTopThickness, z - halfLegWidth);

        // Right face
        gl.glVertex3f(x + halfLegWidth, 0, z - halfLegWidth);
        gl.glVertex3f(x + halfLegWidth, 0, z + halfLegWidth);
        gl.glVertex3f(x + halfLegWidth, height - tableTopThickness, z + halfLegWidth);
        gl.glVertex3f(x + halfLegWidth, height - tableTopThickness, z - halfLegWidth);

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
            JFrame frame = new JFrame("Table Renderer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            TableRenderer renderer = new TableRenderer();
            frame.getContentPane().add(renderer);
            frame.setVisible(true);
        });
    }
}