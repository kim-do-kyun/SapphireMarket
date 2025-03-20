package org.desp.sapphireMarket.listener;

import java.util.Map;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.desp.sapphireMarket.database.ItemPurchaseLogRepository;
import org.desp.sapphireMarket.database.PlayerDataRepository;
import org.desp.sapphireMarket.dto.ItemDataDto;
import org.desp.sapphireMarket.dto.ItemPurchaseLogDto;
import org.desp.sapphireMarket.dto.PlayerDataDto;
import org.desp.sapphireMarket.gui.ItemPurchaseConfirmGUI;
import org.desp.sapphireMarket.gui.SapphireMarketGUI;
import org.desp.sapphireMarket.utils.DateUtil;
import org.desp.sapphireMarket.utils.ItemParser;

public class ItemConfirmListener implements Listener {

    public ItemDataRepository itemDataRepository;
    public PlayerDataRepository playerDataRepository;

    public ItemConfirmListener() {
        itemDataRepository = ItemDataRepository.getInstance();
        playerDataRepository = PlayerDataRepository.getInstance();
    }

    @EventHandler
    public void onItemConfirm(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ItemPurchaseConfirmGUI)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        Map<String, ItemDataDto> itemDataList = itemDataRepository.getItemDataList();

        PlayerDataDto playerData = playerDataRepository.getPlayerData(player);

        ItemStack item = event.getInventory().getItem(13);
        String purchaseItem = MMOItems.getID(item);

        ItemDataDto purchaseItemDataDto = itemDataList.get(purchaseItem);
        int purchaseItemPrice = purchaseItemDataDto.getPrice();

        ItemStack purchaseItemStack = ItemParser.getValidTypeItem(purchaseItem);
        purchaseItemStack.setAmount(purchaseItemDataDto.getAmount());

        int slot = event.getRawSlot();
        if ((0 <= slot && slot <= 2) || (9 <= slot && slot <= 11) || (18 <= slot && slot <= 20)) {    //구매취소
            SapphireMarketGUI sapphireMarketGUI = new SapphireMarketGUI(player);
            player.openInventory(sapphireMarketGUI.getInventory());
        } else if ((6 <= slot && slot <= 8) || (15 <= slot && slot <= 17) || (24 <= slot && slot <= 26)) {    //구매
            //서버수량 제한있는 경우
            if (purchaseItemDataDto.getServerMaxPurchaseAmount() != -1 ) {
                itemDataRepository.reduceServerMaxPurchaseAMount(purchaseItemDataDto);
            }

            if (playerData.getSapphireAmount() >= purchaseItemPrice) {  //구매 가능
                player.sendMessage("§f[사파이어상점]: ◇ 아이템 구매에 성공하였습니다.");
                player.getInventory().addItem(purchaseItemStack);

                //사파이어 차감
                playerDataRepository.reduceSapphireAmount(player, purchaseItemPrice);

                //구매 로그
                ItemPurchaseLogDto newLog = ItemPurchaseLogDto.builder()
                        .user_id(player.getName())
                        .uuid(player.getUniqueId().toString())
                        .purchaseItemID(purchaseItem)
                        .amount(purchaseItemStack.getAmount())
                        .purchasePrice(purchaseItemPrice)
                        .purchaseTime(DateUtil.getCurrentTime())
                        .build();
                ItemPurchaseLogRepository.getInstance().insertPurchaseLog(newLog);
            } else {                    //구매 불가능
                player.sendMessage("[사파이어상점]: ◇ §c 잔액이 부족합니다.");
            }
            SapphireMarketGUI sapphireMarketGUI = new SapphireMarketGUI(player);
            player.openInventory(sapphireMarketGUI.getInventory());
        }
    }


}
