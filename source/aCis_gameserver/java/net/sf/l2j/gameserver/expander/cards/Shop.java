package net.sf.l2j.gameserver.expander.cards;

import net.sf.l2j.gameserver.expander.cards.actions.shop.BuyItemAction;
import net.sf.l2j.gameserver.model.actor.Player;

public class Shop {
    protected static final BuyItemAction _buyItemAction = new BuyItemAction();

    public static void buyItem(Player player, String type, int index, int count) {
        _buyItemAction.execute(player, type, index, count);
    }
}