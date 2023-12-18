package net.sf.l2j.gameserver.communitybbs.manager;

import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.xml.ShopData;

import java.util.StringTokenizer;

public class FavoriteBBSManager extends BaseBBSManager {

    protected FavoriteBBSManager() {
    }

    @Override
    public void parseCmd(String command, Player player) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        st.nextToken();

        int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
        int itemIndex = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0;
        int itemCount = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;

        if (itemIndex > 0) {
            ShopData.buy(player, "monster", itemIndex, itemCount);
        }

        String content = HtmCache.getInstance().getHtm(CB_PATH + getFolder() + "index.htm");
        content = player.getCards().get("monster").getPageShop(content, page);
        separateAndSend(content, player);
    }

    @Override
    protected String getFolder() {
        return "favorite/";
    }

    public static FavoriteBBSManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final FavoriteBBSManager INSTANCE = new FavoriteBBSManager();
    }
}