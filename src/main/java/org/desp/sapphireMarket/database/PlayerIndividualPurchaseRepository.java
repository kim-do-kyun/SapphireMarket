package org.desp.sapphireMarket.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.sapphireMarket.dto.ItemDataDto;
import org.desp.sapphireMarket.dto.PlayerDataDto;
import org.desp.sapphireMarket.dto.PlayerIndividualPurchaseDto;

public class PlayerIndividualPurchaseRepository {

    private static PlayerIndividualPurchaseRepository instance;
    private final MongoCollection<Document> playerList;
    private static final Map<String, Map<String, Integer>> playerIndividualPurchaseCache = new HashMap<>();

    public PlayerIndividualPurchaseRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.playerList = database.getDatabase().getCollection("PlayerIndividualPurchase");
    }

    public static synchronized PlayerIndividualPurchaseRepository getInstance() {
        if (instance == null) {
            instance = new PlayerIndividualPurchaseRepository();
        }
        return instance;
    }


    public void loadPlayerPurchaseCache(Player player) {
        String uuid = player.getUniqueId().toString();

        FindIterable<Document> documents = playerList.find(
                Filters.eq("uuid", uuid)
        );

        Map<String, Integer> purchaseData = new HashMap<>();

        for (Document doc : documents) {
            String itemId = doc.getString("MMOItem_ID");
            int bought = doc.getInteger("bought", 0);

            purchaseData.put(itemId, bought);
        }

        playerIndividualPurchaseCache.put(uuid, purchaseData);
    }


    public void saveCacheIndividualPurchase(Player player, String MMOItem_ID) {
        String uuid = player.getUniqueId().toString();
        playerIndividualPurchaseCache
                .computeIfAbsent(uuid, k -> new HashMap<>())
                .merge(MMOItem_ID, 1, Integer::sum);
    }

    public void saveDBIndividualPurchaseData(Player player) {
        String uuid = player.getUniqueId().toString();
        Map<String, Integer> purchaseData = playerIndividualPurchaseCache.get(uuid);
        if (purchaseData == null) return;

        for (Map.Entry<String, Integer> entry : purchaseData.entrySet()) {
            String itemId = entry.getKey();
            int bought = entry.getValue();

            playerList.updateOne(
                    Filters.and(
                            Filters.eq("uuid", uuid),
                            Filters.eq("MMOItem_ID", itemId)
                    ),
                    Updates.inc("bought", bought),
                    new UpdateOptions().upsert(true)
            );
        }

        playerIndividualPurchaseCache.remove(uuid);  // 캐시 제거
    }


    public Map<String, Map<String, Integer>> getPlayerListCache() {
        return playerIndividualPurchaseCache;
    }

    public void registerItem(Player player) {
        String uuid = player.getUniqueId().toString();

        FindIterable<Document> playerIndividualItems = ItemDataRepository.getInstance()
                .findPlayerIndividualItems(player);

        for (Document itemDoc : playerIndividualItems) {
            String MMOItem_ID = itemDoc.getString("MMOItem_ID");

            Document purchaseDoc = playerList.find(
                    Filters.and(
                            Filters.eq("uuid", uuid),
                            Filters.eq("MMOItem_ID", MMOItem_ID)
                    )
            ).first();

            if (purchaseDoc == null) {
                Map<String, Integer> itemData = new HashMap<>();
                itemData.put(MMOItem_ID, 0);
                playerIndividualPurchaseCache.put(uuid,itemData);
            }
        }
    }
}
