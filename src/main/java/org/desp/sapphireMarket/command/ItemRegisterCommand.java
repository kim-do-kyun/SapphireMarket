package org.desp.sapphireMarket.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.jetbrains.annotations.NotNull;

public class ItemRegisterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }
        if (!player.isOp()){
            return false;
        }

        String MMOItem_ID = strings[0];
        int amount = Integer.parseInt(strings[1]);
        int price = Integer.parseInt(strings[2]);

        ItemDataRepository.getInstance().insertItemData(MMOItem_ID, amount, price);
        return false;
    }
}
