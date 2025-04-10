package Fillers;

import rasters.Raster;

import java.awt.*;

public class BasicFiller implements Filler {
    private Raster raster;

    public BasicFiller(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill(Point click, Color fillColor) {
        int baseColor = raster.getPixel((int) click.getX(), (int) click.getY());
        if (baseColor != fillColor.getRGB()) {
            recursiveFill(click, fillColor.getRGB(), baseColor);
        }
    }

    private void recursiveFill(Point point, int fillColor, int baseColor) {
        int x = (int) point.getX();
        int y = (int) point.getY();


        if (x < 0 || y < 0 || x >= raster.getWidth() || y >= raster.getHeight()) {
            return;
        }
        if (raster.getPixel(x, y) != baseColor) {
            return;
        }

        //vybarveni pixelu
        raster.setPixel(x, y, fillColor);


        recursiveFill(new Point(x + 1, y), fillColor, baseColor);
        recursiveFill(new Point(x - 1, y), fillColor, baseColor);
        recursiveFill(new Point(x, y + 1), fillColor, baseColor);
        recursiveFill(new Point(x, y - 1), fillColor, baseColor);
    }
}
