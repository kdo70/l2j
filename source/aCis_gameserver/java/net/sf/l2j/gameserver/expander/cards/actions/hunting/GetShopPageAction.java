package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.cards.data.xml.ShopData;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.expander.helpers.data.xml.ItemIconData;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;

import java.util.concurrent.atomic.AtomicInteger;

public class GetShopPageAction extends Action {

    protected static final String CB_PATH = "data/html/CommunityBoard/";
    protected String getFolder() {
        return "favorite/";
    }
    public String execute(Player player, int page) {
        CharacterCardHolder card = player.getCards().get(CardsTypeEnum.MONSTER.getName());
        String content = HtmCache.getInstance().getHtm(CB_PATH + getFolder() + "index.htm");
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
        content = content.replace("%battlePoints%", Str.numFormat(card.getPoints()));
        content = content.replace("%prevPage%", String.valueOf(prevPage));
        content = content.replace("%nextPage%", String.valueOf(currentPage + 1));
        content = content.replace("%currentPage%", String.valueOf(currentPage));
        content = content.replace("%pageCount%", String.valueOf(pageCount));

        return content;
    }
}
