package org.example.kmanage.User;

public class Profile {
    //felter
    private String name;
    private String Position;
    private String Department;
    private int id;
    //Constructor
    public Profile(String name, String Position, String Department, int id) {
        this.name = name;
        this.Position = Position;
        this.Department = Department;
        this.id = id;

    }
    //gets n sets n toString
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", Position='" + Position + '\'' +
                ", Department='" + Department + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

}
