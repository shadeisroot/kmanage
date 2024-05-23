package org.example.kmanage.User;

public class Permissions {

    private int id;
    private String name;
    //CONSTRUCTOR
    public Permissions(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Permissions{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
    //gets n sets
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
