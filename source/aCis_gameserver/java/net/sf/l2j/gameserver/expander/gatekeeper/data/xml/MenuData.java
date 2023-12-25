package net.sf.l2j.gameserver.expander.gatekeeper.data.xml;

import net.sf.l2j.Config;
import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.expander.gatekeeper.enums.MenuEnum;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.MenuHolder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MenuData implements IXmlReader {
    private final Map<Integer, Map<Integer, LocationHolder>> _menu = new ConcurrentHashMap<>();
    private static final int _popularLimit = Config.TELEPORT_POPULAR_LIMIT;

    protected MenuData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/gatekeeper/menu.xml");
        LOGGER.info("Loaded {} gatekeeper menu.", _menu.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "menu", listNode -> forEach(listNode, "list", list -> {
            final NamedNodeMap npcAttributes = list.getAttributes();
            final int listId = Integer.parseInt(npcAttributes.getNamedItem("id").getNodeValue());

            final Map<Integer, LocationHolder> locations = new ConcurrentHashMap<>();

            forEach(list, "item", item -> {
                final MenuHolder menuHolder = new MenuHolder(parseAttributes(item));
                LocationHolder locationHolder = LocationsData.getInstance().getLocation(menuHolder.getLocId()).clone();

                locationHolder.setChildId(menuHolder.getChildId());
                locationHolder.setPlaceholder(menuHolder.getPlaceholder());
                locationHolder.setDesc(menuHolder.getDesc());

                locations.put(menuHolder.getLocId(), locationHolder);
            });

            _menu.put(listId, listId == MenuEnum.POPULAR.getId() ? getPopularList() : locations);
        }));
    }

    private Map<Integer, LocationHolder> getPopularList() {
        return Optional.ofNullable(LocationsData.getInstance().getLocations())
                .orElse(Collections.emptyMap())
                .values()
                .stream()
                .filter(location -> location.getCastleId() == 0)
                .filter(location -> location.getTeleportCount() > 0)
                .limit(_popularLimit)
                .collect(Collectors.toConcurrentMap(LocationHolder::getId, Function.identity()));
    }

    public void reload() {
        load();
    }

    public Map<Integer, LocationHolder> getList(int id) {
        return _menu.get(id);
    }

    public static MenuData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final MenuData INSTANCE = new MenuData();
    }
}