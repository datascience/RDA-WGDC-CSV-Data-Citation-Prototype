package at.stefanproell.Authentication;


import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Entity
@Table(name = "tomcat_users")
public class UserDetails {

    private long id;
    private String name;
    private String passwordHash;


    private Logger logger;


    private Set<GroupDetails> groups = new HashSet<GroupDetails>(0);

    public UserDetails() {
        super();
        this.logger = Logger.getLogger(UserDetails.class.getName());
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "user_name", unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "password", unique = false, nullable = false)
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String password) {
        this.passwordHash = createHash(password);
    }


    public String createHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.update(password.getBytes());
        byte byteData[] = digest.digest();
        //convert bytes to hex chars
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    @ManyToMany
    public Set<GroupDetails> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupDetails> groups) {
        this.groups = groups;
    }

    public void addUserToGroup(GroupDetails group) {
        this.logger.info("Add user " + this.getName() + " to group " + group.getName());

        Set groups = this.getGroups();
        groups.add(group);
    }
}
