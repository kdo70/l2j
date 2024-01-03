package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.skills.L2Skill;

public class GetSkillAction extends Action {
    private static final int SKILL_ID = 8000;
    public String execute(Player player) {
        CharacterCardHolder card = player.getCards().get(CardsTypeEnum.MONSTER.getName());

        L2Skill currentSkill = SkillTable.getInstance().getInfo(SKILL_ID, card.getLevel());
        L2Skill nextSkill = SkillTable.getInstance().getInfo(SKILL_ID, card.getLevel() + 1);

        if (nextSkill == null) {
            nextSkill = currentSkill;
        }

        StringBuilder html = new StringBuilder();
        getAutolootInfo(html, player, currentSkill, nextSkill, card);
        getCombatInfo(html, player, currentSkill, nextSkill);
        getDropInfo(html, player, currentSkill, nextSkill);

        return html.toString();
    }

    public static void getAutolootInfo(StringBuilder html, Player player, L2Skill currentSkill, L2Skill nextSkill, CharacterCardHolder card) {
        html
                .append("<br><table bgcolor=000000><tr><td width=300> &nbsp; ")
                .append("<font color=B09979>Пассивный подбор предметов</font> ")
                .append("<img src=l2ui.squaregray width=287 height=1><br></td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, nextSkill, 0, 2, card))
                .append("><tr><td width=300> &nbsp; Валюта: ")
                .append(getAutolootStat(player, currentSkill, 0, 2))
                .append("</td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, nextSkill, 0, 3, card))
                .append("><tr><td width=300> &nbsp; Ресурсы: ")
                .append(getAutolootStat(player, currentSkill, 0, 3))
                .append("</td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, nextSkill, 0, 4, card))
                .append("><tr><td width=300> &nbsp; Экипировка: ")
                .append(getAutolootStat(player, currentSkill, 0, 4))
                .append("</td></tr></table><br>");

    }

    public static String getAutolootStat(Player player, L2Skill currentSkill, int index, int value) {
        double currentStat = currentSkill.getStatFuncs(player).get(index).getValue();

        return currentStat >= value ? "<font color=B09979>активировано</font>" : "<font color=A3A0A3>необходимый уровень " + value + "</font>";
    }

    public static void getCombatInfo(StringBuilder html, Player player, L2Skill currentSkill, L2Skill nextSkill) {
        html
                .append("<table bgcolor=000000><tr><td width=300> &nbsp; ")
                .append("<font color=B09979>Увеличение боевых характеристик</font> ")
                .append("<img src=l2ui.squaregray width=287 height=1><br></td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 1))
                .append("><tr><td width=300> &nbsp; Урон против монстров: ")
                .append(getCardStat(player, currentSkill, nextSkill, 1))
                .append("</td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 3))
                .append("><tr><td width=300> &nbsp; Защита от монстров: ")
                .append(getCardStat(player, currentSkill, nextSkill, 3))
                .append("</td></tr></table><br>");

    }

    public static String getBgColor(Player player, L2Skill nextSkill, int index, int value, CharacterCardHolder card) {
        String color = "000000";
        double nextStat = nextSkill.getStatFuncs(player).get(index).getValue();

        if (value == nextStat && value <= 4 && card.getLevel() < 4) {
            color = "ff0000";
        }

        return color;
    }

    public static void getDropInfo(StringBuilder html, Player player, L2Skill currentSkill, L2Skill nextSkill) {
        html
                .append("<table bgcolor=000000><tr><td width=300> &nbsp; ")
                .append("<font color=B09979>Увеличение характеристик удачи</font> ")
                .append("<img src=l2ui.squaregray width=287 height=1><br></td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 5))
                .append("><tr><td width=300> &nbsp; Шанс получить адену: ")
                .append(getCardStat(player, currentSkill, nextSkill, 5))
                .append("</td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 6))
                .append("><tr><td width=300> &nbsp; Шанс получить золото: ")
                .append(getCardStat(player, currentSkill, nextSkill, 6))
                .append("</td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 7))
                .append("><tr><td width=300> &nbsp; Шанс получить ресурс: ")
                .append(getCardStat(player, currentSkill, nextSkill, 7))
                .append("</td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 8))
                .append("><tr><td width=300> &nbsp; Шанс получить предмет экипировки: ")
                .append(getCardStat(player, currentSkill, nextSkill, 8))
                .append("</td></tr></table>")
                .append("<table bgcolor=000000><tr><td width=300></td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 9))
                .append("><tr><td width=300> &nbsp; Количество адены: ")
                .append(getCardStat(player, currentSkill, nextSkill, 9))
                .append("</td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 10))
                .append("><tr><td width=300> &nbsp; Количество золота: ")
                .append(getCardStat(player, currentSkill, nextSkill, 10))
                .append("</td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 11))
                .append("><tr><td width=300> &nbsp; Количество ресурсов: ")
                .append(getCardStat(player, currentSkill, nextSkill, 11))
                .append("</td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 12))
                .append("><tr><td width=300> &nbsp; Количество предметов экипировки: ")
                .append(getCardStat(player, currentSkill, nextSkill, 12))
                .append("</td></tr></table>")
                .append("<table bgcolor=000000><tr><td width=300></td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 13))
                .append("><tr><td width=300> &nbsp; Количество опыта: ")
                .append(getCardStat(player, currentSkill, nextSkill, 13))
                .append("</td></tr></table>")
                .append("<table bgcolor=")
                .append(getBgColor(player, currentSkill, nextSkill, 14))
                .append("><tr><td width=300> &nbsp; Количество очков умений: ")
                .append(getCardStat(player, currentSkill, nextSkill, 14))
                .append("</td></tr></table><br>");
    }

    public static String getCardStat(Player player, L2Skill currentSkill, L2Skill nextSkill, int index) {
        String stat = "";
        double currentStat = currentSkill.getStatFuncs(player).get(index).getValue();
        double nextStat = nextSkill.getStatFuncs(player).get(index).getValue();

        if (currentStat == nextStat) {
            stat = "<font color=B09979> x" + currentStat + "</font>";
        } else {
            stat = "<font color=B09979>x" + currentStat + "</font> > x" + nextStat + "</font>";
        }

        return stat;
    }

    public static String getBgColor(Player player, L2Skill currentSkill, L2Skill nextSkill, int index) {
        String color = "000000";
        double currentStat = currentSkill.getStatFuncs(player).get(index).getValue();
        double nextStat = nextSkill.getStatFuncs(player).get(index).getValue();

        if (currentStat != nextStat) {
            color = "ff0000";
        }

        return color;
    }
}
