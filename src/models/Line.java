package models;

import java.awt.*;

public class Line extends GraphicsObject {
    private Point point1;
    private Point point2;
    private boolean dotted;

    public Line(Point point1, Point point2, Color color) {
        this(point1, point2, color, false);
    }

    public Line(Point point1, Point point2, Color color, boolean dotted) {
        super(color);
        this.point1 = point1;
        this.point2 = point2;
        this.dotted = dotted;
    }

    public Point getPoint1() {
        return point1;
    }

    public Point getPoint2() {
        return point2;
    }

    public boolean isDotted() {
        return dotted;
    }

    @Override
    public void draw(rasterizers.LineCanvasRasterizer rasterizer) {
        if (dotted) {
            rasterizer.rasterizeDottedLine(this);
        } else {
            rasterizer.rasterizeLine(this);
        }
    }
}