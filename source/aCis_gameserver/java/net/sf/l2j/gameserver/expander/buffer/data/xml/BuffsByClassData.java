package net.sf.l2j.gameserver.expander.buffer.data.xml;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuffsByClassData implements IXmlReader {

    private final List<BuffHolder> _buffs = new ArrayList<>();

    private int _minLvl = 80;

    private int _maxLvl = 1;

    protected BuffsByClassData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/Buffer/classes.xml");
        LOGGER.info("Loaded {} classes buffs", _buffs.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", listNode -> forEach(listNode, "buff", buffNode -> {
            final StatSet set = parseAttributes(buffNode);
            final int minLvl = set.getInteger("minLvl");
            final int maxLvl = set.getInteger("maxLvl");

            if (minLvl < _minLvl) {
                _minLvl = minLvl;
            }

            if (maxLvl > _maxLvl) {
                _maxLvl = maxLvl;
            }

            _buffs.add(new BuffHolder(set));
        }));
    }

    public List<BuffHolder> getValidBuffs(boolean isMystic, int lvl) {
        return _buffs.stream().filter(b -> b.isMagic() == isMystic && lvl >= b.getMinLvl() && lvl <= b.getMaxLvl()).collect(Collectors.toList());
    }

    public List<BuffHolder> getValidBuffs(boolean isMystic) {
        return _buffs.stream().filter(b -> b.isMagic() == isMystic).collect(Collectors.toList());
    }

    public int getMinLvl() {
        return _minLvl;
    }

    public int getMaxLvl() {
        return _maxLvl;
    }

    public static BuffsByClassData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final BuffsByClassData INSTANCE = new BuffsByClassData();
    }
}