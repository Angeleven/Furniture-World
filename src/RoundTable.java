import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.*;
import java.awt.event.*;

public class RoundTable extends GLJPanel implements GLEventListener, MouseMotionListener {

    private FPSAnimator animator;
    private int lastX, lastY;
    private float rotateX = 0.0f;
    private float rotateY = 0.0f;
    private JButton tableColorButton;
    private JButton scaleUpButton;
    private JButton scaleDownButton;
    private JTextField radiusField;
    private Color tableColor = Color.blue;
    private float[] normalizedTableColor;
    private float radius = 4.0f;
    private float tableHeight = 0.2f;
    private float scaleX = 1.0f, scaleY = 1.0f, scaleZ = 1.0f;
    private JPanel buttonPanel;

    public RoundTable() {
        super(new GLCapabilities(GLProfile.getDefault()));
        setBackground(Color.black); // Set panel background to black
        setLayout(new BorderLayout());
        buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.setBackground(Color.black); // Button panel background

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
        return new float[]{
                color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f
        };
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

        buttonPanel.add(scaleUpButton);
        buttonPanel.add(scaleDownButton);
    }

    private void initTableDimensionsForm(JPanel buttonPanel) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.black);
        panel.setForeground(Color.white);

        panel.add(new JLabel("Radius:"));
        radiusField = new JTextField(String.valueOf(radius), 5);
        panel.add(radiusField);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            try {
                radius = Float.parseFloat(radiusField.getText());
                repaint();
            } catch (NumberFormatException ignored) {}
        });

        panel.add(applyButton);
        buttonPanel.add(panel);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0f, 0f, 0f, 1f); // Black OpenGL background
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -15.0f);
        gl.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
        gl.glScalef(scaleX, scaleY, scaleZ);

        drawRoundTable(gl);
        drawTableLegs(gl);
    }

    private void drawRoundTable(GL2 gl) {
        int slices = 36;
        float angleStep = (float) (2 * Math.PI / slices);

        gl.glColor3fv(normalizedTableColor, 0);

        // Top surface
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex3f(0, tableHeight, 0);
        for (int i = 0; i <= slices; i++) {
            float angle = i * angleStep;
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            gl.glVertex3f(x, tableHeight, z);
        }
        gl.glEnd();

        // Side surface
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= slices; i++) {
            float angle = i * angleStep;
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            gl.glVertex3f(x, 0, z);
            gl.glVertex3f(x, tableHeight, z);
        }
        gl.glEnd();
    }

    private void drawTableLegs(GL2 gl) {
        float legHeight = 3.0f;
        float legRadius = 0.1f;
        int slices = 20;

        float[][] legPositions = {
                {(float) (radius * 0.7f), 0, (float) (radius * 0.7f)},
                {(float) (-radius * 0.7f), 0, (float) (radius * 0.7f)},
                {(float) (radius * 0.7f), 0, (float) (-radius * 0.7f)},
                {(float) (-radius * 0.7f), 0, (float) (-radius * 0.7f)},
        };

        gl.glColor3f(0.4f, 0.2f, 0.1f); // Brown legs

        for (float[] pos : legPositions) {
            drawCylinder(gl, pos[0], 0, pos[2], legRadius, legHeight, slices);
        }
    }

    private void drawCylinder(GL2 gl, float x, float y, float z, float r, float h, int slices) {
        float angleStep = (float) (2 * Math.PI / slices);

        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);

        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= slices; i++) {
            float angle = i * angleStep;
            float cx = (float) Math.cos(angle) * r;
            float cz = (float) Math.sin(angle) * r;
            gl.glVertex3f(cx, 0, cz);
            gl.glVertex3f(cx, -h, cz);
        }
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
        gl.glFrustum(-aspect, aspect, -1.0, 1.0, 1.0, 100.0);
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
            JFrame frame = new JFrame("Round Table With Legs");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            RoundTable tableRenderer = new RoundTable();
            frame.getContentPane().add(tableRenderer);

            frame.setVisible(true);
        });
    }
}
