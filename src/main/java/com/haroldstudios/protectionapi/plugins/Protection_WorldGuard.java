package com.haroldstudios.protectionapi.plugins;

import com.haroldstudios.protectionapi.protection.UniversalProtection;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.haroldstudios.protectionapi.components.Region3D;
import com.haroldstudios.protectionapi.components.UniversalRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Protection_WorldGuard extends UniversalProtection {

    private final String name = "WorldGuard";
    private Plugin plugin;
    private WorldGuardPlugin worldGuard;

    public Protection_WorldGuard(Plugin plugin) {
        this.plugin = plugin;
        Logger log = plugin.getLogger();
        getRegions();

        if (worldGuard == null) {
            Plugin worldGuard = plugin.getServer().getPluginManager().getPlugin(name);
            if (worldGuard != null && worldGuard.isEnabled()) {
                this.worldGuard = (WorldGuardPlugin) worldGuard;
                log.info(String.format("[ProtectionAPI] %s hooked.", name));
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public Collection<UniversalRegion> getRegions() {

        long current = System.currentTimeMillis();
        Collection<UniversalRegion> regions = new ArrayList<>();
        for (World world : this.plugin.getServer().getWorlds()) {
            regions.addAll(getRegions(world));
        }

        //TODO This is a stress test - remove once publishing
        for (int i = 0; i < 100; i++) {
            regions.add(new Region3D(Bukkit.getWorld("world"), new Vector(i,i,i), new Vector(i,i,i+1)));
        }

        System.out.println("ms: " + (System.currentTimeMillis() - current));
        return regions;
    }

    @Override
    public Collection<UniversalRegion> getRegions(World world) {
        RegionManager t = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        if (t == null) return Collections.emptyList();

        return t.getRegions().values().stream().map(region -> new Region3D(world, region.getPoints().stream().map(
                points -> new Vector(points.getBlockX(), region.getMinimumPoint().getBlockY(), points.getBlockZ())).toArray(Vector[]::new))
                .setRegionOwners(region.getOwners().getUniqueIds())
                .setRegionMembers(region.getMembers().getUniqueIds())
                .setRegionProvider(this.getName())
                .setWelcomeMessages(region.getFlag(Flags.GREET_MESSAGE))).collect(Collectors.toList());

    }

    @Override
    public boolean supportsPolygonRegions() {
        return true;
    }

    @Override
    public Collection<UniversalRegion> getPlayerClaims(Player player) {

        return null;
    }

    @Override
    public Collection<UniversalRegion> getPlayerClaims(World world, Player player) {
        return null;
    }
}
