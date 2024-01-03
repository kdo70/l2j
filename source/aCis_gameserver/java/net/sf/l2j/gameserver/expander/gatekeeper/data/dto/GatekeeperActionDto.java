package net.sf.l2j.gameserver.expander.gatekeeper.data.dto;

public class GatekeeperActionDto {
    private final String _action;
    private final Integer _listId;
    private final Integer _locationId;
    private final String _parentAction;
    private final int _page;

    public GatekeeperActionDto(
            String action,
            Integer listId,
            Integer locationId,
            String parentAction,
            Integer page
    ) {
        _action = action;
        _listId = listId;
        _locationId = locationId;
        _parentAction = parentAction;
        _page = page;
    }

    public String getAction() {
        return _action;
    }

    public Integer getListId() {
        return _listId;
    }

    public Integer getLocationId() {
        return _locationId;
    }

    public String getParentAction() {
        return _parentAction;
    }

    public int getPage() {
        return _page;
    }
}
