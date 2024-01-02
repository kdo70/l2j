package net.sf.l2j.gameserver.expander.cards;

import net.sf.l2j.gameserver.expander.cards.actions.CreateCardsAction;
import net.sf.l2j.gameserver.expander.cards.actions.RestoreCardsAction;
import net.sf.l2j.gameserver.expander.cards.actions.StoreCardsAction;
import net.sf.l2j.gameserver.model.actor.Player;

public class CharacterCard {
    protected static final CreateCardsAction _createCardsAction = new CreateCardsAction();
    protected static final StoreCardsAction _storeCardsAction = new StoreCardsAction();
    protected static final RestoreCardsAction _restoreCardsAction = new RestoreCardsAction();

    public static void createCards(Player player) {
        _createCardsAction.execute(player);
    }

    public static void storeCards(Player player) {
        _storeCardsAction.execute(player);
    }

    public static void restoreCards(Player player) {
        _restoreCardsAction.execute(player);
    }
}