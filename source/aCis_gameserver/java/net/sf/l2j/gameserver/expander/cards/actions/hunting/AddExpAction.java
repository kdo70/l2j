package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.cards.data.xml.HuntingCardData;
import net.sf.l2j.gameserver.expander.cards.model.holder.CardLvlHolder;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.L2Skill;

public class AddExpAction extends Action {
    private static final int MAX_LEVEL_DIFF = 5;

    public void execute(Player player, Monster monster) {
        CharacterCardHolder card = player.getCards().get(CardsTypeEnum.MONSTER.getName());

        final int levelDiff = player.getStatus().getLevel() - monster.getStatus().getLevel();

        if (Rnd.chance(1)) {
            int cardPoints = 1;

            if (Rnd.chance(1)) {
                cardPoints *= 2;
            }

            card.setPoints(card.getPoints() + 1);

            String wordPoint = Str.morph(cardPoints, "очко", "очка", "очков");
            player.sendPacket(
                    SystemMessage.getSystemMessage(SystemMessageId.S1)
                            .addString("Вы получили " + cardPoints + " " + wordPoint + " охоты")
            );
        }

        final int cardMaxLvl = HuntingCardData.getInstance().getCards().size();
        if (card.getLevel() == cardMaxLvl || monster.isRaidBoss() || levelDiff > MAX_LEVEL_DIFF) {
            return;
        }

        CardLvlHolder huntingLevel = HuntingCardData.getInstance().getCard(card.getLevel() + 1);
        int cardExpCount = getRewardExp(levelDiff, monster);
        int currentExp = card.getExp() + cardExpCount;

        if (currentExp < huntingLevel.getExp()) {
            card.setExp(currentExp);

            String wordExp = Str.morph(cardExpCount, "очко", "очка", "очков");
            player.sendPacket(
                    SystemMessage.getSystemMessage(SystemMessageId.S1)
                            .addString("Вы получили " + cardExpCount + " " + wordExp + " опыта охоты")
            );


            if (!player.hasSkill(huntingLevel.getSkillId())) {
                final L2Skill skill = SkillTable.getInstance().getInfo(huntingLevel.getSkillId(), huntingLevel.getSkillLvl());
                player.addSkill(skill, true);
            }

            return;
        }

        CardLvlHolder nextCardLevel = getNextCardLevel(card.getLevel() + 1, cardExpCount);
        card.setLevel(nextCardLevel.getLvl());

        String message = "Hunting Pass Level " + nextCardLevel.getLvl();
        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_DUEL_WILL_BEGIN_IN_S1_SECONDS).addString(message));

        if (player.getInventory().validateCapacityByItemId(nextCardLevel.getItemId(), nextCardLevel.getItemCount())) {
            player.addItem("Loot", nextCardLevel.getItemId(), nextCardLevel.getItemCount(), player, true);
        } else {
            final ItemInstance item = ItemInstance.create(nextCardLevel.getItemId(), nextCardLevel.getItemCount(), player, null);
            item.dropMe(player, 70);
        }

        final L2Skill skill = SkillTable.getInstance().getInfo(nextCardLevel.getSkillId(), nextCardLevel.getSkillLvl());
        player.addSkill(skill, true);

        int cardExp = currentExp - nextCardLevel.getExp();
        card.setExp(Math.max(cardExp, 0));
    }

    public static int getRewardExp(int levelDiff, Monster monster) {
        int count = monster.getStatus().getLevel();

        switch (levelDiff) {
            case (5) -> count /= 5;
            case (4) -> count /= 4;
            case (3) -> count /= 3;
            case (2) -> count /= 2;
            default -> {
            }
        }

        int cardExpCount = Rnd.get(1, count);

        if (Rnd.chance(1)) {
            cardExpCount *= 2;
        }

        if (Rnd.chance(1)) {
            cardExpCount *= 10;
        }

        return cardExpCount;
    }

    public static CardLvlHolder getNextCardLevel(int cardLevel, int cardExpCount) {
        CardLvlHolder nextCardLevel = HuntingCardData.getInstance().getCard(cardLevel);

        if (cardExpCount > nextCardLevel.getExp()) {
            return getNextCardLevel(cardLevel + 1, cardExpCount);
        }

        return nextCardLevel;
    }
}
