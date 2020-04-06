package com.haroldstudios.protectionapi.plugins;

import com.haroldstudios.protectionapi.protection.Protection;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.haroldstudios.protectionapi.components.Region3D;
import com.haroldstudios.protectionapi.components.UniversalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Protection_WorldGuard implements Protection {

    private final String name = "WorldGuard";
    private Plugin plugin;
    private WorldGuardPlugin worldGuard;

    public Protection_WorldGuard(Plugin plugin) {
        this.plugin = plugin;
        Logger log = plugin.getLogger();

        if (worldGuard == null) {
            Plugin worldGuard = plugin.getServer().getPluginManager().getPlugin(name);
            if (worldGuard != null && worldGuard.isEnabled()) {
                this.worldGuard = (WorldGuardPlugin) worldGuard;
                log.info(String.format("[ProtectionAPI] %s hooked.", name));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return worldGuard != null;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public Collection<UniversalRegion> getRegions() {

        Collection<UniversalRegion> regions = new ArrayList<>();
        for (World world : this.plugin.getServer().getWorlds()) {
            regions.addAll(getRegions(world));
        }
        return regions;
    }

    @Override
    public Collection<UniversalRegion> getRegions(World world) {
        RegionManager t = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        if (t == null) return Collections.emptyList();

        return t.getRegions().values().stream().map(region -> createRegion(world, region)).collect(Collectors.toList());

    }

    @Override
    public boolean supportsPolygonRegions() {
        return true;
    }

    @Override
    public Object getExternalInstance() {
        return worldGuard;
    }

    // Creates region based from protected region
    private UniversalRegion createRegion(World world, ProtectedRegion region) {

        int minY = region.getMinimumPoint().getBlockY();
        int maxY = region.getMaximumPoint().getBlockY();

        /* TODO Improve here */
        List<Vector> points = new ArrayList<>();

        int i = 0;

        for (BlockVector2 point : region.getPoints()) {

            Vector v = new Vector(point.getBlockX(), maxY, point.getBlockZ());

            if (i == 0) v.setY(minY);
            points.add(v);

            // Global region not supported yet --
            if (region.getId().equals("__global__")) points.add(v);
            i++;
        }

        return new Region3D(world, points.toArray(new Vector[0]))
                .setRegionOwners(region.getOwners().getUniqueIds())
                .setRegionMembers(region.getMembers().getUniqueIds())
                .setRegionProvider(this.getName())
                .setWelcomeMessages(region.getFlag(Flags.GREET_MESSAGE)) //TODO check if this is actually getting the messages
                .setFarewellMessage(region.getFlag(Flags.FAREWELL_MESSAGE));
    }

}
