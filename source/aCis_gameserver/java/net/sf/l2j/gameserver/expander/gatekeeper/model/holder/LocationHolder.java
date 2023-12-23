package net.sf.l2j.gameserver.expander.gatekeeper.model.holder;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.gameserver.enums.TeleportType;
import net.sf.l2j.gameserver.model.location.Location;

public class LocationHolder extends Location {
    private final int _id;
    private final int _childId;
    private int _teleportCount;
    private final String _lvl;
    private final String _name;
    private final String _desc;
    private final String _point;
    private final TeleportType _type;
    private final int _priceId;
    private final int _priceCount;
    private final int _castleId;

    public LocationHolder(StatSet set, int teleportCount) {
        super(set.getInteger("x"), set.getInteger("y"), set.getInteger("z"));

        _id = set.getInteger("id");
        _childId = set.getInteger("childId", 0);
        _teleportCount = teleportCount;
        _lvl = set.getString("lvl", "-");
        _name = set.getString("name");
        _desc = set.getString("desc", "");
        _point = set.getString("point", "");
        _type = set.getEnum("type", TeleportType.class, TeleportType.STANDARD);
        _priceId = set.getInteger("priceId");
        _priceCount = set.getInteger("priceCount");
        _castleId = set.getInteger("castleId", 0);
    }

    public int getId() {
        return _id;
    }

    public int getChildId() {
        return _childId;
    }

    public int getTeleportCount() {
        return _teleportCount;
    }

    public void setTeleportCount(int _teleportCount) {
        this._teleportCount = _teleportCount;
    }

    public String getLvl() {
        return _lvl;
    }

    public String getName() {
        return _name;
    }

    public String getDesc() {
        return _desc;
    }

    public String getPoint() {
        return _point;
    }

    public TeleportType getType() {
        return _type;
    }

    public int getPriceId() {
        return _priceId;
    }

    public int getPriceCount() {
        return _priceCount;
    }

    public int getCastleId() {
        return _castleId;
    }
}