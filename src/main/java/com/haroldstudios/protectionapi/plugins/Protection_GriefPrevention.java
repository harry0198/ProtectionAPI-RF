package com.haroldstudios.protectionapi.plugins;

import com.haroldstudios.protectionapi.components.Region3D;
import com.haroldstudios.protectionapi.components.UniversalRegion;
import com.haroldstudios.protectionapi.protection.Protection;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Protection_GriefPrevention implements Protection {

    private final String name = "GriefPrevention";

    private GriefPrevention griefPrevention;

    public Protection_GriefPrevention(Plugin plugin) {
        Logger log = plugin.getLogger();

        if (griefPrevention == null) {
            Plugin griefPrevention = plugin.getServer().getPluginManager().getPlugin(name);
            if (griefPrevention != null && griefPrevention.isEnabled()) {
                this.griefPrevention =  (GriefPrevention) griefPrevention;
                log.info(String.format("[ProtectionAPI] %s hooked.", name));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return griefPrevention != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<UniversalRegion> getRegions() {

        Collection<Claim> regions = griefPrevention.dataStore.getClaims();

        return regions.stream().map(this::createRegion).collect(Collectors.toList());
    }

    @Override
    public Collection<UniversalRegion> getRegions(World world) {
        return getRegions().stream().filter(region -> region.getWorld().equals(world)).collect(Collectors.toList());
    }

    @Override
    public boolean supportsPolygonRegions() {
        return false;
    }

    @Override
    public Object getExternalInstance() {
        return griefPrevention;
    }

    private UniversalRegion createRegion(Claim region) {

        UniversalRegion universalRegion = new Region3D(region.getLesserBoundaryCorner().getWorld(), region.getLesserBoundaryCorner().toVector(), region.getGreaterBoundaryCorner().toVector())
                .setRegionOwners(Collections.singletonList(region.ownerID))
                .setRegionMembers(null) // The actual fuck is this api - can't get members!
                .setRegionProvider(this.getName());

        List<UniversalRegion> children = new ArrayList<>();

        if (region.children != null) {
            region.children.forEach(child -> children.add(createRegion(child)));
        }

        universalRegion.setChildRegions(children);

        return universalRegion;

    }
}
