package at.stefanproell.Authentication;


import javax.persistence.*;

@Entity
@Table(name = "tomcat_groups")
public class GroupDetails {

    private long id;
    private String name;


    public GroupDetails() {

    }

    public GroupDetails(String name) {
        this.name = name;
    }


    @Column(name = "group_name", unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}