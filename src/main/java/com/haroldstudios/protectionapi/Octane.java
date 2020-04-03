package com.haroldstudios.protectionapi;

import org.bukkit.util.Vector;

public class Octane {

    private Vector min, max;

    public Octane(Vector min, Vector max){

        this.min = min;
        this.max = max;

    }

    public Vector getMax() {
        return max;
    }

    public Vector getMin() {
        return min;
    }

    public boolean isBetweenAxis(Vector point){

        // For readability
        int minX = min.getBlockX();
        int minY = min.getBlockY();
        int minZ = min.getBlockZ();

        int maxX = max.getBlockX();
        int maxY = max.getBlockY();
        int maxZ = max.getBlockZ();

        int pX = point.getBlockX();
        int pY = point.getBlockY();
        int pZ = point.getBlockZ();


        return ((pX > minX) && (pX < maxX) && (pY > minY) && (pY < maxY) && (pZ > minZ) && (pZ < maxZ));


    }


}
