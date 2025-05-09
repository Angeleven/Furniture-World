import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.*;
import java.awt.event.*;

public class BoxTable extends GLJPanel implements GLEventListener, MouseMotionListener {

    private FPSAnimator animator;
    private int lastX, lastY;
    private float rotateX = 0.0f;
    private float rotateY = 0.0f;
    private JButton tableColorButton;
    private JButton scaleUpButton;
    private JButton scaleDownButton;
    private JTextField dimensionField;
    private Color tableColor = Color.blue;
    private float[] normalizedTableColor;
    private float tableDimension = 6.0f;
    private float tableHeight = 1.0f;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float scaleZ = 1.0f;
    private JPanel buttonPanel;

    public BoxTable() {
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
    }

    private void changeTableColor(ActionEvent e) {
        Color defaultColor = tableColor != null ? tableColor : Color.BLUE;
        Color newTableColor = JColorChooser.showDialog(null, "Choose Table Color", defaultColor);
        if (newTableColor != null) {
            tableColor = newTableColor;
            normalizeColors();
            repaint();
        }
    }

    private void normalizeColors() {
        normalizedTableColor = normalizeColor(tableColor);
    }

    private float[] normalizeColor(Color color) {
        return new float[]{color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f};
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

        JPanel scalingPanel = new JPanel();
        scalingPanel.setLayout(new GridLayout(2, 1));
        scalingPanel.add(scaleUpButton);
        scalingPanel.add(scaleDownButton);

        buttonPanel.add(scalingPanel);
    }

    private void initTableDimensionsForm(JPanel buttonPanel) {
        JLabel dimensionLabel = new JLabel("Dimension:");
        dimensionField = new JTextField(5);
        JButton adjustTableButton = new JButton("Adjust Table");

        JPanel formPanel = new JPanel(new GridLayout(0, 2));
        formPanel.add(dimensionLabel);
        formPanel.add(dimensionField);
        formPanel.add(adjustTableButton);

        buttonPanel.add(formPanel);

        adjustTableButton.addActionListener(this::submitForm);
    }

    private void submitForm(ActionEvent e) {
        try {
            float newDimension = Float.parseFloat(dimensionField.getText());
            if (newDimension > 0) {
                tableDimension = newDimension;
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a positive number.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number.");
        }
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Cleanup if needed
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -20.0f);
        gl.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
        gl.glScalef(scaleX, scaleY, scaleZ);

        if (normalizedTableColor != null) {
            gl.glColor3f(normalizedTableColor[0], normalizedTableColor[1], normalizedTableColor[2]);
        }

        float width = tableDimension;
        float height = tableHeight;
        float length = tableDimension;

        // Top face (Table Surface)
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

        // Adding table legs (Four corners)
        float legHeight = 3.0f; // Height of the legs
        float legWidth = 0.3f;  // Width of the legs

        // Front-left leg
        gl.glPushMatrix();
        gl.glTranslatef(-width / 2 + legWidth / 2, 0, length / 2 - legWidth / 2);
        gl.glScalef(legWidth, legHeight, legWidth);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glEnd();
        gl.glPopMatrix();

        // Front-right leg
        gl.glPushMatrix();
        gl.glTranslatef(width / 2 - legWidth / 2, 0, length / 2 - legWidth / 2);
        gl.glScalef(legWidth, legHeight, legWidth);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glEnd();
        gl.glPopMatrix();

        // Back-left leg
        gl.glPushMatrix();
        gl.glTranslatef(-width / 2 + legWidth / 2, 0, -length / 2 + legWidth / 2);
        gl.glScalef(legWidth, legHeight, legWidth);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glEnd();
        gl.glPopMatrix();

        // Back-right leg
        gl.glPushMatrix();
        gl.glTranslatef(width / 2 - legWidth / 2, 0, -length / 2 + legWidth / 2);
        gl.glScalef(legWidth, legHeight, legWidth);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glEnd();
        gl.glPopMatrix();
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
            JFrame frame = new JFrame("Box Table Renderer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            BoxTable renderer = new BoxTable();
            frame.getContentPane().add(renderer);

            frame.setVisible(true);
        });
    }
}
