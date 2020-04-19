package com.haroldstudios.protectionapi;

import org.bukkit.util.Vector;

public final class Octane {

    private final Vector min,
                         max;

    /**
     * Class Constructor
     * @param min Minimum corner of octane
     * @param max Maximum corner of octane
     */
    public Octane(Vector min, Vector max){

        this.min = min;
        this.max = max;

    }

    /**
     * Gets Maximum corner of octane
     * @return Maximum point of Octane as Vector
     */
    public Vector getMax() {
        return max;
    }

    /**
     * Gets Minimum corner of octane
     * @return Minimum point of Octane as Vector
     */
    public Vector getMin() {
        return min;
    }

    /**
     * Checks if the vector point is between the Octane Bounds
     * @param point Vector point to check
     * @return If point is between the octane's boundaries
     */
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
