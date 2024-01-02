package net.sf.l2j.gameserver.expander.cards.data.xml;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.expander.cards.model.holder.CardLvlHolder;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HuntingCardData implements IXmlReader {
    private final Map<Integer, CardLvlHolder> _cards = new HashMap<>();

    protected HuntingCardData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/cards/hunting.xml");
        LOGGER.info("Loaded {} hunting card levels.", _cards.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", list -> forEach(list, "item", item ->
        {
            final StatSet set = parseAttributes(item);
            final int level = set.getInteger("lvl");

            _cards.put(level, new CardLvlHolder(set));
        }));
    }

    public CardLvlHolder getCard(int level) {
        return _cards.get(level);
    }

    public Map<Integer, CardLvlHolder> getCards() {
        return _cards;
    }

    public static HuntingCardData getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final HuntingCardData _instance = new HuntingCardData();
    }
}