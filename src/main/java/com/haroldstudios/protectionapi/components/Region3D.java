package com.haroldstudios.protectionapi.components;

import com.google.common.collect.ImmutableList;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.awt.*;
import java.awt.geom.Area;

public final class Region3D extends UniversalRegion{

    private ImmutableList<Vector> points;
    private Area2D planePoints;
    private double minY;
    private double maxY;

    /**
     * Class Constructor
     * @param world World region is based in
     * @param points Points of region e.g Min and Max or Polygon points
     */
    public Region3D(World world, Vector... points) {
        super(world);
        if (points.length < 2) throw new IllegalArgumentException("Must have 2 or more vector points!");

        this.points = ImmutableList.copyOf(points);
        this.minY = getMinYValue(points);
        this.maxY = getMaxYValue(points);

        planePoints = new Area2D(this.points);
        setMinMaxPoint(this.points);
    }

    private static double getMaxYValue(Vector[] points) {

        double maxVal = points[0].getY();
        for (Vector point : points) {
            if (point.getBlockY() > maxVal) {
                maxVal = point.getY();
            }
        }
        return maxVal;
    }

    private static double getMinYValue(Vector[] points) {
        double minVal = points[0].getY();
        for (Vector point : points) {
            if (point.getBlockY() < minVal) {
                minVal = point.getY();
            }
        }
        return minVal;
    }

    /**
     * Get Minimum Y Coord
     * @return Min Y Coord
     */
    public double getMinY() {
        return minY;
    }

    /**
     * Get Maximum Y Coord
     * @return Max Y Coord
     */
    public double getMaxY() {
        return maxY;
    }

    @Override
    public Area getArea() {

        int numPoints = planePoints.getPoints().size();
        int[] xCoords = new int[numPoints];
        int[] yCoords = new int[numPoints];

        int i = 0;
        for (Point point : planePoints.getPoints()) {
            xCoords[i] = point.x; // X
            yCoords[i] = point.y; // Z
            i++;
        }

        Polygon polygon = new Polygon(xCoords, yCoords, numPoints);
        return new Area(polygon);
    }

}
