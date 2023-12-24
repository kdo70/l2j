package net.sf.l2j.gameserver.expander.gatekeeper.data.xml;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.gameserver.expander.gatekeeper.conditions.PopularCondition;
import net.sf.l2j.gameserver.expander.gatekeeper.enums.MenuEnum;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocationsData implements IXmlReader {
    private final Map<Integer, Map<Integer, LocationHolder>> _lists = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> _statistics = new ConcurrentHashMap<>();
    private final Map<Integer, LocationHolder> _popularList = new ConcurrentHashMap<>();
    private final PopularCondition _popularCondition = new PopularCondition();
    private static final String SELECT_LOCATIONS_COUNT = "SELECT location_id,count FROM teleport_locations";
    private static final String ADD_OR_UPDATE_LOCATION = "INSERT INTO teleport_locations (location_id,count) VALUES (?,?) ON DUPLICATE KEY UPDATE count=VALUES(count)";
    private static final int _reloadDelay = 1000 * 60 * 30;

    protected LocationsData() {
        load();
        ThreadPool.scheduleAtFixedRate(this::reload, _reloadDelay, _reloadDelay);
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/gatekeeper/locations.xml");
        LOGGER.info("Loaded {} gatekeeper lists.", _lists.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        restoreLocationsCount();

        forEach(doc, "list", listNode -> forEach(listNode, "list", list -> {
            final NamedNodeMap npcAttributes = list.getAttributes();
            final int listId = Integer.parseInt(npcAttributes.getNamedItem("id").getNodeValue());

            final Map<Integer, LocationHolder> locations = new ConcurrentHashMap<>();

            forEach(list, "loc", location -> {
                final NamedNodeMap locationAttributes = location.getAttributes();
                final int locationId = Integer.parseInt(locationAttributes.getNamedItem("id").getNodeValue());
                final int count = _statistics.get(locationId) == null ? 0 : _statistics.get(locationId);
                final LocationHolder locationHolder = new LocationHolder(parseAttributes(location), count);

                locations.put(locationId, locationHolder);

                if (_popularCondition.execute(_statistics, locationHolder, _popularList.size())) {
                    _popularList.put(locationId, locationHolder);
                }
            });

            _lists.put(listId, listId == MenuEnum.POPULAR.getId() ? _popularList : locations);
        }));
    }

    public void restoreLocationsCount() {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_LOCATIONS_COUNT)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int index = rs.getInt("location_id");
                    int count = rs.getInt("count");

                    _statistics.put(index, count);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() {
        updateLocationsCounter();

        _lists.clear();
        _popularList.clear();

        load();
    }

    public void updateLocationsCounter() {
        for (Map<Integer, LocationHolder> map : _lists.values()) {
            for (LocationHolder locationHolder : map.values()) {
                try (Connection con = ConnectionPool.getConnection();
                     PreparedStatement ps = con.prepareStatement(ADD_OR_UPDATE_LOCATION)) {
                    ps.setInt(1, locationHolder.getId());
                    ps.setInt(2, locationHolder.getTeleportCount());

                    ps.executeUpdate();
                } catch (final Exception e) {
                    LOGGER.error("Error update location counter.", e);
                }
            }
        }
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