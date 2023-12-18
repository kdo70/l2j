package net.sf.l2j.gameserver.expander.helpers.data.xml;

import net.sf.l2j.commons.data.xml.IXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemIconData implements IXmlReader {
    private static final Map<Integer, String> _icons = new ConcurrentHashMap<>();
    private static final Map<Integer, String> _desc = new ConcurrentHashMap<>();
    private static final String _defaultIcon = "icon.NOIMAGE";
    private static final String _defaultDesc = "-";

    public static ItemIconData getInstance() {
        return SingletonHolder._instance;
    }

    protected ItemIconData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/Helpers/icons.xml");
        LOGGER.info("Loaded {} icons.", _icons.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        Node n = doc.getFirstChild();

        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if (d.getNodeName().equalsIgnoreCase("icon")) {

                NamedNodeMap attrs = d.getAttributes();

                final int itemId = Integer.parseInt(attrs.getNamedItem("itemId").getNodeValue());
                String iconName = attrs.getNamedItem("iconName").getNodeValue();
                String desc = attrs.getNamedItem("desc").getNodeValue();

                _icons.put(itemId, iconName);
                _desc.put(itemId, desc);
            }
        }
    }

    public static String getIcon(int id) {
        return _icons.get(id) == null ? _defaultIcon : _icons.get(id);
    }

    public static String getDesc(int id) {
        return _desc.get(id) == null ? _defaultDesc : _desc.get(id);
    }

    private static class SingletonHolder {
        protected static final ItemIconData _instance = new ItemIconData();
    }
}