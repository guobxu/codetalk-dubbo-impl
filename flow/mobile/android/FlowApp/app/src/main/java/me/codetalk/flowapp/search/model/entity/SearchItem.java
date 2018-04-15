package me.codetalk.flowapp.search.model.entity;

/**
 * Created by guobxu on 2018/1/22.
 */

public class SearchItem {

    private String text;
    private Long lastSearch;

    public SearchItem() {}

    public SearchItem(String text, Long lastSearch) {
        this.text = text;
        this.lastSearch = lastSearch;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getLastSearch() {
        return lastSearch;
    }

    public void setLastSearch(Long lastSearch) {
        this.lastSearch = lastSearch;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj instanceof SearchItem) {
            SearchItem item = (SearchItem)obj;

            return text.equals(item.text);
        }

        return false;
    }
}
