package net.sf.l2j.gameserver.communitybbs.manager;

import net.sf.l2j.gameserver.expander.cards.HuntingCard;
import net.sf.l2j.gameserver.expander.cards.actions.hunting.AddItemAction;
import net.sf.l2j.gameserver.expander.cards.actions.hunting.AddSkillAction;
import net.sf.l2j.gameserver.model.actor.Player;

import java.util.StringTokenizer;

public class TopBBSManager extends BaseBBSManager {

    protected final AddItemAction _addItemAction = new AddItemAction();
    protected final AddSkillAction _addSkillAction = new AddSkillAction();

    protected TopBBSManager() {
    }

    @Override
    public void parseCmd(String event, Player player) {
        final StringTokenizer command = new StringTokenizer(event, " ");
        final String bbsPage = command.hasMoreTokens() ? command.nextToken() : null;
        final String action = command.hasMoreTokens() ? command.nextToken() : "";
        final int index = command.hasMoreTokens() ? Integer.parseInt(command.nextToken()) : 1;
        final int skillLvl = command.hasMoreTokens() ? Integer.parseInt(command.nextToken()) : 1;

        switch (action) {
            case "Reward" -> _addItemAction.execute(player, index);
            case "Skill" -> _addSkillAction.execute(player, index, skillLvl);
        }

        separateAndSend(HuntingCard.getPageIndex(player), player);
    }

    @Override
    protected String getFolder() {
        return "top/";
    }

    public static TopBBSManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final TopBBSManager INSTANCE = new TopBBSManager();
    }
}