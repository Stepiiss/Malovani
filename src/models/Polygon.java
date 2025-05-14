package models;

import rasterizers.LineCanvasRasterizer;

import java.awt.*;
import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> vertices;

    public Polygon() {
        vertices = new ArrayList<>();
    }


    public void addPoint(Point point) {
        vertices.add(point);
    }

    public ArrayList<Point> getPoints() {
        return vertices;
    }


}