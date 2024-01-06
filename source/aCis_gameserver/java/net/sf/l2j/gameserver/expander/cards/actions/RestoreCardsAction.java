package net.sf.l2j.gameserver.expander.cards.actions;

import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;

public class RestoreCardsAction extends Action {
    private static final LoadCardsAction _loadCardsAction = new LoadCardsAction();
    private static final CreateCardsAction _createCardsAction = new CreateCardsAction();
    private static final LoadCardSkillsAction _loadCardSkillsAction = new LoadCardSkillsAction();

    public void execute(Player player) {
        _loadCardsAction.execute(player);

        if (player.getCards().size() != CardsTypeEnum.getList().size()) {
            _createCardsAction.execute(player);
            _loadCardsAction.execute(player);
        }

        _loadCardSkillsAction.execute(player);
    }
}
