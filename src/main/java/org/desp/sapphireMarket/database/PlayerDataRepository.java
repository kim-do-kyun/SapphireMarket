package org.desp.sapphireMarket.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.sapphireMarket.dto.PlayerDataDto;

public class PlayerDataRepository {

    private static PlayerDataRepository instance;
    private final MongoCollection<Document> playerList;
    private static final Map<String, PlayerDataDto> playerListCache = new HashMap<>();

    public PlayerDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.playerList = database.getDatabase().getCollection("PlayerData");
    }

    public static synchronized PlayerDataRepository getInstance() {
        if (instance == null) {
            instance = new PlayerDataRepository();
        }
        return instance;
    }

    public void loadPlayerData(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();

        Document document = new Document("uuid", uuid);
        if (playerList.find(Filters.eq("uuid", uuid)).first() == null) {
            Document newUserDocument = new Document()
                    .append("user_id", user_id)
                    .append("uuid", uuid)
                    .append("sapphireAmount", 0);
            playerList.insertOne(document);
        }

        int sapphireAmount = playerList.find(document).first().getInteger("sapphireAmount");
        PlayerDataDto playerDto = PlayerDataDto.builder()
                .user_id(user_id)
                .uuid(uuid)
                .sapphireAmount(sapphireAmount)
                .build();
        playerListCache.put(uuid, playerDto);
    }

    public PlayerDataDto getPlayerData(Player player) {
        if (!player.isOnline()){
            return null;
        }
        return playerListCache.get(player.getUniqueId().toString());
    }

    public void addSapphireAmount(Player player, int sapphireAmount) {
        PlayerDataDto playerDataDto = playerListCache.get(player.getUniqueId().toString());
        playerDataDto.setSapphireAmount(playerDataDto.getSapphireAmount() + sapphireAmount);
        playerListCache.put(player.getUniqueId().toString(), playerDataDto);
    }

    public void savePlayerData(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();
        int sapphireAmount = playerListCache.get(uuid).getSapphireAmount();

        Document document = new Document()
                .append("user_id", user_id)
                .append("uuid", uuid)
                .append("sapphireAmount", sapphireAmount);

        playerList.replaceOne(
                Filters.eq("uuid", uuid),
                document,
                new ReplaceOptions().upsert(true)
        );
    }
}
