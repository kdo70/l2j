package net.sf.l2j.gameserver.expander.cards.actions.hunting.htm;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.expander.cards.data.enums.HuntingSkillEnum;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.skills.L2Skill;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetSkillInfoAction extends Action {
    protected final String _template = "data/html/script/feature/cards/hunting/skill-info.htm";
    protected final String _buttonTemplate = "data/html/script/feature/cards/hunting/skill-button.htm";
    protected final String _buttonColor = "B09779";
    protected final String _buttonActiveColor = "6697FF";

    public String execute(Player player, CharacterCardHolder card) {
        String template = buildDesc(player);
        template = buildButtons(player, card, template);

        return template;
    }

    private String buildDesc(Player player) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(_template))) {
            String htm = reader.readLine();

            for (Integer skill : HuntingSkillEnum.getList()) {
                L2Skill playerSkill = player.getSkill(skill);
                int skillMaxLvl = SkillTable.getInstance().getMaxLevel(skill);
                String desc;

                if (playerSkill == null) {
                    desc = (skillMaxLvl == 1) ? "Не активировано" : "x1";
                } else {
                    desc = (skillMaxLvl == 1) ? "Активировано" : "x" + playerSkill.getStatFuncs(player).get(0).getValue();
                }

                htm = htm.replace("%" + skill + "%", desc);
            }

            return htm;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String buildButtons(Player player, CharacterCardHolder card, String template) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(_buttonTemplate))) {
            String buttonTemplate = reader.readLine();

            for (Integer skill : HuntingSkillEnum.getList()) {
                String htm = buttonTemplate;

                htm = htm.replace("%skillId%", Integer.toString(skill));

                L2Skill currentSkill = player.getSkill(skill);
                int currentLevel = (currentSkill != null) ? currentSkill.getLevel() : 0;
                int skillMaxLvl = SkillTable.getInstance().getMaxLevel(skill);

                String color = _buttonColor;
                if (card.getSp() > 0 && currentLevel != skillMaxLvl) {
                    color = _buttonActiveColor;
                }

                htm = htm.replace("%color%", color);

                String text;
                if (skillMaxLvl == 1) {
                    text = "Активировать";
                } else {
                    int nextLvl = currentLevel == skillMaxLvl ? currentLevel : currentLevel + 1;
                    L2Skill nextSkill = SkillTable.getInstance().getInfo(skill, nextLvl);
                    text = "x" + nextSkill.getStatFuncs(player).get(0).getValue();
                }

                int skillLvl = (currentLevel == 0 && skillMaxLvl == 1) ? skillMaxLvl : Math.min(currentLevel + 1, skillMaxLvl);

                htm = htm.replace("%skillLvl%", Integer.toString(skillLvl));
                htm = htm.replace("%text%", text);

                template = template.replace("%" + skill + "Button%", htm);
            }

            return template;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
