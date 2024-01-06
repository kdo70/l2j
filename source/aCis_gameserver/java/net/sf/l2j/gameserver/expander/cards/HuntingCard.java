package net.sf.l2j.gameserver.expander.cards;

import net.sf.l2j.gameserver.expander.cards.actions.hunting.AddRewardAction;
import net.sf.l2j.gameserver.expander.cards.actions.hunting.htm.GetIndexPageAction;
import net.sf.l2j.gameserver.expander.cards.actions.hunting.htm.GetShopPageAction;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;

public class HuntingCard {
    protected static final AddRewardAction _addRewardAction = new AddRewardAction();
    protected static final GetIndexPageAction _getIndexPageAction = new GetIndexPageAction();
    protected static final GetShopPageAction _getShopPageAction = new GetShopPageAction();

    public static void addReward(Player player, Monster monster) {
        _addRewardAction.execute(player, monster);
    }

    public static String getPageIndex(Player player) {
        return _getIndexPageAction.execute(player);
    }

    public static String getPageShop(Player player, int page) {
        return _getShopPageAction.execute(player, page);
    }
}