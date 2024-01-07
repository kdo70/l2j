package net.sf.l2j.gameserver.expander.cards;

import net.sf.l2j.gameserver.expander.cards.actions.*;
import net.sf.l2j.gameserver.model.actor.Player;

public class CharacterCard {
    protected static final CreateCardsAction _createCardsAction = new CreateCardsAction();
    protected static final StoreCardsAction _storeCardsAction = new StoreCardsAction();
    protected static final RestoreCardsAction _restoreCardsAction = new RestoreCardsAction();
    protected static final StoreCardSkillsAction _storeCardSkillsAction = new StoreCardSkillsAction();
    protected static final RestoreCardSkillsAction _restoreCardSkillsAction = new RestoreCardSkillsAction();

    public static void createCards(Player player) {
        _createCardsAction.execute(player);
    }

    public static void storeCards(Player player) {
        _storeCardsAction.execute(player);
        _storeCardSkillsAction.execute(player);
    }

    public static void restoreCards(Player player) {
        _restoreCardsAction.execute(player);
        _restoreCardSkillsAction.execute(player);
    }
}