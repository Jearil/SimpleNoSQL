package com.colintmiller.simplenosql;

import java.util.List;
import java.util.Map;

/**
 * A basic data object that might be saved to disk.
 */
public class SampleBean {

    private String name;
    private String field1;
    private int id;
    private boolean exists;
    private Map<String, String> mapping;
    private List<String> listing;
    private SampleBean innerBean;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public SampleBean getInnerBean() {
        return innerBean;
    }

    public void setInnerBean(SampleBean innerBean) {
        this.innerBean = innerBean;
    }

    public List<String> getListing() {
        return listing;
    }

    public void setListing(List<String> listing) {
        this.listing = listing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampleBean that = (SampleBean) o;

        if (exists != that.exists) return false;
        if (id != that.id) return false;
        if (field1 != null ? !field1.equals(that.field1) : that.field1 != null) return false;
        if (innerBean != null ? !innerBean.equals(that.innerBean) : that.innerBean != null) return false;
        if (mapping != null ? !mapping.equals(that.mapping) : that.mapping != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (field1 != null ? field1.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (exists ? 1 : 0);
        result = 31 * result + (mapping != null ? mapping.hashCode() : 0);
        result = 31 * result + (innerBean != null ? innerBean.hashCode() : 0);
        return result;
    }
}
