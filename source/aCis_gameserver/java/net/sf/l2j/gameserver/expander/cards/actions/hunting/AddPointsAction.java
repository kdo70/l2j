package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.expander.cards.calculators.hunting.HuntingPointsCalculator;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.common.actions.SendMsgAction;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;

public class AddPointsAction extends Action {
    private static final int CHANCE = 1;
    protected final HuntingPointsCalculator _huntingPointsCalculator = new HuntingPointsCalculator();

    public void execute(Player player, CharacterCardHolder card, Monster monster) {
        final int lvlDiff = player.getStatus().getLevel() - monster.getStatus().getLevel();

        if (!Rnd.chance(CHANCE)) {

            return;
        }

        int points = _huntingPointsCalculator.execute(monster, lvlDiff);

        card.setPoints(card.getPoints() + points);

        String wordPoint = Str.morph(points, "очко", "очка", "очков");
        SendMsgAction.execute(player, "Вы получили " + points + " " + wordPoint + " охоты");
    }
}
