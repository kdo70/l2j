package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.gameserver.expander.cards.calculators.hunting.HuntingExpCalculator;
import net.sf.l2j.gameserver.expander.cards.data.xml.HuntingCardData;
import net.sf.l2j.gameserver.expander.cards.model.holder.CardLvlHolder;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.common.actions.SendMsgAction;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;

public class AddExpAction extends Action {
    protected final HuntingExpCalculator _huntingExpCalculator = new HuntingExpCalculator();
    protected final NextCardAction _nextCardAction = new NextCardAction();

    public void execute(Player player, CharacterCardHolder card, Monster monster) {
        final int lvlDiff = player.getStatus().getLevel() - monster.getStatus().getLevel();
        CardLvlHolder huntingLvl = HuntingCardData.getInstance().getLvl(card.getLvl() + 1);

        int cardExpCount = _huntingExpCalculator.execute(monster, lvlDiff);
        int currentExp = card.getExp() + cardExpCount;

        if (currentExp < huntingLvl.getExp()) {
            card.setExp(currentExp);

            String wordExp = Str.morph(cardExpCount, "очко", "очка", "очков");
            SendMsgAction.execute(player, "Вы получили " + cardExpCount + " " + wordExp + " опыта охоты");

            return;
        }

        CardLvlHolder nextCardLvl = _nextCardAction.execute(card.getLvl() + 1, cardExpCount);
        card.setLvl(nextCardLvl.getLvl());
        card.setSp(card.getSp() + 1);
        card.setExp(Math.max(currentExp - nextCardLvl.getExp(), 0));

        SendMsgAction.execute(player, "Hunting Pass Level " + nextCardLvl.getLvl());
    }
}
