package org.desp.sapphireMarket;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.desp.sapphireMarket.command.ItemRegisterCommand;
import org.desp.sapphireMarket.command.SapphireMarketCommand;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.desp.sapphireMarket.listener.PlayerJoinAndQuitListener;

public final class SapphireMarket extends JavaPlugin {

    @Getter
    private static SapphireMarket instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndQuitListener(), this);
        getCommand("사파이어상점").setExecutor(new SapphireMarketCommand());
        getCommand("아이템등록").setExecutor(new ItemRegisterCommand());

        ItemDataRepository.getInstance().loadItemData();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
