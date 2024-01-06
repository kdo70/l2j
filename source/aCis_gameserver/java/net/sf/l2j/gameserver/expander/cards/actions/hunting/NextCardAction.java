package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.gameserver.expander.cards.data.xml.HuntingCardData;
import net.sf.l2j.gameserver.expander.cards.model.holder.CardLvlHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;

public class NextCardAction extends Action {
    public CardLvlHolder execute(int cardLevel, int cardExpCount) {
        CardLvlHolder nextCardLvl = HuntingCardData.getInstance().getLvl(cardLevel);

        if (cardExpCount > nextCardLvl.getExp()) {
            return execute(cardLevel + 1, cardExpCount);
        }

        return nextCardLvl;
    }
}
