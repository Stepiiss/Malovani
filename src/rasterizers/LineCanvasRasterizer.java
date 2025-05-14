package rasterizers;

import models.Line;
import models.GraphicsObject;
import rasters.Raster;

import java.awt.*;

public class LineCanvasRasterizer {
    private Raster raster;
    private Rasterizer lineRasterizer;
    private Rasterizer dottedLineRasterizer;

    public LineCanvasRasterizer(Raster raster) {
        this.raster = raster;
        lineRasterizer = new LineRasterizerTrivial(raster);
        dottedLineRasterizer = new DottedLineRasterizerTrivial(raster);
    }

    public void rasterizeLine(Line line) {
        lineRasterizer.rasterize(line);
    }

    public void rasterizeDottedLine(Line line) {
        dottedLineRasterizer.rasterize(line);
    }

    // Pomocná metoda pro kreslení bodů kružnice
    public void drawCirclePoints(int cx, int cy, int x, int y, Color color) {
        raster.setPixel(cx + x, cy + y, color.getRGB());
        raster.setPixel(cx - x, cy + y, color.getRGB());
        raster.setPixel(cx + x, cy - y, color.getRGB());
        raster.setPixel(cx - x, cy - y, color.getRGB());
        raster.setPixel(cx + y, cy + x, color.getRGB());
        raster.setPixel(cx - y, cy + x, color.getRGB());
        raster.setPixel(cx + y, cy - x, color.getRGB());
        raster.setPixel(cx - y, cy - x, color.getRGB());
    }

    // Jednotná metoda pro kreslení kružnice
    public void drawCircle(int centerX, int centerY, int radius, Color color) {
        int x = 0, y = radius;
        int d = 3 - 2 * radius;

        while (y >= x) {
            drawCirclePoints(centerX, centerY, x, y, color);
            x++;
            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            } else {
                d = d + 4 * x + 6;
            }
        }
    }
}