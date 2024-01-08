package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.common.actions.SendMsgAction;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.skills.L2Skill;

public class AddSkillAction extends Action {
    public void execute(Player player, int skillId, int skillLvl) {
        CharacterCardHolder card = player.getCards().get(CardsTypeEnum.MONSTER.getName());
        int playerSp = card.getSp();
        L2Skill currentSkill = player.getSkill(skillId);

        if (playerSp < 1) {
            SendMsgAction.execute(player, "Для изучения умения недостаточно Skill Points");

            return;
        }

        if (currentSkill != null) {
            int currentLevel = currentSkill.getLevel();
            int skillMaxLvl = SkillTable.getInstance().getMaxLevel(currentSkill.getId());

            if (currentLevel == skillLvl || currentLevel + 1 > skillMaxLvl) {
                SendMsgAction.execute(player, "Достигнут максимальный уровень умения");

                return;
            }

            if (currentLevel + 1 != skillLvl) {
                SendMsgAction.execute(player, "Невозможно изучить это умение");

                return;
            }
        } else if (skillLvl != 1) {
            SendMsgAction.execute(player, "Невозможно изучить это умение");

            return;
        }

        L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);

        if (skill == null) {
            SendMsgAction.execute(player, "Невозможно изучить это умение");

            return;
        }

        card.addSkill(skill);
        player.addSkill(skill, true);
        card.setSp(playerSp - 1);

        SendMsgAction.execute(player, "Вы успешно изучили умение");
    }
}
