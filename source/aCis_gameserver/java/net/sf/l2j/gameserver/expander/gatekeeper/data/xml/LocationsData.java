package net.sf.l2j.gameserver.expander.gatekeeper.data.xml;

import net.sf.l2j.Config;
import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;
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
    private final Map<Integer, LocationHolder> _locations = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> _statistics = new ConcurrentHashMap<>();
    private static final String SELECT_LOCATIONS_COUNT = "SELECT location_id,count FROM teleport_locations ORDER BY count DESC";
    private static final String ADD_OR_UPDATE_LOCATION = "INSERT INTO teleport_locations (location_id,count) VALUES (?,?) ON DUPLICATE KEY UPDATE count=VALUES(count)";
    private static final int _reloadDelay = 1000 * 60 * Config.TELEPORT_POPULAR_REFRESH_DELAY;

    protected LocationsData() {
        load();
        ThreadPool.scheduleAtFixedRate(this::reload, _reloadDelay, _reloadDelay);
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/gatekeeper/locations.xml");
        LOGGER.info("Loaded {} gatekeeper locations", _locations.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        restoreLocationsCount();

        forEach(doc, "list", listNode -> forEach(listNode, "loc", location -> {
            final NamedNodeMap locationAttributes = location.getAttributes();
            final int locationId = Integer.parseInt(locationAttributes.getNamedItem("id").getNodeValue());
            final int count = _statistics.get(locationId) == null ? 0 : _statistics.get(locationId);
            final LocationHolder locationHolder = new LocationHolder(parseAttributes(location), count);

            _locations.put(locationId, locationHolder);
        }));
    }

    public void restoreLocationsCount() {
        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_LOCATIONS_COUNT)) {
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
        _locations.clear();
        load();
        MenuData.getInstance().reload();
    }

    public void updateLocationsCounter() {
        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(ADD_OR_UPDATE_LOCATION)) {

            for (LocationHolder location : _locations.values()) {
                ps.setInt(1, location.getId());
                ps.setInt(2, location.getTeleportCount());

                ps.addBatch();
            }

            ps.executeBatch();
        } catch (final Exception e) {
            LOGGER.error("Error update location counter.", e);
        }
    }

    public LocationHolder getLocation(int id) {
        return _locations.get(id);
    }

    public Map<Integer, LocationHolder> getLocations() {
        return _locations;
    }

    public static LocationsData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final LocationsData INSTANCE = new LocationsData();
    }
}