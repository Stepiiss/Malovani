import models.Line;
import models.LineCanvas;
import models.Point;
import models.Polygon;
import rasterizers.LineCanvasRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class App {

    private final JPanel panel;
    private final Raster raster;
    private MouseAdapter mouseAdapter;
    private MouseMotionAdapter mouseMotionAdapter;
    private Point startPoint;
    private Point currentPoint; // Aktuální pozice kurzoru
    private LineCanvasRasterizer rasterizer;
    private LineCanvas canvas;
    private Polygon currentPolygon; // Aktuálně kreslený polygon

    private String currentTool = "line"; // Defaultní nástroj
    private Color currentColor = Color.BLACK;
    private boolean snapTo45 = false; // Příznak pro snapování na 45°
    private boolean dottedLine = false; // Příznak pro čárkovanou čáru

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    public App(int width, int height) {
        this.raster = new RasterBufferedImage(width, height);
        this.canvas = new LineCanvas(new ArrayList<>(), new ArrayList<>());
        this.rasterizer = new LineCanvasRasterizer(raster);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
                drawPreview(g); // Náhled aktuálního tvaru
            }
        };

        panel.setPreferredSize(new Dimension(width, height));
        createAdapters();
    }

    public void start() {
        JFrame frame = new JFrame("Jednoduché malování");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel tools = createToolbar();

        frame.add(tools, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

        clear(Color.WHITE.getRGB());
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton lineButton = new JButton("Čára");
        lineButton.addActionListener(e -> currentTool = "line");

        JButton rectButton = new JButton("Obdélník");
        rectButton.addActionListener(e -> currentTool = "rectangle");

        JButton circleButton = new JButton("Kružnice");
        circleButton.addActionListener(e -> currentTool = "circle");

        JButton polygonButton = new JButton("Polygon");
        polygonButton.addActionListener(e -> {
            currentTool = "polygon";
            currentPolygon = new Polygon();
        });

        JButton colorButton = new JButton("Barva");
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(panel, "Vyberte barvu", currentColor);
            if (selectedColor != null) {
                currentColor = selectedColor;
            }
        });

        toolbar.add(lineButton);
        toolbar.add(rectButton);
        toolbar.add(circleButton);
        toolbar.add(polygonButton);
        toolbar.add(colorButton);

        return toolbar;
    }

    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = new Point(e.getX(), e.getY());
                currentPoint = startPoint;

                if ("polygon".equals(currentTool)) {
                    if (currentPolygon == null) {
                        currentPolygon = new Polygon();
                    }
                    if (!currentPolygon.getPoints().isEmpty()) {
                        // Při polygonu vykreslíme čáru mezi posledním bodem a současným
                        Point lastPoint = currentPolygon.getPoints().get(currentPolygon.getPoints().size() - 1);
                        drawLine(lastPoint, startPoint);
                    }
                    currentPolygon.addPoint(startPoint);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentPoint = new Point(e.getX(), e.getY());

                if ("polygon".equals(currentTool) && e.getClickCount() == 2) {
                    // Dokončení kreslení polygonu
                    if (!currentPolygon.getPoints().isEmpty()) {
                        // Konečná čára spojující začátek a konec
                        drawLine(
                                currentPolygon.getPoints().get(currentPolygon.getPoints().size() - 1),
                                currentPolygon.getPoints().get(0)
                        );
                    }
                    canvas.add(currentPolygon);
                    currentPolygon = null; // Ukončit aktuální polygon
                } else {
                    switch (currentTool) {
                        case "line" -> {
                            if (dottedLine) {
                                drawDottedLine(startPoint, adjustPoint(currentPoint));
                            } else {
                                drawLine(startPoint, adjustPoint(currentPoint));
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
                panel.repaint();
            }
        };

        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseMotionAdapter);

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    snapTo45 = true; // Zapnutí snapování na 45°
                }
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    dottedLine = true; // Zapnutí čárkované čáry
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    snapTo45 = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    dottedLine = false;
                }
            }
        });
        panel.setFocusable(true);
    }

    public void drawLine(Point start, Point end) {
        Line line = new Line(start, end, currentColor);
        rasterizer.rasterizeLine(line);
    }

    public void drawDottedLine(Point start, Point end) {
        Line line = new Line(start, end, currentColor);
        rasterizer.rasterizeDottedLine(line);
    }

    public void drawRectangle(Point topLeft, Point bottomRight) {
        Point topRight = new Point(bottomRight.getX(), topLeft.getY());
        Point bottomLeft = new Point(topLeft.getX(), bottomRight.getY());

        drawLine(topLeft, topRight);
        drawLine(topRight, bottomRight);
        drawLine(bottomRight, bottomLeft);
        drawLine(bottomLeft, topLeft);
    }

    public void drawCircle(Point start, Point end) {
        int radius = (int) Math.sqrt(
                Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2)
        );

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
    }

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

    private Point adjustPoint(Point target) {
        if (snapTo45 && startPoint != null) {
            return snapToNearest45deg(startPoint, target);
        }
        return target;
    }

    public static Point snapToNearest45deg(Point p1, Point p2) {
        int dx = p2.getX() - p1.getX();
        int dy = p2.getY() - p1.getY();

        if (Math.abs(dx) > Math.abs(dy)) {
            dy = 0;
        } else {
            dx = 0;
        }
        return new Point(p1.getX() + dx, p1.getY() + dy);
    }

    private void drawPreview(Graphics g) {
        if (startPoint != null && currentPoint != null) {
            g.setColor(currentColor);

            switch (currentTool) {
                case "line" -> g.drawLine(
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
                            Math.pow(startPoint.getX() - currentPoint.getX(), 2)
                                    + Math.pow(startPoint.getY() - currentPoint.getY(), 2)
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

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }
}