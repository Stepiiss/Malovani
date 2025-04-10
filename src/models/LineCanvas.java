package models;

import java.util.ArrayList;

public class LineCanvas {

    private ArrayList<Line> lines;
    private ArrayList<Line> dottedLines;
    private ArrayList<Polygon> polygons;

    public LineCanvas(
            ArrayList<Line> lines,
            ArrayList<Line> dottedLines
    ) {
        this.lines = lines;
        this.dottedLines = dottedLines;
        this.polygons = new ArrayList<>();
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public ArrayList<Line> getDottedLines() {
        return dottedLines;
    }

    public ArrayList<Polygon> getPolygons() {
        return this.polygons;
    }

    public void add(Line line) {
        lines.add(line);
    }

    public void addDottedLine(Line line) {
        dottedLines.add(line);
    }

    public void add(Polygon polygon) {
        polygons.add(polygon);
    }

    public void clear() {
        lines.clear();
        dottedLines.clear();
        polygons.clear();
    }

}