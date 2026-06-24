package com.wardrobe.backend.entity;

public class Size {
    private Integer id;
    private String sizeName;
    private Integer typeId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getSizeName() { return sizeName; }
    public void setSizeName(String sizeName) { this.sizeName = sizeName; }
    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }
}
