package org.example.kmanage;

import java.util.List;

public class Role {

    private int id;
    private String name;
    private List<Permissions> permissions;

    public Role(int id, String name, List<Permissions> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }
}
