package net.sf.l2j.gameserver.expander.cards.actions.hunting.htm;

import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;

public class GetIndexPageAction extends Action {

    protected static final String _template = "data/html/script/feature/cards/hunting/index.htm";
    protected static final GetHeaderAction _getHeaderAction = new GetHeaderAction();
    protected static final GetProgressAction _getProgressAction = new GetProgressAction();
    protected static final GetRewardsAction _getRewardsAction = new GetRewardsAction();
    protected static final GetStatsLeftAction _getStatsLeftAction = new GetStatsLeftAction();
    protected static final GetStatsRightAction _getStatsRightAction = new GetStatsRightAction();
    protected static final GetSkillInfoAction _getSkillInfoAction = new GetSkillInfoAction();

    public String execute(Player player) {
        String template = HtmCache.getInstance().getHtm(_template);
        CharacterCardHolder card = player.getCards().get(CardsTypeEnum.MONSTER.getName());

        template = template.replace("%header%", _getHeaderAction.execute(card));
        template = template.replace("%progress%", _getProgressAction.execute(card));
        template = template.replace("%rewards%", _getRewardsAction.execute(card));
        template = template.replace("%huntingStatsLeft%", _getStatsLeftAction.execute(player));
        template = template.replace("%huntingStatsRight%", _getStatsRightAction.execute(player));
        template = template.replace("%skillInfo%", _getSkillInfoAction.execute(player));

        return template;
    }
}
