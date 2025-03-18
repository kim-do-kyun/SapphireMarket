package org.desp.sapphireMarket.gui;

import java.util.Map;
import java.util.Map.Entry;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.desp.sapphireMarket.dto.ItemDataDto;
import org.desp.sapphireMarket.utils.ItemParser;
import org.jetbrains.annotations.NotNull;

public class SapphireMarketGUI implements InventoryHolder {

    public Inventory inventory;

    @Override
    public @NotNull Inventory getInventory() {
        if(this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, 54, "사파이어 상점");
        }

        Map<String, ItemDataDto> itemDataList = ItemDataRepository.getInstance().getItemDataList();

        int slot = 10;
        for (Entry<String, ItemDataDto> stringItemDataDtoEntry : itemDataList.entrySet()) {
            String MMOItem_ID = stringItemDataDtoEntry.getKey();
            ItemStack item = ItemParser.getValidTypeItem(MMOItem_ID);
            item.setAmount(stringItemDataDtoEntry.getValue().getAmount());
            inventory.setItem(slot, item);
            slot++;
        }

        return inventory;
    }
}
