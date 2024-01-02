package net.sf.l2j.gameserver.expander.cards.data.xml;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.expander.cards.model.holder.ShopItemHolder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopData implements IXmlReader {
    private final Map<String, List<ShopItemHolder>> _shops = new HashMap<>();

    protected ShopData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/cards/shop.xml");
        LOGGER.info("Loaded {} card shops.", _shops.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", list -> forEach(list, "shop", shop ->
        {
            final NamedNodeMap shopAttributes = shop.getAttributes();
            final String type = String.valueOf(shopAttributes.getNamedItem("type").getNodeValue());
            final List<ShopItemHolder> items = new ArrayList<>();

            forEach(shop, "item", shopItem -> items.add(new ShopItemHolder(parseAttributes(shopItem))));

            _shops.put(type, items);
        }));
    }

    public void reload() {
        _shops.clear();

        load();
    }

    public List<ShopItemHolder> getItems(String type) {
        return _shops.get(type);
    }

    public static ShopData getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final ShopData _instance = new ShopData();
    }
}