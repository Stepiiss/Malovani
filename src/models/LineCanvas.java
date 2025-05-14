package models;

import java.awt.*;
import java.util.ArrayList;

public class LineCanvas {
    private ArrayList<GraphicsObject> objects;

    public LineCanvas() {
        this.objects = new ArrayList<>();
    }

    public LineCanvas(ArrayList<Line> lines, ArrayList<Line> dottedLines) {
        this();
        // Přidání existujících čar
        if (lines != null) {
            for (Line line : lines) {
                objects.add(line);
            }
        }

        // Přidání tečkovaných čar
        if (dottedLines != null) {
            for (Line line : dottedLines) {
                objects.add(line);
            }
        }
    }

    public void add(GraphicsObject object) {
        objects.add(object);
    }

    public void add(Line line) {
        objects.add(line);
    }

    public void addDottedLine(Line line) {
        Line dottedLine = new Line(line.getPoint1(), line.getPoint2(), line.getColor(), true);
        objects.add(dottedLine);
    }

    public void add(Polygon polygon) {
        objects.add(polygon);
    }

    public ArrayList<GraphicsObject> getObjects() {
        return objects;
    }

    // Pro zachování zpětné kompatibility
    public ArrayList<Line> getLines() {
        ArrayList<Line> lines = new ArrayList<>();
        for (GraphicsObject obj : objects) {
            if (obj instanceof Line line && !line.isDotted()) {
                lines.add(line);
            }
        }
        return lines;
    }

    public ArrayList<Line> getDottedLines() {
        ArrayList<Line> dottedLines = new ArrayList<>();
        for (GraphicsObject obj : objects) {
            if (obj instanceof Line line && line.isDotted()) {
                dottedLines.add(line);
            }
        }
        return dottedLines;
    }

    public ArrayList<Polygon> getPolygons() {
        ArrayList<Polygon> polygons = new ArrayList<>();
        for (GraphicsObject obj : objects) {
            if (obj instanceof Polygon) {
                polygons.add((Polygon) obj);
            }
        }
        return polygons;
    }

    public void draw(rasterizers.LineCanvasRasterizer rasterizer) {
        for (GraphicsObject object : objects) {
            object.draw(rasterizer);
        }
    }

    public void clear() {
        objects.clear();
    }
}