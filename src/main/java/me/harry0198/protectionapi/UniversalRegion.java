package me.harry0198.protectionapi;

import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UniversalRegion {

    private Vector loc1, loc2, midpoint;
    private World world;
    private List<UUID> owners, members;
    private String claimPluginName;

    public static Vector getMinFromPoints(Vector loc1, Vector loc2){
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        return new Vector(x1, y1, z1);
    }

    public static Vector getMaxFromPoints(Vector loc1, Vector loc2){
        int x1 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        return new Vector(x1, y1, z1);
    }

    //
    // GETTERS
    //

    public List<UUID> getMembers() {
        return members;
    }

    public List<UUID> getOwners() {
        return owners;
    }

    public Vector getMin() {
        return loc1;
    }

    public Vector getMax() {
        return loc2;
    }

    public World getWorld() {
        return world;
    }

    public String getClaimPluginName() { return claimPluginName; }

    public Vector getMidpoint() { return midpoint; }

    //
    // SETTERS
    //


    public void setOwners(List<UUID> owners) {
        this.owners = owners;
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    public void setLoc1(Vector loc1) {
        this.loc1 = loc1;
    }

    public void setLoc2(Vector loc2) {
        this.loc2 = loc2;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setClaimPluginName(String string) { this.claimPluginName = string; }

    public static class Builder {

        private Vector min, max, midpoint;
        private List<UUID> owners, members = new ArrayList<>();
        private World world;
        private String claimPluginName;

        public Builder(Vector min, Vector max, World world) {
            this.min = min;
            this.max = max;
            this.world = world;

            int x = max.getBlockX() - min.getBlockX();
            int y = max.getBlockY() - min.getBlockY();
            int z = max.getBlockZ() - min.getBlockZ();

            int b = (x * x) + (z * z);
            int hypotenuse = (b * b) + (y * y) / 2;
            this.midpoint = new Vector(min.getBlockX() + hypotenuse, min.getBlockY() + hypotenuse, min.getBlockZ() + hypotenuse);

        }
        public Builder setOwners(List<UUID> owners){
            this.owners = owners;
            return this;
        }
        public Builder setMembers(List<UUID> members){
            this.members = members;
            return this;
        }
        public Builder setClaimPluginName(String claimPluginName){
            this.claimPluginName = claimPluginName;
            return this;
        }


        public UniversalRegion build(){
            UniversalRegion region = new UniversalRegion();

            region.setLoc1(UniversalRegion.getMinFromPoints(min, max));
            region.setLoc2(UniversalRegion.getMaxFromPoints(min, max));
            region.setWorld(world);
            region.midpoint = midpoint;
            region.setOwners(owners);
            region.setMembers(members);
            region.setClaimPluginName(claimPluginName);


            return region;
        }

        }
}
