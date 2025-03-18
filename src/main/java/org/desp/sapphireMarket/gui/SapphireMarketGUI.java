package org.desp.sapphireMarket.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.desp.sapphireMarket.dto.ItemDataDto;
import org.desp.sapphireMarket.utils.ItemParser;
import org.jetbrains.annotations.NotNull;

public class SapphireMarketGUI implements InventoryHolder {

    public Inventory inventory;

    @Override
    public @NotNull Inventory getInventory() {
        if(this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, 54, "사파이어샵");
        }

        Map<String, ItemDataDto> itemDataList = ItemDataRepository.getInstance().getItemDataList();

        int slot = 10;
        for (Entry<String, ItemDataDto> stringItemDataDtoEntry : itemDataList.entrySet()) {
            String MMOItem_ID = stringItemDataDtoEntry.getKey();
            ItemDataDto itemDto = stringItemDataDtoEntry.getValue();

            ItemStack item = ItemParser.getValidTypeItem(MMOItem_ID);

            ItemStack cloneItem = item.clone();
            ItemMeta cloneItemMeta = cloneItem.getItemMeta();
            cloneItem.setAmount(stringItemDataDtoEntry.getValue().getAmount());

            List<String> lore = new ArrayList<>(cloneItemMeta.getLore());
            if (itemDto.getUserMaxPurchaseAmount() == 999) {
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
                        "§f개인 한정: §e" + itemDto.getUserMaxPurchaseAmount(),
                        "",
                        "§f----------------------"
                ));
            }
            cloneItemMeta.setLore(lore);
            cloneItem.setItemMeta(cloneItemMeta);

            inventory.setItem(slot, cloneItem);
            slot++;
        }

        return inventory;
    }
}
