package Database.Authentication;


import org.hibernate.envers.Audited;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Created by stefan on 18.06.14.
 */
@Entity
@Audited
@Table(name = "user")
public class User implements Serializable {
    private String username;
    private String password;
    private Long user_id;
    private int organizational_id;

    private String firstName;
    private String lastName;



    /*
    * Create a new user
    *  */
    public User(String username, String firstname, String lastname, String password, int organizational_id) {
        this.username = username;
        this.firstName = firstname;
        this.lastName = lastname;
        this.password = this.hashPassword(password);
        this.organizational_id = organizational_id;
    }

    public User() {
    }

    @Column(name = "username", unique = true)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password", columnDefinition = "char(60)")
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {


        this.password = password;
    }

    /*
* 
* Needed for the persistent identifiers */
    @Column(name = "organizational_id")
    public int getOrganizational_id() {
        return organizational_id;
    }

    public void setOrganizational_id(int organizational_id) {
        this.organizational_id = organizational_id;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    @Column(name = "first_name", unique = false, nullable = false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name", unique = false, nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /*
        * Hash the password
        * * * */
    private String hashPassword(String input) {

        // gensalt's log_rounds parameter determines the complexity the work factor is 2**log_rounds, and the default
        // is 10
        String hashedInput = BCrypt.hashpw(input, BCrypt.gensalt(12));

        return hashedInput;


    }
}
