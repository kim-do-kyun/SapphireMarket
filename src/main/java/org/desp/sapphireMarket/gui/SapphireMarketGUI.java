package org.desp.sapphireMarket.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.desp.sapphireMarket.database.PlayerIndividualPurchaseRepository;
import org.desp.sapphireMarket.dto.ItemDataDto;
import org.desp.sapphireMarket.utils.ItemParser;
import org.jetbrains.annotations.NotNull;

public class SapphireMarketGUI implements InventoryHolder {

    private Player player;
    public Inventory inventory;

    public SapphireMarketGUI(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if(this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, 54, "사파이어샵");
        }

        Map<String, ItemDataDto> itemDataList = ItemDataRepository.getInstance().getItemDataList();
        Map<String, Map<String, Integer>> playerListCache = PlayerIndividualPurchaseRepository.getInstance()
                .getPlayerListCache();
        Map<String, Integer> itemMap = playerListCache.get(player.getUniqueId().toString());

        for (Entry<String, ItemDataDto> stringItemDataDtoEntry : itemDataList.entrySet()) {
            String MMOItem_ID = stringItemDataDtoEntry.getKey();
            ItemDataDto itemDto = stringItemDataDtoEntry.getValue();
            int slot = itemDto.getSlot();

            ItemStack item = ItemParser.getValidTypeItem(MMOItem_ID);

            ItemStack cloneItem = item.clone();
            ItemMeta cloneItemMeta = cloneItem.getItemMeta();
            cloneItem.setAmount(stringItemDataDtoEntry.getValue().getAmount());

            List<String> lore = new ArrayList<>(cloneItemMeta.getLore());
            if (itemDto.getUserMaxPurchaseAmount() == -1) {
                lore.addAll(Arrays.asList(
                        "",
                        "§f----------------------",
                        "",
                        "§f가격: §e" + itemDto.getPrice() + " §a사파이어",
                        "",
                        "§f----------------------"
                ));
            } else {
                lore.addAll(Arrays.asList(
                        "",
                        "§f----------------------",
                        "",
                        "§f가격: §e" + itemDto.getPrice() + " §a사파이어",
                        "§f개인 한정: §e" + itemMap.get(MMOItem_ID) +  "/"  + itemDto.getUserMaxPurchaseAmount(),
                        "",
                        "§f----------------------"
                ));
            }
            cloneItemMeta.setLore(lore);
            cloneItem.setItemMeta(cloneItemMeta);

            inventory.setItem(slot, cloneItem);
        }

        return inventory;
    }
}
