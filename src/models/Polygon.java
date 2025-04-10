package models;

import rasterizers.LineCanvasRasterizer;

import java.awt.*;
import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> vertices;

    public Polygon() {
        vertices = new ArrayList<>();
    }

    public Polygon(ArrayList<Point> vertices) {
        this.vertices = vertices;
    }

    public void addPoint(Point point) {
        vertices.add(point);
    }

    public ArrayList<Point> getPoints() {
        return vertices;
    }

    public void draw(LineCanvasRasterizer rasterizer, Color color) {
        for (int i = 0; i < vertices.size(); i++) {
            Point start = vertices.get(i);
            Point end = vertices.get((i + 1) % vertices.size());
            Line line = new Line(start, end, color);
            rasterizer.rasterizeLine(line);
        }
    }
}