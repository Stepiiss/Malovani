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


    public void rasterizeLine(Line line) {
        lineRasterizer.rasterize(line);
    }


    public void rasterizeDottedLine(Line line) {
        dottedLineRasterizer.rasterize(line);
    }

}