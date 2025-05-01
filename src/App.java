// Importy tříd
import models.Line;
import models.LineCanvas;
import models.Point;
import models.Polygon;
import rasterizers.LineCanvasRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;
import Fillers.BasicFiller;
import Fillers.Filler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// Hlavní třída aplikace
public class App {

    // panel pro vykreslení
    private final JPanel panel;
    // Raster pro práci s pixely
    private final Raster raster;
    // Adaptery na vstupy od myši a klávesnice
    private MouseAdapter mouseAdapter;
    private MouseMotionAdapter mouseMotionAdapter;
    private KeyAdapter keyAdapter;
    // Body pro kreslení čar
    private Point startPoint;
    private Point currentPoint;
    // Rasterizer čar
    private LineCanvasRasterizer rasterizer;
    // Canvas pro uchování nakreslených čar a polygonů
    private LineCanvas canvas;
    // Aktuální polygon
    private Polygon currentPolygon;
    // Výplň
    private BasicFiller filler;
    // Předchozí barva (použití při gumování)
    private Color previousColor;

    // Aktuální nástroj, barva
    private String currentTool = "line";
    private Color currentColor = Color.WHITE;
    private boolean snapTo45 = false;
    private boolean dottedLine = false;
    private int lineWidth = 1;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    // Konstruktor aplikace
    public App(int width, int height) {
        this.raster = new RasterBufferedImage(width, height);
        this.canvas = new LineCanvas(new ArrayList<>(), new ArrayList<>());
        this.rasterizer = new LineCanvasRasterizer(raster);
        this.filler = new BasicFiller(raster);

        // Panel, kde se vsechno vykresluje
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);     // vykreslení obsahu rastru
                drawPreview(g); // vykreslení náhledu aktuálního nástroje
            }
        };

        panel.setPreferredSize(new Dimension(width, height));
        createAdapters(); // Nastavení posluchačů na vstupy
    }

    // Vytvoření okna
    public void start() {
        JFrame frame = new JFrame("Kerthor malování");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel tools = createToolbar(); // panel s nástroji

        frame.add(tools, BorderLayout.NORTH);   // toolbar nahoru
        frame.add(panel, BorderLayout.CENTER);  // kreslicí panel do středu

        frame.pack();
        frame.setVisible(true);

        clear(Color.BLACK.getRGB()); // vymazání plátna na černo
    }

    // Vytvoření toolbaru s nástroji
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Tlačítka pro různé nástroje
        JButton lineButton = new JButton("Čára");
        lineButton.addActionListener(e -> currentTool = "line");

        JButton rectButton = new JButton("Obdélník");
        rectButton.addActionListener(e -> currentTool = "rectangle");

        JButton circleButton = new JButton("Kružnice");
        circleButton.addActionListener(e -> currentTool = "circle");

        JButton polygonButton = new JButton("Polygon");
        polygonButton.addActionListener(e -> {
            currentTool = "polygon";
            currentPolygon = new Polygon(); // nový prázdný polygon
        });

        JButton colorButton = new JButton("Barva");
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(panel, "Vyber barvu", currentColor);
            if (selectedColor != null) {
                currentColor = selectedColor;
            }
        });

        // Tlačítko pro výplň
        JButton fillButton = new JButton("Výplň");
        fillButton.addActionListener(e -> currentTool = "fill");

        // Tlačítko pro gumu
        JButton eraserButton = new JButton("Guma");
        eraserButton.addActionListener(e -> {
            currentTool = "eraser";
            previousColor = currentColor;
            currentColor = Color.BLACK; // "gumování" je kreslení černou barvou
        });

        // Volba tloušťky čáry
        JLabel widthLabel = new JLabel("Tloušťka:");
        JComboBox<Integer> lineWidthSelector = new JComboBox<>(new Integer[]{1, 2, 3, 5, 8});
        lineWidthSelector.setSelectedIndex(0);
        lineWidthSelector.addActionListener(e -> {
            lineWidth = (Integer) lineWidthSelector.getSelectedItem();
        });

        // Přidání všech komponent do toolbaru
        toolbar.add(lineButton);
        toolbar.add(rectButton);
        toolbar.add(circleButton);
        toolbar.add(polygonButton);
        toolbar.add(fillButton);
        toolbar.add(eraserButton);
        toolbar.add(colorButton);
        toolbar.add(widthLabel);
        toolbar.add(lineWidthSelector);


        return toolbar;
    }

    // Nastavení listeneru pro myš a klávesnici
    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                panel.requestFocusInWindow();
                startPoint = new Point(e.getX(), e.getY());
                currentPoint = startPoint;

                if ("fill".equals(currentTool)) {
                    filler.fill(new java.awt.Point(e.getX(), e.getY()), currentColor);
                    panel.repaint();
                    return;
                } else if ("eraser".equals(currentTool)) {
                    // Guma se chová jako kreslení čáry s černou barvou
                } else if ("polygon".equals(currentTool)) {
                    if (currentPolygon == null) {
                        currentPolygon = new Polygon();
                    }
                    if (!currentPolygon.getPoints().isEmpty()) {
                        Point lastPoint = currentPolygon.getPoints().get(currentPolygon.getPoints().size() - 1);
                        drawThickLine(lastPoint, startPoint, lineWidth);
                    }
                    currentPolygon.addPoint(startPoint);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentPoint = new Point(e.getX(), e.getY());

                if ("eraser".equals(currentTool)) {
                    drawThickLine(startPoint, currentPoint, lineWidth * 2); // silnější tloušťka pro gumu
                    currentColor = previousColor;
                } else if ("polygon".equals(currentTool) && e.getClickCount() == 2) {
                    if (!currentPolygon.getPoints().isEmpty()) {
                        drawThickLine(
                                currentPolygon.getPoints().get(currentPolygon.getPoints().size() - 1),
                                currentPolygon.getPoints().get(0),
                                lineWidth
                        );
                    }
                    canvas.add(currentPolygon);
                    currentPolygon = null;
                } else {
                    switch (currentTool) {
                        case "line" -> {
                            if (dottedLine) {
                                drawDottedLine(startPoint, adjustPoint(currentPoint));
                            } else {
                                drawThickLine(startPoint, adjustPoint(currentPoint), lineWidth);
                            }
                        }
                        case "rectangle" -> drawRectangle(startPoint, adjustPoint(currentPoint));
                        case "circle" -> drawCircle(startPoint, adjustPoint(currentPoint));
                    }
                }

                startPoint = null;
                currentPoint = null;
                panel.repaint();
            }
        };

        mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currentPoint = new Point(e.getX(), e.getY());

                if ("eraser".equals(currentTool)) {
                    drawThickLine(startPoint, currentPoint, lineWidth * 2);
                    startPoint = currentPoint;
                }

                panel.repaint();
            }
        };

        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    snapTo45 = true;
                    panel.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    dottedLine = true;
                    panel.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    clear(Color.BLACK.getRGB());
                    canvas.clear();
                    panel.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    snapTo45 = false;
                    panel.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    dottedLine = false;
                    panel.repaint();
                }
            }
        };

        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseMotionAdapter);
        panel.addKeyListener(keyAdapter);

        panel.setFocusable(true);
    }

    // Základní kreslení čáry
    public void drawLine(Point start, Point end) {
        Line line = new Line(start, end, currentColor);
        rasterizer.rasterizeLine(line);
    }

    // Kreslení čáry s danou tloušťkou
    public void drawThickLine(Point start, Point end, int thickness) {
        if (thickness <= 1) {
            drawLine(start, end);
            return;
        }

        int halfThickness = thickness / 2;

        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        double len = Math.sqrt(dx*dx + dy*dy);
        if (len < 0.0001) return;

        double nx = -dy / len;
        double ny = dx / len;

        for (int i = -halfThickness; i <= halfThickness; i++) {
            Point s = new Point((int)(start.getX() + i * nx), (int)(start.getY() + i * ny));
            Point e = new Point((int)(end.getX() + i * nx), (int)(end.getY() + i * ny));
            drawLine(s, e);
        }
    }

    // Kreslení čárkované čáry
    public void drawDottedLine(Point start, Point end) {
        Line line = new Line(start, end, currentColor);
        rasterizer.rasterizeDottedLine(line);
    }

    // Kreslení obdélníku
    public void drawRectangle(Point topLeft, Point bottomRight) {
        Point topRight = new Point(bottomRight.getX(), topLeft.getY());
        Point bottomLeft = new Point(topLeft.getX(), bottomRight.getY());

        drawThickLine(topLeft, topRight, lineWidth);
        drawThickLine(topRight, bottomRight, lineWidth);
        drawThickLine(bottomRight, bottomLeft, lineWidth);
        drawThickLine(bottomLeft, topLeft, lineWidth);
    }

    // Kreslení kružnice
    public void drawCircle(Point start, Point end) {
        int radius = (int) Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2));

        int centerX = start.getX();
        int centerY = start.getY();

        int x = 0, y = radius;
        int d = 3 - 2 * radius;

        while (y >= x) {
            drawCirclePoints(centerX, centerY, x, y);
            x++;
            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            } else {
                d = d + 4 * x + 6;
            }
        }

        if (lineWidth > 1) {
            for (int i = 1; i < lineWidth; i++) {
                if (radius - i > 0) {
                    drawSimpleCircle(centerX, centerY, radius - i);
                }
                drawSimpleCircle(centerX, centerY, radius + i);
            }
        }
    }

    // Pomocné kreslení kružnice se zadaným poloměrem
    private void drawSimpleCircle(int centerX, int centerY, int radius) {
        int x = 0, y = radius;
        int d = 3 - 2 * radius;

        while (y >= x) {
            drawCirclePoints(centerX, centerY, x, y);
            x++;
            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            } else {
                d = d + 4 * x + 6;
            }
        }
    }

    // Pomocné body kružnice
    private void drawCirclePoints(int cx, int cy, int x, int y) {
        raster.setPixel(cx + x, cy + y, currentColor.getRGB());
        raster.setPixel(cx - x, cy + y, currentColor.getRGB());
        raster.setPixel(cx + x, cy - y, currentColor.getRGB());
        raster.setPixel(cx - x, cy - y, currentColor.getRGB());
        raster.setPixel(cx + y, cy + x, currentColor.getRGB());
        raster.setPixel(cx - y, cy + x, currentColor.getRGB());
        raster.setPixel(cx + y, cy - x, currentColor.getRGB());
        raster.setPixel(cx - y, cy - x, currentColor.getRGB());
    }

    // Upraví bod na zarovnání na 45°
    private Point adjustPoint(Point target) {
        if (snapTo45 && startPoint != null) {
            return snapToNearest45deg(startPoint, target);
        }
        return target;
    }

    // Snapování na 45°
    public static Point snapToNearest45deg(Point p1, Point p2) {
        int dx = p2.getX() - p1.getX();
        int dy = p2.getY() - p1.getY();

        if (Math.abs(dx) < 5 && Math.abs(dy) < 5) {
            return p1;
        }

        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        double angle = Math.toDegrees(Math.atan2(absDy, absDx));

        int snapAngle;
        if (angle < 22.5) {
            snapAngle = 0;
        } else if (angle < 67.5) {
            snapAngle = 45;
        } else {
            snapAngle = 90;
        }

        if (snapAngle == 0) {
            dy = 0;
        } else if (snapAngle == 90) {
            dx = 0;
        } else if (snapAngle == 45) {
            int magnitude = Math.max(absDx, absDy);
            dx = (dx >= 0) ? magnitude : -magnitude;
            dy = (dy >= 0) ? magnitude : -magnitude;
        }

        return new Point(p1.getX() + dx, p1.getY() + dy);
    }

    // Náhled aktuálního kresleného objektu
    private void drawPreview(Graphics g) {
        if (startPoint != null && currentPoint != null) {
            g.setColor(currentColor);

            if (g instanceof Graphics2D) {
                ((Graphics2D) g).setStroke(new BasicStroke(lineWidth));
            }

            switch (currentTool) {
                case "line", "eraser" -> g.drawLine(
                        startPoint.getX(), startPoint.getY(),
                        adjustPoint(currentPoint).getX(), adjustPoint(currentPoint).getY()
                );
                case "rectangle" -> {
                    int x = Math.min(startPoint.getX(), currentPoint.getX());
                    int y = Math.min(startPoint.getY(), currentPoint.getY());
                    int width = Math.abs(startPoint.getX() - currentPoint.getX());
                    int height = Math.abs(startPoint.getY() - currentPoint.getY());
                    g.drawRect(x, y, width, height);
                }
                case "circle" -> {
                    int radius = (int) Math.sqrt(
                            Math.pow(startPoint.getX() - currentPoint.getX(), 2) + Math.pow(startPoint.getY() - currentPoint.getY(), 2)
                    );
                    g.drawOval(
                            startPoint.getX() - radius, startPoint.getY() - radius,
                            2 * radius, 2 * radius
                    );
                }
                case "polygon" -> {
                    if (currentPolygon != null) {
                        ArrayList<Point> points = currentPolygon.getPoints();
                        for (int i = 0; i < points.size() - 1; i++) {
                            Point p1 = points.get(i);
                            Point p2 = points.get(i + 1);
                            g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                        }
                        g.drawLine(
                                points.get(points.size() - 1).getX(),
                                points.get(points.size() - 1).getY(),
                                currentPoint.getX(), currentPoint.getY()
                        );
                    }
                }

            }
        }
    }

    // Vymazání celé plochy
    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    // Překreslení rastru
    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }
}
