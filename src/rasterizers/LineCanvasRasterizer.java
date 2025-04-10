package rasterizers;

import models.Line;
import models.LineCanvas;
import models.Polygon;
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

    public void rasterizeCanvas(LineCanvas canvas) {
        lineRasterizer.rasterizeArray(canvas.getLines());
        dottedLineRasterizer.rasterizeArray(canvas.getDottedLines());
        for(Polygon polygon : canvas.getPolygons()) {
            this.rasterizePolygon(polygon);
        }
    }

    public void rasterizeLine(Line line) {
        lineRasterizer.rasterize(line);
    }

    public void rasterizePolygon(Polygon polygon) {
        models.Point prevPoint = null;
        for (models.Point point : polygon.getPoints()) {
            if(prevPoint == null) {
                prevPoint = point;
                continue;
            }

            lineRasterizer.rasterize(new Line(prevPoint, point, Color.white));

            prevPoint = point;
        }

        lineRasterizer.rasterize(new Line(prevPoint, polygon.getPoints().getFirst(), Color.white));
    }

    public void rasterizeDottedLine(Line line) {
        dottedLineRasterizer.rasterize(line);
    }

}