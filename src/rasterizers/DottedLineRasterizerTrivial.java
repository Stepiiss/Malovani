package rasterizers;

import models.Line;
import rasters.Raster;

import java.awt.*;
import java.util.ArrayList;

public class DottedLineRasterizerTrivial implements Rasterizer {

    private Raster raster;
    private Color color;

    public DottedLineRasterizerTrivial(Raster raster) {
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

        // Ošetření hranic rastru
        if (x1 < 0 || x1 >= raster.getWidth() || x2 < 0 || x2 >= raster.getWidth() ||
                y1 < 0 || y1 >= raster.getHeight() || y2 < 0 || y2 >= raster.getHeight()) {
            return;
        }

        // Použití Bresenhamova algoritmu s preskakováním pixelů
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        int dotSpacing = 3; // Mezera mezi tečkami
        int dotCounter = 0;

        while (true) {
            // Vykreslit pixel pouze když je čas (každý dotSpacing krok)
            if (dotCounter % dotSpacing == 0) {
                raster.setPixel(x1, y1, line.getColor().getRGB());
            }
            dotCounter++;

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
    }
}