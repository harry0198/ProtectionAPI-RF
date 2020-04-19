package com.haroldstudios.protectionapi.plugins;

import com.haroldstudios.protectionapi.components.Region3D;
import com.haroldstudios.protectionapi.components.UniversalRegion;
import com.haroldstudios.protectionapi.protection.Protection;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// Towny is not very efficient for us in its data handling. Thus it will result in creating lots of UniversalRegions
// Due to its TownBlocks which essentially split up the world where claimed as it doesn't support polygon regions e.g one big outline.
// On the positives, it allows us to handle plots easier.
public final class Protection_Towny implements Protection {

    private final String name = "Towny";
    private Towny towny;

    public Protection_Towny(Plugin plugin) {

        Logger log = plugin.getLogger();

        if (towny == null) {
            Plugin towny = plugin.getServer().getPluginManager().getPlugin(name);
            if (towny != null && towny.isEnabled()) {
                this.towny = (Towny) towny;
                log.info(String.format("[ProtectionAPI] %s hooked.", name));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return towny != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Collection<UniversalRegion> getRegions() {

        List<UniversalRegion> regions = new ArrayList<>();

        // Town Blocks are sections of the world that are divided into plots within a town
        // Players can buy these plots and thus have their own management system

        // Size of Section
        int townBlockSize = TownySettings.getTownBlockSize();

        for (Town town : TownyAPI.getInstance().getDataSource().getTowns()) {
            for (TownBlock block : town.getTownBlocks()){

                WorldCoord worldCoord = block.getWorldCoord();
                int townBlockHeight = worldCoord.getBukkitWorld().getMaxHeight() - 1;

                int blockX = 0;
                int blockZ = 0;
                for (int x = 0; x < townBlockSize; ++x) {
                    for (int z = 0; z < townBlockSize; ++z) {
                        for (int y = townBlockHeight; y > 0; --y) {
                            blockX = worldCoord.getX() * townBlockSize + x - (townBlockSize / 2);
                            blockZ = worldCoord.getZ() * townBlockSize + z - (townBlockSize / 2);
                        }
                    }
                }

                Vector min = new Vector(
                        (blockX - (townBlockSize / 2) + 1),
                        -1,
                        blockZ - (townBlockSize / 2) + 1);
                Vector max = new Vector(
                        blockX + (townBlockSize / 2),
                        256,
                        blockZ + (townBlockSize / 2));

                // Town block resident
                UUID[] resident = new UUID[1];
                boolean registered = true;
                try {
                    block.getResident();
                } catch (NotRegisteredException e) {
                    registered = false;
                }

                try {
                    if (registered)
                        resident[0] = Bukkit.getOfflinePlayer(block.getResident().getName()).getUniqueId();
                } catch (NotRegisteredException ignore) {}

                regions.add(new Region3D(worldCoord.getBukkitWorld(), min, max)
                        .setRegionProvider(name)
                        .setRegionOwners(Collections.singletonList(Bukkit.getOfflinePlayer(town.getMayor().getName()).getUniqueId()))
                        .setRegionAdmins(town.getAssistants().stream().map(residents -> Bukkit.getOfflinePlayer(residents.getName()).getUniqueId()).collect(Collectors.toList()))
                        .setRegionMembers(Arrays.asList(resident))
                );
            }
        }

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
        return towny;
    }
}
