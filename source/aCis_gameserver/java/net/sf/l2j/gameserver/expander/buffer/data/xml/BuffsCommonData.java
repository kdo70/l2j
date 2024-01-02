package net.sf.l2j.gameserver.expander.buffer.data.xml;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BuffsCommonData implements IXmlReader {
    private final List<BuffHolder> _buffs = new ArrayList<>();

    protected BuffsCommonData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/Buffer/common.xml");
        LOGGER.info("Loaded {} common buffs", _buffs.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", listNode -> forEach(listNode, "buff", buffNode -> {
            final StatSet set = parseAttributes(buffNode);
            _buffs.add(new BuffHolder(set));
        }));
    }

    public List<BuffHolder> getBuffs() {
        return _buffs;
    }

    public static BuffsCommonData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final BuffsCommonData INSTANCE = new BuffsCommonData();
    }
}