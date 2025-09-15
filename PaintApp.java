import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class PaintApp extends JFrame {

    private int x, y, prevX, prevY;
    private boolean drawing = false;
    private Color currentColor = Color.WHITE; // default brush color
    private int brushSize = 5; // default brush size
    private String brushShape = "Circle"; // default brush shape
    private JPanel canvas;
    private Random random = new Random();
    private BufferedImage canvasImage;

    public PaintApp() {
        setTitle("Java Paint App - Pastel Palette");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🖼️ Buffered image for saving
        canvasImage = new BufferedImage(900, 700, BufferedImage.TYPE_INT_ARGB);

        // 🎨 Toolbar (top)
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(new Color(245, 245, 245));

        Font toolbarFont = new Font("Segoe UI", Font.BOLD, 14);

        // Brush size selector
        String[] brushSizes = {"Small", "Medium", "Large"};
        JComboBox<String> brushSelector = new JComboBox<>(brushSizes);
        brushSelector.setFont(toolbarFont);
        brushSelector.addActionListener(e -> {
            String size = (String) brushSelector.getSelectedItem();
            switch (size) {
                case "Small": brushSize = 3; break;
                case "Medium": brushSize = 7; break;
                case "Large": brushSize = 12; break;
            }
        });

        // Brush shape selector
        String[] brushShapes = {"Circle", "Square", "Spray"};
        JComboBox<String> shapeSelector = new JComboBox<>(brushShapes);
        shapeSelector.setFont(toolbarFont);
        shapeSelector.addActionListener(e -> {
            brushShape = (String) shapeSelector.getSelectedItem();
        });

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(toolbarFont);
        clearBtn.setBackground(new Color(240, 128, 128));
        clearBtn.setForeground(Color.WHITE);

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(toolbarFont);
        saveBtn.setBackground(new Color(100, 181, 246));
        saveBtn.setForeground(Color.WHITE);

        toolbar.add(new JLabel("Brush Size:")).setFont(toolbarFont);
        toolbar.add(brushSelector);
        toolbar.add(new JLabel("Brush Shape:")).setFont(toolbarFont);
        toolbar.add(shapeSelector);
        toolbar.add(clearBtn);
        toolbar.add(saveBtn);

        add(toolbar, BorderLayout.NORTH);

        // 🎨 Color palette panel (grid layout)
        JPanel palette = new JPanel();
        palette.setLayout(new GridLayout(10, 3, 5, 5));
        palette.setPreferredSize(new Dimension(200, 0));

        Color[] colors = {
            new Color(187, 134, 252), new Color(206, 147, 216), new Color(224, 176, 255),
            new Color(244, 143, 177), new Color(255, 167, 167), new Color(255, 204, 188),
            new Color(255, 223, 186), new Color(255, 255, 186), new Color(255, 249, 196),
            new Color(200, 230, 201), new Color(165, 214, 167), new Color(128, 222, 234),
            new Color(179, 229, 252), new Color(187, 222, 251), new Color(197, 202, 233),
            new Color(255, 236, 179), new Color(255, 245, 157), new Color(255, 255, 141),
            new Color(178, 223, 219), new Color(128, 203, 196), new Color(129, 212, 250),
            new Color(159, 168, 218), new Color(121, 134, 203), new Color(186, 104, 200),
            new Color(244, 180, 255), new Color(240, 98, 146), new Color(229, 115, 115),
            new Color(255, 138, 101), new Color(255, 209, 128), new Color(255, 241, 118)
        };

        for (Color c : colors) {
            JButton btn = new JButton();
            btn.setBackground(c);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setPreferredSize(new Dimension(60, 60));
            btn.addActionListener(e -> currentColor = c);
            palette.add(btn);
        }

        add(palette, BorderLayout.WEST);

        // 🖼️ Drawing canvas
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(canvasImage, 0, 0, null);
            }
        };
        canvas.setBackground(Color.BLACK);

        // Mouse listeners
        canvas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                drawing = true;
                prevX = e.getX();
                prevY = e.getY();
                drawBrush(e.getX(), e.getY());
            }
            public void mouseReleased(MouseEvent e) {
                drawing = false;
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (drawing) {
                    drawBrush(e.getX(), e.getY());
                    prevX = e.getX();
                    prevY = e.getY();
                }
            }
        });

        add(canvas, BorderLayout.CENTER);

        // Clear button
        clearBtn.addActionListener(e -> {
            Graphics2D g2 = canvasImage.createGraphics();
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
            g2.dispose();
            canvas.repaint();
        });

        // Save button
        saveBtn.addActionListener(e -> {
            try {
                File outputFile = new File("painting.png");
                ImageIO.write(canvasImage, "png", outputFile);
                JOptionPane.showMessageDialog(this, "Saved as painting.png");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        });
    }

    // Draw brush shapes
    private void drawBrush(int x, int y) {
        Graphics2D g2 = canvasImage.createGraphics();
        g2.setColor(currentColor);

        switch (brushShape) {
            case "Circle":
                g2.fillOval(x - brushSize / 2, y - brushSize / 2, brushSize, brushSize);
                break;
            case "Square":
                g2.fillRect(x - brushSize / 2, y - brushSize / 2, brushSize, brushSize);
                break;
            case "Spray":
                for (int i = 0; i < 20; i++) {
                    int offsetX = random.nextInt(brushSize * 2) - brushSize;
                    int offsetY = random.nextInt(brushSize * 2) - brushSize;
                    if (offsetX * offsetX + offsetY * offsetY <= brushSize * brushSize) {
                        g2.fillRect(x + offsetX, y + offsetY, 1, 1);
                    }
                }
                break;
        }

        g2.dispose();
        canvas.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PaintApp().setVisible(true);
        });
    }
}
