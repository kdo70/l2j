package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.expander.statistic.CharacterStatistic;
import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.cards.data.xml.HuntingCardData;
import net.sf.l2j.gameserver.expander.cards.model.holder.CardLvlHolder;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.actor.Player;

public class GetIndexPageAction extends Action {

    protected static final String CB_PATH = "data/html/CommunityBoard/";

    protected String getFolder() {
        return "top/";
    }
    protected static final GetRewardsAction _getRewardsAction = new GetRewardsAction();
    protected static final GetSkillAction _getSkillAction = new GetSkillAction();
    public String execute(Player player) {

        String content = HtmCache.getInstance().getHtm(CB_PATH + getFolder() + "index.htm");
        CharacterCardHolder card = player.getCards().get(CardsTypeEnum.MONSTER.getName());

        content = content.replace("%battlePoints%", Str.numFormat(card.getPoints()));
        content = content.replace("%battleLevel%", String.valueOf(card.getLevel()));
        content = content.replace("%percent%", getPercent(card));
        content = content.replace("%progress%", getProgress(card));
        content = content.replace("%rewardList%", getRewardList(player));
        content = content.replace("%rewardSkillInfo%", getRewardSkillInfo(player));
        content = content.replace("%monsterKills%", CharacterStatistic.getMonsterKills(player));
        content = content.replace("%monsterKillsToday%", CharacterStatistic.getMonsterKillsToday(player));
        content = content.replace("%monsterDeaths%", CharacterStatistic.getMonsterDeaths(player));
        content = content.replace("%monsterDeathsToday%", CharacterStatistic.getMonsterDeathsToday(player));
        content = content.replace("%exp%", CharacterStatistic.getExp(player));
        content = content.replace("%expToday%", CharacterStatistic.getExpToday(player));
        content = content.replace("%sp%", CharacterStatistic.getSp(player));
        content = content.replace("%spToday%", CharacterStatistic.getSpToday(player));
        content = content.replace("%adena%", CharacterStatistic.getAdena(player));
        content = content.replace("%adenaToday%", CharacterStatistic.getAdenaToday(player));
        content = content.replace("%gold%", CharacterStatistic.getGold(player));
        content = content.replace("%goldToday%", CharacterStatistic.getGoldToday(player));
        content = content.replace("%items%", CharacterStatistic.getItems(player));
        content = content.replace("%itemsToday%", CharacterStatistic.getItemsToday(player));
        content = content.replace("%equip%", CharacterStatistic.getEquip(player));
        content = content.replace("%equipToday%", CharacterStatistic.getEquipToday(player));

        return content;
    }

    public static String getRewardList(Player player) {
        return _getRewardsAction.execute(player);
    }

    public static String getRewardSkillInfo(Player player) {
        return _getSkillAction.execute(player);
    }

    public static String getPercent(CharacterCardHolder card) {
        double percent = 0;

        if (card.getLevel() < 210) {
            CardLvlHolder nextBattleLevel = HuntingCardData.getInstance().getCard(card.getLevel() + 1);
            percent = ((double) card.getExp() / (double) nextBattleLevel.getExp()) * 100;
        }

        return Str.percent(percent);
    }

    public static String getProgress(CharacterCardHolder card) {
        double percent;
        double progress = 0;

        if (card.getLevel() < 210) {
            CardLvlHolder nextBattleLevel = HuntingCardData.getInstance().getCard(card.getLevel() + 1);
            percent = ((double) card.getExp() / (double) nextBattleLevel.getExp()) * 100;
            progress = 2.85 * percent;
        }

        return Str.percent(progress);
    }
}
