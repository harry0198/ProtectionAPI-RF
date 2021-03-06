package com.haroldstudios.protectionapi.plugins;


import com.github.intellectualsites.plotsquared.api.PlotAPI;
import com.github.intellectualsites.plotsquared.bukkit.BukkitMain;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Protection_PlotSquared implements Protection {

        private final String name = "PlotSquared";
        private BukkitMain plotSquared;
        private PlotAPI plotAPI;


        public Protection_PlotSquared(Plugin plugin) {
                Logger log = plugin.getLogger();

                if (plotSquared == null) {
                        Plugin plotSquared = plugin.getServer().getPluginManager().getPlugin(name);
                        if (plotSquared != null && plotSquared.isEnabled()) {
                                this.plotSquared = (BukkitMain) plotSquared;
                                this.plotAPI = new PlotAPI();
                                log.info(String.format("%s hooked.", name));
                        }
                }
        }

        @Override
        public boolean isEnabled() {
                return plotSquared != null;
        }

        @Override
        public String getName() {
                return name;
        }

        @Override
        public Collection<UniversalRegion> getRegions() {

                List<UniversalRegion> regions = new ArrayList<>();

                for (Plot plot : plotAPI.getAllPlots()) {

                        Vector[] vectors = plot.getAllCorners().stream().map(location -> new Vector(location.getX(), location.getY(), location.getZ())).toArray(Vector[]::new);

                        regions.add(new Region3D(
                                Bukkit.getWorld(plot.getWorldName()), vectors)
                                .setRegionProvider(name)
                                .setRegionAdmins(new ArrayList<>(plot.getOwners()))
                                .setRegionMembers(new ArrayList<>(plot.getMembers())
                        ));
                }
                return regions;
        }

        @Override
        public Collection<UniversalRegion> getRegions(World world) {
                return getRegions().stream().filter(region -> region.getWorld().equals(world)).collect(Collectors.toList());
        }

        @Override
        public boolean supportsPolygonRegions() {
                return true;
        }

        @Override
        public Object getExternalInstance() {
                return plotSquared;
        }
}
