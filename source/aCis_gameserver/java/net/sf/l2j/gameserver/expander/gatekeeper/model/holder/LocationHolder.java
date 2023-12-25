package net.sf.l2j.gameserver.expander.gatekeeper.model.holder;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.gameserver.enums.TeleportType;
import net.sf.l2j.gameserver.model.location.Location;

public class LocationHolder extends Location {
    private final int _id;
    private int _childId;
    private int _teleportCount;
    private String _placeholder;
    private final String _name;
    private String _desc;
    private final String _point;
    private final TeleportType _type;
    private final int _priceId;
    private final int _priceCount;
    private final int _castleId;
    private final StatSet _statSet;

    public LocationHolder(StatSet set, int teleportCount) {
        super(set.getInteger("x"), set.getInteger("y"), set.getInteger("z"));

        _id = set.getInteger("id");
        _teleportCount = teleportCount;
        _name = set.getString("name");
        _point = set.getString("point", "");
        _type = set.getEnum("type", TeleportType.class, TeleportType.STANDARD);
        _priceId = set.getInteger("priceId");
        _priceCount = set.getInteger("priceCount");
        _castleId = set.getInteger("castleId", 0);
        _statSet = set;
    }

    public int getId() {
        return _id;
    }

    public int getChildId() {
        return _childId;
    }

    public void setChildId(int childId) {
        this._childId = childId;
    }

    public int getTeleportCount() {
        return _teleportCount;
    }

    public void setTeleportCount(int teleportCount) {
        this._teleportCount = teleportCount;
    }

    public String getPlaceholder() {
        return _placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this._placeholder = placeholder;
    }

    public String getName() {
        return _name;
    }

    public String getDesc() {
        return _desc;
    }

    public void setDesc(String desc) {
        this._desc = desc;
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

    @Override
    public LocationHolder clone() {
        super.clone();

        return new LocationHolder(_statSet, _teleportCount);
    }
}