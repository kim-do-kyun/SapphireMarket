package org.desp.sapphireMarket.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.desp.sapphireMarket.database.PlayerDataRepository;
import org.desp.sapphireMarket.database.PlayerIndividualPurchaseRepository;

public class PlayerJoinAndQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataRepository.getInstance().loadPlayerData(player);
        PlayerIndividualPurchaseRepository.getInstance().registerItem(player);
        PlayerIndividualPurchaseRepository.getInstance().loadPlayerPurchaseCache(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerDataRepository.getInstance().savePlayerData(player);
        PlayerIndividualPurchaseRepository.getInstance().saveDBIndividualPurchaseData(player);
    }
}
