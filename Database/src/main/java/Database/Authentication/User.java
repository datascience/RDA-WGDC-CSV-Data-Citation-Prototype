/*
 * Copyright [2014] [Stefan Pröll]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/*
 * Copyright [2014] [Stefan Pröll]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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


    /*
    * Create a new user
    *  */
    public User(String username, String password) {
        this.username = username;
        this.password = this.hashPassword(password);
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
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
