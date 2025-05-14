package models;

import java.awt.*;
import java.util.ArrayList;

public class Polygon extends GraphicsObject {
    private ArrayList<Point> vertices;

    public Polygon() {
        this(Color.WHITE);
    }

    public Polygon(Color color) {
        super(color);
        vertices = new ArrayList<>();
    }

    public void addPoint(Point point) {
        vertices.add(point);
    }

    public ArrayList<Point> getPoints() {
        return vertices;
    }

    @Override
    public void draw(rasterizers.LineCanvasRasterizer rasterizer) {
        // Vykreslení všech hran polygonu
        if (vertices.size() < 2) return;

        for (int i = 0; i < vertices.size(); i++) {
            Point p1 = vertices.get(i);
            Point p2 = vertices.get((i + 1) % vertices.size());
            Line edge = new Line(p1, p2, color);
            rasterizer.rasterizeLine(edge);
        }
    }
}