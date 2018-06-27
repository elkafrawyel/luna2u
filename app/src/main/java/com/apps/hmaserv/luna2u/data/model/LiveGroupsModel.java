package com.apps.hmaserv.luna2u.data.model;

public class LiveGroupsModel {

    private String id;
    private String name;

    public LiveGroupsModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
