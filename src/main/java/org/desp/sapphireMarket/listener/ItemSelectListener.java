package org.desp.sapphireMarket.listener;

import java.util.Map;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.desp.sapphireMarket.dto.ItemDataDto;
import org.desp.sapphireMarket.gui.ItemPurchaseConfirmGUI;
import org.desp.sapphireMarket.gui.SapphireMarketGUI;

public class ItemSelectListener implements Listener {

    @EventHandler
    public void onItemConfirm(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SapphireMarketGUI)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();

        if (currentItem == null || currentItem.getType() == Material.AIR) return;

        ItemDataRepository itemDataRepository = ItemDataRepository.getInstance();
        Map<String, ItemDataDto> itemDataList = itemDataRepository.getItemDataList();

        String currentItemID = MMOItems.getID(currentItem);

        ItemDataDto itemDataDto = itemDataList.get(currentItemID);
        System.out.println("==============================");
        System.out.println("currentItem = " + currentItem);
        System.out.println("currentItemID = " + currentItemID);
        System.out.println("itemDataDto.getMMOItem_ID() = " + itemDataDto.getMMOItem_ID());
        System.out.println("itemDataDto.getPrice() = " + itemDataDto.getPrice());
        System.out.println("itemDataDto.getAmount() = " + itemDataDto.getAmount());
        System.out.println("itemDataDto.getUserMaxPurchaseAmount() = " + itemDataDto.getUserMaxPurchaseAmount());
        System.out.println("itemDataDto.getServerMaxPurchaseAmount() = " + itemDataDto.getServerMaxPurchaseAmount());
        System.out.println("==============================");

        ItemPurchaseConfirmGUI itemPurchaseConfirmGUI = new ItemPurchaseConfirmGUI(currentItem);
        player.openInventory(itemPurchaseConfirmGUI.getInventory());

    }
}
