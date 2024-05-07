package org.example.kmanage.User;

public class Profile {
    private String name;
    private String Position;
    private String Department;

    public Profile(String name, String Position, String Department) {
        this.name = name;
        this.Position = Position;
        this.Department = Department;
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
