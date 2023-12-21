package net.sf.l2j.gameserver.expander.gatekeeper.data.xml;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocationsData implements IXmlReader {
    private final Map<Integer, Map<Integer, LocationHolder>> _lists = new ConcurrentHashMap<>();

    protected LocationsData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/gatekeeper/locations.xml");
        LOGGER.info("Loaded {} gatekeeper lists.", _lists.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", listNode -> forEach(listNode, "list", list -> {
            final NamedNodeMap npcAttributes = list.getAttributes();
            final int listId = Integer.parseInt(npcAttributes.getNamedItem("id").getNodeValue());

            final Map<Integer, LocationHolder> locations = new ConcurrentHashMap<>();

            forEach(list, "loc", location -> {
                final NamedNodeMap locationAttributes = location.getAttributes();
                final int locationId = Integer.parseInt(locationAttributes.getNamedItem("id").getNodeValue());

                locations.put(locationId, new LocationHolder(parseAttributes(location)));
            });

            _lists.put(listId, locations);
        }));
    }

    public void reload() {
        _lists.clear();
        load();
    }

    public Map<Integer, LocationHolder> getList(int id) {
        return _lists.get(id);
    }

    public static LocationsData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final LocationsData INSTANCE = new LocationsData();
    }
}