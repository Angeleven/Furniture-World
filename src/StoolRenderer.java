import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.*;
import java.awt.event.*;

public class StoolRenderer extends GLJPanel implements GLEventListener, MouseMotionListener {
    private FPSAnimator animator;
    private int lastX, lastY;
    private float rotateX = 0.0f;
    private float rotateY = 0.0f;
    private JButton stoolColorButton;
    private JButton scaleUpButton;
    private JButton scaleDownButton;
    private JTextField radiusField;
    private JTextField heightField;
    private Color stoolColor = Color.green;
    private float[] normalizedStoolColor;
    private float radius = 3.0f;
    private float height = 15.0f;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private JPanel buttonPanel;
    float legHeight = height;
    float legWidth = 0.5f;
    float legOffset = 2.0f; // Distance of legs from center
    float seatThickness = 1.0f;

    public StoolRenderer() {
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
        initStoolDimensionsForm(buttonPanel);
    }

    private void initColorPicker(JPanel buttonPanel) {
        stoolColorButton = new JButton("Change Stool Color");
        stoolColorButton.addActionListener(this::changeStoolColor);
        buttonPanel.add(stoolColorButton);
    }

    private void changeStoolColor(ActionEvent e) {
        Color newColor = JColorChooser.showDialog(this, "Choose Stool Color", stoolColor);
        if (newColor != null) {
            stoolColor = newColor;
            normalizeColors();
            repaint();
        }
    }

    private void normalizeColors() {
        normalizedStoolColor = new float[]{
                stoolColor.getRed() / 255.0f,
                stoolColor.getGreen() / 255.0f,
                stoolColor.getBlue() / 255.0f
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

    private void initStoolDimensionsForm(JPanel buttonPanel) {
        radiusField = new JTextField(Float.toString(radius), 5);
        heightField = new JTextField(Float.toString(height), 5);
        JButton applyButton = new JButton("Apply Dimensions");

        applyButton.addActionListener(e -> {
            try {
                radius = Float.parseFloat(radiusField.getText());
                height = Float.parseFloat(heightField.getText());
                legHeight = height;
                repaint();
            } catch (NumberFormatException ignored) {}
        });

        buttonPanel.add(new JLabel("Radius:"));
        buttonPanel.add(radiusField);
        buttonPanel.add(new JLabel("Height:"));
        buttonPanel.add(heightField);
        buttonPanel.add(applyButton);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        // Black background to match your previous settings
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -40f);
        gl.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
        gl.glScalef(scaleX, scaleY, scaleX);

        drawStool(gl);
    }

    private void drawStool(GL2 gl) {
        // Draw stool seat (top disk)
        gl.glColor3fv(normalizedStoolColor, 0);
        int slices = 40;
        float[] x = new float[slices];
        float[] z = new float[slices];

        for (int i = 0; i < slices; i++) {
            double angle = 2 * Math.PI * i / slices;
            x[i] = (float) Math.cos(angle);
            z[i] = (float) Math.sin(angle);
        }

        gl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i < slices; i++) {
            gl.glVertex3f(x[i] * radius, height, z[i] * radius);
        }
        gl.glEnd();

        // Draw seat bottom (optional)
        gl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i < slices; i++) {
            gl.glVertex3f(x[i] * radius, height - seatThickness, z[i] * radius);
        }
        gl.glEnd();

        // Draw seat edge (connecting top and bottom)
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i < slices; i++) {
            gl.glVertex3f(x[i] * radius, height, z[i] * radius);
            gl.glVertex3f(x[i] * radius, height - seatThickness, z[i] * radius);
        }
        gl.glVertex3f(x[0] * radius, height, z[0] * radius);
        gl.glVertex3f(x[0] * radius, height - seatThickness, z[0] * radius);
        gl.glEnd();

        // Draw legs in brown (RGB: 0.4, 0.2, 0.1)
        gl.glColor3f(0.4f, 0.2f, 0.1f);

        // Draw three legs placed evenly around the stool underside
        for (int i = 0; i < 3; i++) {
            double angle = 2 * Math.PI * i / 3;
            float legX = (float) (Math.cos(angle) * legOffset);
            float legZ = (float) (Math.sin(angle) * legOffset);
            drawLeg(gl, legX, legZ);
        }
    }

    private void drawLeg(GL2 gl, float x, float z) {
        // Draw a leg as a thin rectangular prism from y = 0 to y = height - seatThickness.
        gl.glBegin(GL2.GL_QUADS);

        // Front face
        gl.glVertex3f(x - legWidth/2, 0, z - legWidth/2);
        gl.glVertex3f(x + legWidth/2, 0, z - legWidth/2);
        gl.glVertex3f(x + legWidth/2, height - seatThickness, z - legWidth/2);
        gl.glVertex3f(x - legWidth/2, height - seatThickness, z - legWidth/2);

        // Back face
        gl.glVertex3f(x - legWidth/2, 0, z + legWidth/2);
        gl.glVertex3f(x + legWidth/2, 0, z + legWidth/2);
        gl.glVertex3f(x + legWidth/2, height - seatThickness, z + legWidth/2);
        gl.glVertex3f(x - legWidth/2, height - seatThickness, z + legWidth/2);

        // Left face
        gl.glVertex3f(x - legWidth/2, 0, z - legWidth/2);
        gl.glVertex3f(x - legWidth/2, 0, z + legWidth/2);
        gl.glVertex3f(x - legWidth/2, height - seatThickness, z + legWidth/2);
        gl.glVertex3f(x - legWidth/2, height - seatThickness, z - legWidth/2);

        // Right face
        gl.glVertex3f(x + legWidth/2, 0, z - legWidth/2);
        gl.glVertex3f(x + legWidth/2, 0, z + legWidth/2);
        gl.glVertex3f(x + legWidth/2, height - seatThickness, z + legWidth/2);
        gl.glVertex3f(x + legWidth/2, height - seatThickness, z - legWidth/2);

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
            JFrame frame = new JFrame("3D Stool Renderer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            StoolRenderer renderer = new StoolRenderer();
            frame.getContentPane().add(renderer);
            frame.setVisible(true);
        });
    }
}
