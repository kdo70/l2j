package net.sf.l2j.gameserver.model.actor.container.player.custom.xml.cards;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.model.actor.container.player.custom.cards.HuntingCardLevel;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HuntingData implements IXmlReader {
    private final Map<Integer, HuntingCardLevel> _levels = new HashMap<>();

    protected HuntingData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/cards/hunting.xml");
        LOGGER.info("Loaded {} battle levels.", _levels.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", listNode -> forEach(listNode, "battleLevel", levelNode ->
        {
            final StatSet set = parseAttributes(levelNode);
            final int level = set.getInteger("level");

            _levels.put(level, new HuntingCardLevel(set));
        }));
    }

    public HuntingCardLevel getBattleLevel(int level) {
        return _levels.get(level);
    }

    public static HuntingData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final HuntingData INSTANCE = new HuntingData();
    }
}