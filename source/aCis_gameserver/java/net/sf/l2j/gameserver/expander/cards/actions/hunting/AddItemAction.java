package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.cards.data.xml.HuntingCardData;
import net.sf.l2j.gameserver.expander.cards.model.holder.CardLvlHolder;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.common.actions.SendMsgAction;
import net.sf.l2j.gameserver.model.actor.Player;

public class AddItemAction extends Action {
    public void execute(Player player, int index) {
        CharacterCardHolder playerCard = player.getCards().get(CardsTypeEnum.MONSTER.getName());

        if (playerCard.getRewardLvl() >= index) {
            SendMsgAction.execute(player, "Награда была получена вами ранее");

            return;
        }

        if (Math.abs(playerCard.getRewardLvl() - index) > 1) {
            SendMsgAction.execute(player, "Награда будет доступна после получения награды за " + (index - 1) + " уровнь");

            return;
        }

        if (playerCard.getRewardLvl() + 1 != index || index > playerCard.getLvl()) {
            SendMsgAction.execute(player, "Награда будет доступна на " + index + " уровне");

            return;
        }

        CardLvlHolder card = HuntingCardData.getInstance().getLvl(index);

        if (player.getInventory().validateCapacityByItemId(card.getItemId(), card.getItemCount())) {
            player.addItem("Loot", card.getItemId(), card.getItemCount(), player, true);
            playerCard.setRewardLvl(playerCard.getRewardLvl() + 1);

            return;
        }

        SendMsgAction.execute(player, "Инвентарь переполнен");
    }
}
