package org.example.kmanage;

import java.util.List;

public class Role {

    private int id;
    private Permissions permissions;

    public Role(int id, Permissions permissions) {
        this.id = id;
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", permissions=" + permissions +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }
}
