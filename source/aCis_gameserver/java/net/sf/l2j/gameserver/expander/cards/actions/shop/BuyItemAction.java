package net.sf.l2j.gameserver.expander.cards.actions.shop;

import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.expander.cards.data.xml.ShopData;
import net.sf.l2j.gameserver.expander.cards.model.holder.ShopItemHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

import java.util.List;

public class BuyItemAction extends Action {
    public void execute(Player player, String type, int index, int count) {
        index -= 1;

        List<ShopItemHolder> items = ShopData.getInstance().getItems(type);
        if (index > items.size() || count < 1) {
            LOGGER.error("A nonexistent store index was called");
            return;
        }

        ShopItemHolder item = items.get(index);
        if (item.getLimit() < count) {
            player.sendMessage(
                    "В магазине недостаточно "
                            + ItemData.getInstance().getTemplate(item.getItemId()).getName()
            );

            return;
        }


        if (!player.getInventory().validateCapacityByItemId(item.getItemId(), item.getCount() * count)) {
            player.sendMessage("Инвентарь переполнен");
            return;
        }

        int points = player.getCards().get(type).getPoints();

        if (points < item.getPrice() * count) {
            int diff = (item.getPrice() * count) - points;
            player.sendMessage(
                    "Недостаточно очков для покупки "
                            + ItemData.getInstance().getTemplate(item.getItemId()).getName()
                            + ", нехватает"
                            + " "
                            + Str.numFormat(diff)
                            + " "
                            + Str.morph(diff, "очко", "очка", "очков"));

            return;
        }

        final int totalPrice = item.getPrice() * count;
        final int balance = player.getCards().get(type).getPoints();

        player.sendMessage("Списано "
                + Str.numFormat(totalPrice)
                + " "
                + Str.morph(totalPrice, "очко", "очка", "очков")
                + ", остаток"
                + " "
                + Str.numFormat(balance)
                + " "
                + Str.morph(balance, "очко", "очка", "очков"));

        ItemInstance itemInstance = new ItemInstance(IdFactory.getInstance().getNextId(), item.getItemId());
        final int totalCount = item.getCount() * count;
        player.getCards().get(type).setPoints(points - item.getPrice() * count);

        if (itemInstance.isStackable()) {
            player.addItem("shop", item.getItemId(), totalCount, null, true);
        } else {
            for (int i = 0; i < totalCount; i++) {
                player.addItem("shop", item.getItemId(), 1, null, true);
            }
        }

        item.setLimit(item.getLimit() - count);
        item.setSoldCount(item.getSoldCount() + count);
    }
}
