package net.sf.l2j.gameserver.expander.gatekeeper.data.dto;

import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;

import java.util.List;

public class GatekeeperHtmlDto {
    private final Integer _itemInPage;
    private final StringBuilder _list;
    private final Boolean _hasMore;
    private final Integer _page;
    private final Integer _listId;
    private final List<LocationHolder> _filteredList;

    public GatekeeperHtmlDto(
            Integer itemInPage,
            StringBuilder list,
            Boolean hasMore,
            Integer page,
            Integer listId,
            List<LocationHolder> filteredList
    ) {
        _itemInPage = itemInPage;
        _list = list;
        _hasMore = hasMore;
        _page = page;
        _listId = listId;
        _filteredList = filteredList;
    }

    public Integer getItemInPage() {
        return _itemInPage;
    }

    public StringBuilder getList() {
        return _list;
    }

    public Boolean getHasMore() {
        return _hasMore;
    }

    public Integer getPage() {
        return _page;
    }

    public Integer getListId() {
        return _listId;
    }

    public List<LocationHolder> getFilteredList() {
        return _filteredList;
    }
}
