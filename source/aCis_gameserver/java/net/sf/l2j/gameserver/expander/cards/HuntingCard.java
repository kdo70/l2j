package net.sf.l2j.gameserver.expander.cards;

import net.sf.l2j.gameserver.expander.cards.actions.hunting.AddExpAction;
import net.sf.l2j.gameserver.expander.cards.actions.hunting.GetIndexPageAction;
import net.sf.l2j.gameserver.expander.cards.actions.hunting.GetShopPageAction;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;

public class HuntingCard {
    protected static final AddExpAction _addExpAction = new AddExpAction();
    protected static final GetIndexPageAction _getIndexPageAction = new GetIndexPageAction();
    protected static final GetShopPageAction _getShopPageAction = new GetShopPageAction();

    public static void addExp(Player player, Monster monster) {
        _addExpAction.execute(player, monster);
    }

    public static String getPageIndex(Player player) {
        return _getIndexPageAction.execute(player);
    }

    public static String getPageShop(Player player, int page) {
        return _getShopPageAction.execute(player, page);
    }
}