package net.sf.l2j.gameserver.expander.cards.data.xml;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.expander.cards.model.holder.CardLvlHolder;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HuntingCardData implements IXmlReader {
    private final Map<Integer, CardLvlHolder> _lvlList = new HashMap<>();

    protected HuntingCardData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/cards/hunting.xml");
        LOGGER.info("Loaded {} hunting card levels", _lvlList.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", list -> forEach(list, "item", item ->
        {
            final StatSet set = parseAttributes(item);
            final int level = set.getInteger("lvl");

            _lvlList.put(level, new CardLvlHolder(set));
        }));
    }

    public CardLvlHolder getLvl(int level) {
        return _lvlList.get(level);
    }

    public Map<Integer, CardLvlHolder> getList() {
        return _lvlList;
    }

    public static HuntingCardData getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final HuntingCardData _instance = new HuntingCardData();
    }
}