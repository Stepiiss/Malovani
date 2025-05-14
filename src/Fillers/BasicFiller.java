package Fillers;

import rasters.Raster;

import java.awt.*;
import java.util.Stack;

public class BasicFiller implements Filler {
    private Raster raster;

    public BasicFiller(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill(Point click, Color fillColor) {
        int baseColor = raster.getPixel((int) click.getX(), (int) click.getY());
        if (baseColor == fillColor.getRGB()) {
            return;
        }

        // Použití zásobníku místo rekurze
        Stack<Point> stack = new Stack<>();
        stack.push(click);

        while (!stack.isEmpty()) {
            Point current = stack.pop();
            int x = (int) current.getX();
            int y = (int) current.getY();

            if (x < 0 || y < 0 || x >= raster.getWidth() || y >= raster.getHeight()) {
                continue;
            }

            if (raster.getPixel(x, y) != baseColor) {
                continue;
            }

            raster.setPixel(x, y, fillColor.getRGB());

            stack.push(new Point(x + 1, y));
            stack.push(new Point(x - 1, y));
            stack.push(new Point(x, y + 1));
            stack.push(new Point(x, y - 1));
        }
    }
}