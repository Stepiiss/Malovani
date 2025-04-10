package rasterizers;

import models.Line;
import models.LineCanvas;
import rasters.Raster;

import java.awt.*;
import java.util.ArrayList;

public class DottedLineRasterizerTrivial implements Rasterizer {

    private Raster raster;

    public DottedLineRasterizerTrivial(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void setColor(Color color) {

    }

    @Override
    public void rasterize(Line line) {
        int x1 = line.getPoint1().getX();
        int y1 = line.getPoint1().getY();
        int x2 = line.getPoint2().getX();
        int y2 = line.getPoint2().getY();

        if(x1 < 0 || x1 >= raster.getWidth() || x2 < 0 || x2 >= raster.getWidth() || y1 < 0 || y1 >= raster.getHeight() || y2 < 0 || y2 >= raster.getHeight()) {
            return;
        }

        float k = (float) (y2 - y1) / (x2 - x1);
        float q = y1 - (k * x1);


        if (Math.abs(k) < 1) {
            if (x1 > x2) {
                int x = x1;
                x1 = x2;
                x2 = x;
            }

            for (int x = x1; x <= x2; x += 3) {
                int y = Math.round(k * x + q);

                raster.setPixel(x, y, line.getColor().getRGB());
            }
        } else {
            if (y1 > y2) {
                int y = y1;
                y1 = y2;
                y2 = y;
            }

            for (int y = y1; y < y2; y += 3) {
                int x = Math.round((y - q) / k);

                raster.setPixel(x, y, line.getColor().getRGB());
            }
        }
    }

    @Override
    public void rasterizeArray(ArrayList<Line> arrayList) {
        for (Line line : arrayList) {
            rasterize(line);
        }
    }

}