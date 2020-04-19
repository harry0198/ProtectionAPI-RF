package com.haroldstudios.protectionapi.plugins;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import com.haroldstudios.protectionapi.components.Region3D;
import com.haroldstudios.protectionapi.components.UniversalRegion;
import com.haroldstudios.protectionapi.protection.Protection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Protection_RedProtect implements Protection {

    private final String name = "RedProtect";

    private RedProtect redProtect;

    public Protection_RedProtect(Plugin plugin) {
        Logger log = plugin.getLogger();

        if (redProtect == null) {
            Plugin redProtect = plugin.getServer().getPluginManager().getPlugin(name);
            if (redProtect != null && redProtect.isEnabled()) {

                this.redProtect = (RedProtect) redProtect;
                log.info(String.format("[ProtectionAPI] %s hooked.", name));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return redProtect != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<UniversalRegion> getRegions() {

        List<UniversalRegion> regions = new ArrayList<>();

        redProtect.getAPI().getAllRegions().forEach(region -> {
            regions.add(new Region3D(
                    Bukkit.getWorld(region.getWorld()), new Vector(region.getMinMbrX(), region.getMinY(), region.getMinMbrZ()), new Vector(region.getMaxMbrX(), region.getMaxY(), region.getMaxMbrZ()))
                    .setRegionOwners(region.getLeaders().stream().filter(leader -> !leader.getUUID().equals("#server#")).map(leader -> UUID.fromString(leader.getUUID())).collect(Collectors.toList()))
                    .setRegionAdmins(region.getAdmins().stream().map(admin -> UUID.fromString(admin.getUUID())).collect(Collectors.toList()))
                    .setRegionMembers(region.getMembers().stream().map(member -> UUID.fromString(member.getUUID())).collect(Collectors.toList()))
                    .setRegionProvider(name)
            );
        });
        return regions;
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
        return redProtect;
    }
}
