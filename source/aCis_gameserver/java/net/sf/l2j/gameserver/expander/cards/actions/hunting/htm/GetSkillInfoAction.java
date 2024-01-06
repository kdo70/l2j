package net.sf.l2j.gameserver.expander.cards.actions.hunting.htm;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.expander.cards.data.enums.HuntingSkillEnum;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.skills.L2Skill;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetSkillInfoAction extends Action {
    protected final String _template = "data/html/script/feature/cards/hunting/skill-info.htm";

    public String execute(Player player) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(_template))) {
            String htm = reader.readLine();

            htm = htm.replace("%autolootCurrency%", getDesc(player, HuntingSkillEnum.AUTOLOOT_CURRENCY.getId()));
            htm = htm.replace("%autolootItems%", getDesc(player, HuntingSkillEnum.AUTOLOOT_ITEMS.getId()));
            htm = htm.replace("%autolootEquip%", getDesc(player, HuntingSkillEnum.AUTOLOOT_EQUIP.getId()));
            htm = htm.replace("%pveDamage%", getDesc(player, HuntingSkillEnum.PVE_DAMAGE.getId()));
            htm = htm.replace("%pveDefence%", getDesc(player, HuntingSkillEnum.PVE_DEFENCE.getId()));
            htm = htm.replace("%adenaChance%", getDesc(player, HuntingSkillEnum.ADENA_CHANCE.getId()));
            htm = htm.replace("%goldChance%", getDesc(player, HuntingSkillEnum.GOLD_CHANCE.getId()));
            htm = htm.replace("%itemsChance%", getDesc(player, HuntingSkillEnum.ITEMS_CHANCE.getId()));
            htm = htm.replace("%equipChance%", getDesc(player, HuntingSkillEnum.EQUIP_CHANCE.getId()));
            htm = htm.replace("%adenaCount%", getDesc(player, HuntingSkillEnum.ADENA_COUNT.getId()));
            htm = htm.replace("%goldCount%", getDesc(player, HuntingSkillEnum.GOLD_COUNT.getId()));
            htm = htm.replace("%itemsCount%", getDesc(player, HuntingSkillEnum.ITEMS_COUNT.getId()));
            htm = htm.replace("%equipCount%", getDesc(player, HuntingSkillEnum.EQUIP_COUNT.getId()));
            htm = htm.replace("%expCount%", getDesc(player, HuntingSkillEnum.EXP_COUNT.getId()));
            htm = htm.replace("%spCount%", getDesc(player, HuntingSkillEnum.SP_COUNT.getId()));

            return htm;
        } catch (IOException e) {
            throw new IllegalStateException("Error in forming the reward list", e);
        }
    }

    protected String getDesc(Player player, int skillId) {
        int skillMaxLvl = SkillTable.getInstance().getMaxLevel(skillId);
        boolean hasSkill = player.hasSkill(skillId);

        if (!hasSkill) {
            return (skillMaxLvl == 1) ? "Не активировано" : "x0%";
        }

        L2Skill playerSkill = player.getSkill(skillId);
        double stat = playerSkill.getStatFuncs(player).get(0).getValue() * 100 - 100;
        String percent = Str.percent(playerSkill.getStatFuncs(player).get(0).getValue());

        return (skillMaxLvl == 1) ? "Активировано" : "x" + percent + "%";
    }
}
