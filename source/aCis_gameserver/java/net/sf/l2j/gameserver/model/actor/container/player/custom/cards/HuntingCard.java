package net.sf.l2j.gameserver.model.actor.container.player.custom.cards;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.model.actor.container.player.custom.xml.cards.HuntingData;
import net.sf.l2j.gameserver.expander.helpers.data.xml.ItemIconData;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.model.actor.container.player.custom.statistics.PlayerStatistic;
import net.sf.l2j.gameserver.model.actor.container.player.custom.xml.ShopData;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.L2Skill;

import java.util.concurrent.atomic.AtomicInteger;

public class HuntingCard {
    private static final CLogger LOGGER = new CLogger(PlayerCard.class.getName());
    private static final int SKILL_ID = 8000;
    private static final int CARD_MAX_LEVEL = 210;
    private static final int MAX_LEVEL_DIFF = 5;

    public static void addExp(Player player, PlayerCard card, Monster monster) {
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

        if (card.getLevel() == CARD_MAX_LEVEL || monster.isRaidBoss() || levelDiff > MAX_LEVEL_DIFF) {
            return;
        }

        HuntingCardLevel huntingLevel = HuntingData.getInstance().getBattleLevel(card.getLevel() + 1);
        int cardExpCount = getRewardExp(levelDiff, monster);
        int currentExp = card.getExp() + cardExpCount;

        if (currentExp < huntingLevel.getRequiredExpToLevelUp()) {
            card.setExp(currentExp);

            String wordExp = Str.morph(cardExpCount, "очко", "очка", "очков");
            player.sendPacket(
                    SystemMessage.getSystemMessage(SystemMessageId.S1)
                            .addString("Вы получили " + cardExpCount + " " + wordExp + " опыта охоты")
            );


            if (!player.hasSkill(huntingLevel.getSkillId())) {
                final L2Skill skill = SkillTable.getInstance().getInfo(huntingLevel.getSkillId(), huntingLevel.getSkillLevel());
                player.addSkill(skill, true);
            }

            return;
        }

        HuntingCardLevel nextCardLevel = getNextCardLevel(card.getLevel() + 1, cardExpCount);
        card.setLevel(nextCardLevel.getLevel());

        String message = "Hunting Pass Level " + nextCardLevel.getLevel();
        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_DUEL_WILL_BEGIN_IN_S1_SECONDS).addString(message));

        if (player.getInventory().validateCapacityByItemId(nextCardLevel.getItemId(), nextCardLevel.getItemCount())) {
            player.addItem("Loot", nextCardLevel.getItemId(), nextCardLevel.getItemCount(), player, true);
        } else {
            final ItemInstance item = ItemInstance.create(nextCardLevel.getItemId(), nextCardLevel.getItemCount(), player, null);
            item.dropMe(player, 70);
        }

        final L2Skill skill = SkillTable.getInstance().getInfo(nextCardLevel.getSkillId(), nextCardLevel.getSkillLevel());
        player.addSkill(skill, true);

        int cardExp = currentExp - nextCardLevel.getRequiredExpToLevelUp();
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

    public static HuntingCardLevel getNextCardLevel(int cardLevel, int cardExpCount) {
        HuntingCardLevel nextCardLevel = HuntingData.getInstance().getBattleLevel(cardLevel);

        if (cardExpCount > nextCardLevel.getRequiredExpToLevelUp()) {
            return getNextCardLevel(cardLevel + 1, cardExpCount);
        }

        return nextCardLevel;
    }

    public static String getRewardList(PlayerCard card) {
        StringBuilder html = new StringBuilder();

        for (int index = 1; index < 6; index++) {
            int cardLevel = card.getLevel();

            if (cardLevel > 205) {
                cardLevel = 205;
            }

            HuntingCardLevel huntingLevel = HuntingData.getInstance().getBattleLevel(cardLevel + index);
            Item item = ItemData.getInstance().getTemplate(huntingLevel.getItemId());

            html.append("<table width=313 bgcolor=000000><tr><td width=22>")
                    .append("<font color=A3A0A3>&nbsp;&nbsp;Ур.</font><font color=B09979>")
                    .append(huntingLevel.getLevel() < 10 ? "&nbsp;&nbsp; " : " ")
                    .append(huntingLevel.getLevel())
                    .append("</font></td><td width=32><img src=")
                    .append(ItemIconData.getIcon(item.getItemId()))
                    .append(" width=32 height=32><img src= width=0 height=6></td><td width=150>")
                    .append(item.getName())
                    .append("<br1><font color=B09979>")
                    .append(ItemIconData.getDesc(item.getItemId()))
                    .append("</font></td><td width=10><font color=B09979>")
                    .append(huntingLevel.getItemCount())
                    .append(" шт.</font></td></tr></table>");

            if (index != 5) {
                html.append("<img src= width=0 height=2>");
            }
        }

        return html.toString();
    }

    public static String getRewardSkillInfo(PlayerCard card, Player player) {
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

    public static void getAutolootInfo(StringBuilder html, Player player, L2Skill currentSkill, L2Skill nextSkill, PlayerCard card) {
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

    public static String getCardStat(Player player, L2Skill currentSkill, L2Skill nextSkill, int index) {
        String stat = "";
        double currentStat = currentSkill.getStatFuncs(player).get(index).getValue();
        double nextStat = nextSkill.getStatFuncs(player).get(index).getValue();

        if (currentStat == nextStat) {
            stat = "<font color=B09979> " + Str.mul(currentStat) + "</font>";
        } else {
            stat = "<font color=B09979>" + Str.mul(currentStat) + "</font> > " + Str.mul(nextStat) + "</font>";
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

    public static String getBgColor(Player player, L2Skill nextSkill, int index, int value, PlayerCard card) {
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

    public static String getPercent(PlayerCard card) {
        double percent = 0;

        if (card.getLevel() < 210) {
            HuntingCardLevel nextBattleLevel = HuntingData.getInstance().getBattleLevel(card.getLevel() + 1);
            percent = ((double) card.getExp() / (double) nextBattleLevel.getRequiredExpToLevelUp()) * 100;
        }

        return Str.percent(percent);
    }

    public static String getProgress(PlayerCard card) {
        double percent;
        double progress = 0;

        if (card.getLevel() < 210) {
            HuntingCardLevel nextBattleLevel = HuntingData.getInstance().getBattleLevel(card.getLevel() + 1);
            percent = ((double) card.getExp() / (double) nextBattleLevel.getRequiredExpToLevelUp()) * 100;
            progress = 2.85 * percent;
        }

        return Str.percent(progress);
    }

    public static String getPageIndex(String content, PlayerCard card, Player player) {
        content = content.replace("%battlePoints%", card.getPointsStr());
        content = content.replace("%battleLevel%", card.getLevelStr());
        content = content.replace("%percent%", card.getPercent());
        content = content.replace("%progress%", card.getProgress());
        content = content.replace("%rewardList%", card.getRewardList());
        content = content.replace("%rewardSkillInfo%", card.getRewardSkillInfo(player));
        content = content.replace("%monsterKills%", PlayerStatistic.getMonsterKills(player));
        content = content.replace("%monsterKillsToday%", PlayerStatistic.getMonsterKillsToday(player));
        content = content.replace("%monsterDeaths%", PlayerStatistic.getMonsterDeaths(player));
        content = content.replace("%monsterDeathsToday%", PlayerStatistic.getMonsterDeathsToday(player));
        content = content.replace("%exp%", PlayerStatistic.getExp(player));
        content = content.replace("%expToday%", PlayerStatistic.getExpToday(player));
        content = content.replace("%sp%", PlayerStatistic.getSp(player));
        content = content.replace("%spToday%", PlayerStatistic.getSpToday(player));
        content = content.replace("%adena%", PlayerStatistic.getAdena(player));
        content = content.replace("%adenaToday%", PlayerStatistic.getAdenaToday(player));
        content = content.replace("%gold%", PlayerStatistic.getGold(player));
        content = content.replace("%goldToday%", PlayerStatistic.getGoldToday(player));
        content = content.replace("%items%", PlayerStatistic.getItems(player));
        content = content.replace("%itemsToday%", PlayerStatistic.getItemsToday(player));
        content = content.replace("%equip%", PlayerStatistic.getEquip(player));
        content = content.replace("%equipToday%", PlayerStatistic.getEquipToday(player));

        return content;
    }

    public static String getPageShop(String content, PlayerCard card, int page) {
        StringBuilder html = new StringBuilder();
        AtomicInteger index = new AtomicInteger();
        AtomicInteger visibleIndex = new AtomicInteger();

        int pageSize = 8;
        int pageCount = ShopData.getInstance().getItems(card.getType()).size() / 8;

        if ((pageCount & 1) != 0) {
            pageCount++;
        }

        int currentPage = Math.min(page, pageCount);

        ShopData.getInstance().getItems(card.getType()).forEach(item -> {
            index.getAndIncrement();

            if (index.get() <= (pageSize * currentPage) - pageSize
                    || index.get() > pageSize * currentPage
                    || visibleIndex.get() > pageSize) {

                return;
            }

            visibleIndex.getAndIncrement();
            Item itemData = ItemData.getInstance().getTemplate(item.getItemId());
            html
                    .append("<table ")
                    .append((index.get() & 1) != 0 ? " " : "bgcolor=000000 ")
                    .append("width=200><tr><td><table width=50><tr><td><img width=3><img height=44></td>")
                    .append("<td><img src=")
                    .append(ItemIconData.getIcon(item.getItemId()))
                    .append(" width=32 height=32><img height=15></td>")
                    .append("</tr></table></td><td><table width=325><tr><td width=325><table width=325><tr><td width=300>")
                    .append(itemData.getName())
                    .append("<font color=B09979> x")
                    .append(Str.numFormat(item.getCount()))
                    .append("</font><br1><font color=A3A3A3>Грейд:</font> <font color=B09979>")
                    .append(itemData.getCrystalType().getName())
                    .append("</font><font color=A3A3A3> Вес:</font> <font color=B09979>")
                    .append(Str.numFormat(itemData.getWeight()))
                    .append("</font></td><td align=right width=25>")
                    .append("<button value=\"\" width=16 height=16 fore=L2UI_CH3.aboutotpicon back=L2UI_CH3.aboutotpicon_down>")
                    .append("</td></tr></table></td></tr></table></td><td>")
                    .append("<table width=110><tr><td width=110 align=center><font color=BABDCC>")
                    .append(Str.numFormat(item.getLimit()))
                    .append(" шт.</font><br1><font color=B09979>Продано: ")
                    .append(Str.numFormat(item.getSoldCount()))
                    .append("</font></td></tr></table>")
                    .append("</td><td><table width=110><tr><td width=110 align=center>")
                    .append("<table width=110><tr><td align=center><font color=B09979>")
                    .append(Str.numFormat(item.getPrice()))
                    .append(" ")
                    .append(Str.morph(item.getPrice(), "очко", "очка", "очков"))
                    .append(" / шт.</font></td></tr></table>")
                    .append("<table width=110><tr><td>")
                    .append("<edit var=count")
                    .append(index)
                    .append(" width=55 height=10 type=number length=4></td>")
                    .append("<td><a action=\"bypass _bbsgetfav ")
                    .append(currentPage)
                    .append(" ")
                    .append(index)
                    .append(" $count")
                    .append(index)
                    .append("\" msg=\"1983;Требуется подтверждение покупки:\\n\\n")
                    .append(itemData.getName())
                    .append("\\n\\nСтоимость: ")
                    .append(Str.numFormat(item.getPrice()))
                    .append(" ")
                    .append(Str.morph(item.getPrice(), "очко", "очка", "очков"))
                    .append(" / шт.\">Купить</a></td></tr></table></td></tr></table></td></tr></table>")
                    .append("<img src=l2ui.squaregray width=616 height=1>");
        });

        if (visibleIndex.get() < pageSize) {
            int height = 48 * (pageSize - visibleIndex.get());
            html.append("<img height=").append(height).append(">");
        }

        int prevPage = currentPage - 1;
        if (prevPage < 1) {
            prevPage = 1;
        }

        content = content.replace("%items%", html.toString());
        content = content.replace("%battlePoints%", card.getPointsStr());
        content = content.replace("%prevPage%", String.valueOf(prevPage));
        content = content.replace("%nextPage%", String.valueOf(currentPage + 1));
        content = content.replace("%currentPage%", String.valueOf(currentPage));
        content = content.replace("%pageCount%", String.valueOf(pageCount));

        return content;
    }
}