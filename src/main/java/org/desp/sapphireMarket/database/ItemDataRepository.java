package org.desp.sapphireMarket.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.sapphireMarket.dto.ItemDataDto;

public class ItemDataRepository {

    private static ItemDataRepository instance;
    private final MongoCollection<Document> itemDataDB;
    @Getter
    public Map<String, ItemDataDto> itemDataList = new HashMap<>();

    public ItemDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.itemDataDB = database.getDatabase().getCollection("ItemData");
    }

    public static ItemDataRepository getInstance() {
        if (instance == null) {
            instance = new ItemDataRepository();
        }
        return instance;
    }

    public void insertItemData(ItemDataDto newItemData) {
        Document document = new Document()
                .append("MMOItem_ID", newItemData.getMMOItem_ID())
                .append("amount", newItemData.getAmount())
                .append("price", newItemData.getPrice())
                .append("userMaxPurchaseAmount", newItemData.getUserMaxPurchaseAmount())
                .append("serverMaxPurchaseAmount", newItemData.getServerMaxPurchaseAmount())
                .append("slot", newItemData.getSlot());

        itemDataDB.insertOne(document);
    }

    public void loadItemData() {
        FindIterable<Document> documents = itemDataDB.find();
        for (Document document : documents) {
            ItemDataDto item = ItemDataDto.builder()
                    .MMOItem_ID(document.getString("MMOItem_ID"))
                    .amount(document.getInteger("amount"))
                    .price(document.getInteger("price"))
                    .userMaxPurchaseAmount(document.getInteger("userMaxPurchaseAmount"))
                    .serverMaxPurchaseAmount(document.getInteger("serverMaxPurchaseAmount"))
                    .slot(document.getInteger("slot"))
                    .build();

            itemDataList.put(item.getMMOItem_ID(), item);
        }
    }

    public void reduceServerMaxPurchaseAMount(ItemDataDto purchaseItemDataDto) {
        purchaseItemDataDto.setServerMaxPurchaseAmount(purchaseItemDataDto.getServerMaxPurchaseAmount() - 1);
        itemDataList.put(purchaseItemDataDto.getMMOItem_ID(), purchaseItemDataDto);

        Document document = new Document()
                .append("MMOItem_ID", purchaseItemDataDto.getMMOItem_ID())
                .append("amount", purchaseItemDataDto.getAmount())
                .append("price", purchaseItemDataDto.getPrice())
                .append("userMaxPurchaseAmount", purchaseItemDataDto.getUserMaxPurchaseAmount())
                .append("serverMaxPurchaseAmount", purchaseItemDataDto.getServerMaxPurchaseAmount())
                .append("slot", purchaseItemDataDto.getSlot());

        itemDataDB.replaceOne(
                Filters.eq("MMOItem_ID", purchaseItemDataDto.getMMOItem_ID()),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    public FindIterable<Document> findPlayerIndividualItems(Player player) {
        String uuid = player.getUniqueId().toString();
        String userId = player.getName();

        // Step 1: 제한 있는 상점 아이템만 불러오기
        FindIterable<Document> limitedItems = itemDataDB.find(
                Filters.ne("userMaxPurchaseAmount", -1)
        );
        return limitedItems;
    }
}
