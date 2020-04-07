package com.haroldstudios.protectionapi.components;

import com.google.common.collect.ImmutableList;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Area2D {

    private ImmutableList<Point> points;

    public Area2D(final List<Vector> points) {
        // If we only have min and max corner of area
        if (points.size() == 2) {
            List<Point> pointList = new ArrayList<>();

            // get all
            Point p1 = new Point(points.get(0).getBlockX(), points.get(0).getBlockZ());
            Point p2 = new Point(points.get(1).getBlockX(), points.get(1).getBlockZ());
            Point p3 = new Point(p1.x, p2.y);
            Point p4 = new Point(p2.x, p1.y);

            pointList.add(p1);
            pointList.add(p2);
            pointList.add(p3);
            pointList.add(p4);

            this.points = ImmutableList.copyOf(pointList);

            return;
        }

        // If is polygon
        List<Point> pointList = new ArrayList<>();
        // Add all 2d points to point list
        points.forEach( vect -> pointList.add(new Point(vect.getBlockX(), vect.getBlockZ())) );
        this.points = ImmutableList.copyOf(pointList);

    }

    public List<Point> getPoints() {
        return this.points;
    }
}
