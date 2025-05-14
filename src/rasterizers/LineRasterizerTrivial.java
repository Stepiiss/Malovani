package rasterizers;

import models.Line;
import rasters.Raster;

import java.awt.*;
import java.util.ArrayList;

public class LineRasterizerTrivial implements Rasterizer {

    private Color color;
    private Raster raster;

    public LineRasterizerTrivial(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void setColor(Color color) {
        this.color = color; // Opraveno - přidáno this
    }

    @Override
    public void rasterize(Line line) {
        int x1 = line.getPoint1().getX();
        int y1 = line.getPoint1().getY();
        int x2 = line.getPoint2().getX();
        int y2 = line.getPoint2().getY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            raster.setPixel(x1, y1, line.getColor().getRGB());
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
    }

    @Override
    public void rasterizeArray(ArrayList<Line> arrayList) {
        for (Line line : arrayList) {
            rasterize(line);
        }

        raster.repaint(raster.getGraphics());
    }
}