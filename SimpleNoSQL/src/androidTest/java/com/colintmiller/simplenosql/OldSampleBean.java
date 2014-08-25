package com.colintmiller.simplenosql;

/**
 * This is going to represent SampleBean in an older form. Basically before we added additional fields into it.
 */
public class OldSampleBean {
    private String name;
    private String field1;
    private int id;

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
}
