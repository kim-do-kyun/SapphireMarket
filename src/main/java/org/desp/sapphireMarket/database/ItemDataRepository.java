package org.desp.sapphireMarket.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;
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
                .append("serverMaxPurchaseAmount", newItemData.getServerMaxPurchaseAmount());

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
                    .build();

            itemDataList.put(item.getMMOItem_ID(), item);
        }
    }
}
