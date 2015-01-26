package at.stefanproell.TomcatAuthentication;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class UserTest {
    public static void main(String[] args) {
        System.out.println("Persistent Identifier Mockup");
        UserTest u = new UserTest();
        u.testUsers();
        System.exit(0);


    }

    private void testUsers() {
        UserAPI userapi = new UserAPI();
        UserDetails u1 = userapi.createNewUser("stefanuser", "hallo");
        GroupDetails g = userapi.createnewGroup("arkusers");
        userapi.addUserToGroup(u1, g);


    }

}
