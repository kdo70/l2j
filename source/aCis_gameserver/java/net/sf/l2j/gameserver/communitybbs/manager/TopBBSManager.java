package net.sf.l2j.gameserver.communitybbs.manager;

import net.sf.l2j.gameserver.expander.cards.HuntingCard;
import net.sf.l2j.gameserver.model.actor.Player;

import java.util.StringTokenizer;

public class TopBBSManager extends BaseBBSManager {

    protected TopBBSManager() {
    }

    @Override
    public void parseCmd(String command, Player player) {
        if (command.equals("_bbshome")) {
            String content = HuntingCard.getPageIndex(player);
            separateAndSend(content, player);
        } else if (command.startsWith("_bbshome;")) {
            final StringTokenizer st = new StringTokenizer(command, ";");
            st.nextToken();

            loadStaticHtm(st.nextToken(), player);
        } else
            super.parseCmd(command, player);
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