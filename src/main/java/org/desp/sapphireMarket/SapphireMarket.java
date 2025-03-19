package org.desp.sapphireMarket;

import java.util.Collection;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.desp.sapphireMarket.command.ItemRegisterCommand;
import org.desp.sapphireMarket.command.SapphireMarketCommand;
import org.desp.sapphireMarket.command.SapphireMarketETCCommand;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.desp.sapphireMarket.database.PlayerDataRepository;
import org.desp.sapphireMarket.database.PlayerIndividualPurchaseRepository;
import org.desp.sapphireMarket.listener.ItemConfirmListener;
import org.desp.sapphireMarket.listener.ItemSelectListener;
import org.desp.sapphireMarket.listener.PlayerJoinAndQuitListener;

public final class SapphireMarket extends JavaPlugin {

    @Getter
    private static SapphireMarket instance;

    @Override
    public void onEnable() {
        instance = this;
        ItemDataRepository.getInstance().loadItemData();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            PlayerDataRepository.getInstance().loadPlayerData(player);
            PlayerIndividualPurchaseRepository.getInstance().registerItem(player);
            PlayerIndividualPurchaseRepository.getInstance().loadPlayerPurchaseCache(player);
        }

        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemSelectListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemConfirmListener(), this);

        getCommand("사파이어").setExecutor(new SapphireMarketETCCommand());
        getCommand("사파이어상점").setExecutor(new SapphireMarketCommand());
        getCommand("사파이어아이템등록").setExecutor(new ItemRegisterCommand());




    }

    @Override
    public void onDisable() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            PlayerDataRepository.getInstance().savePlayerData(player);
            PlayerIndividualPurchaseRepository.getInstance().saveDBIndividualPurchaseData(player);
        }
    }
}
