package net.sf.l2j.gameserver.model.actor.container.player.custom.xml;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopData implements IXmlReader {
    private final Map<String, List<ShopItem>> _shops = new HashMap<>();

    protected ShopData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/shop.xml");
        LOGGER.info("Loaded {} expander shops.", _shops.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", listNode -> forEach(listNode, "shop", shop ->
        {
            final NamedNodeMap telPosListAttrs = shop.getAttributes();
            final String type = String.valueOf(telPosListAttrs.getNamedItem("type").getNodeValue());

            final List<ShopItem> items = new ArrayList<>();
            forEach(shop, "item", shopNode -> items.add(new ShopItem(parseAttributes(shopNode))));

            _shops.put(type, items);
        }));
    }

    public void reload() {
        _shops.clear();

        load();
    }

    public List<ShopItem> getItems(String type) {
        return _shops.get(type);
    }

    public static void buy(Player player, String type, int index, int count) {
        index -= 1;

        List<ShopItem> items = ShopData.getInstance().getItems(type);
        if (index > items.size() || count < 1) {
            LOGGER.error("A nonexistent store index was called");
            return;
        }

        ShopItem item = items.get(index);
        if (item.getLimit() < count) {
            player.sendMessage(
                    "В магазине недостаточно "
                            + ItemData.getInstance().getTemplate(item.getItemId()).getName()
            );

            return;
        }


        if (!player.getInventory().validateCapacityByItemId(item.getItemId(), item.getCount() * count)) {
            player.sendMessage("Инвентарь переполнен");
            return;
        }

        int points = player.getCards().get(type).getPoints();

        if (points < item.getPrice() * count) {
            int diff = (item.getPrice() * count) - points;
            player.sendMessage(
                    "Недостаточно очков для покупки "
                            + ItemData.getInstance().getTemplate(item.getItemId()).getName()
                            + ", нехватает"
                            + " "
                            + Str.numFormat(diff)
                            + " "
                            + Str.morph(diff, "очко", "очка", "очков"));

            return;
        }

        final int totalPrice = item.getPrice() * count;
        final int balance = player.getCards().get(type).getPoints();

        player.sendMessage("Списано "
                + Str.numFormat(totalPrice)
                + " "
                + Str.morph(totalPrice, "очко", "очка", "очков")
                + ", остаток"
                + " "
                + Str.numFormat(balance)
                + " "
                + Str.morph(balance, "очко", "очка", "очков"));

        ItemInstance itemInstance = new ItemInstance(IdFactory.getInstance().getNextId(), item.getItemId());
        final int totalCount = item.getCount() * count;
        player.getCards().get(type).setPoints(points - item.getPrice() * count);

        if (itemInstance.isStackable()) {
            player.addItem("shop", item.getItemId(), totalCount, null, true);
        } else {
            for (int i = 0; i < totalCount; i++) {
                player.addItem("shop", item.getItemId(), 1, null, true);
            }
        }

        item.setLimit(item.getLimit() - count);
        item.setSoldCount(item.getSoldCount() + count);
    }

    public static ShopData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final ShopData INSTANCE = new ShopData();
    }
}