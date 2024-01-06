package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.cards.data.xml.HuntingCardData;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;

public class AddRewardAction extends Action {
    protected final AddPointsAction _addPointsAction = new AddPointsAction();
    protected final AddExpAction _addExpAction = new AddExpAction();
    private static final int MAX_LEVEL_DIFF = 5;

    public void execute(Player player, Monster monster) {
        final int lvlDiff = player.getStatus().getLevel() - monster.getStatus().getLevel();

        if (monster.isRaidBoss() || Math.abs(lvlDiff) > MAX_LEVEL_DIFF) {

            return;
        }

        CharacterCardHolder card = player.getCards().get(CardsTypeEnum.MONSTER.getName());
        _addPointsAction.execute(player, card, monster);

        final int cardMaxLvl = HuntingCardData.getInstance().getList().size();
        if (card.getLvl() == cardMaxLvl) {

            return;
        }

        _addExpAction.execute(player, card, monster);
    }
}
