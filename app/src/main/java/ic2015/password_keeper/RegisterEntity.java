package ic2015.password_keeper;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class RegisterEntity {

    @Id
    private long id;

    private String username;

    @Index
    private String email;

    @Index
    private String password_sha256;

    public RegisterEntity(){
    }

    public RegisterEntity(String username, String email, String password_sha256){
        this.username = username;
        this.email = email;
        this.password_sha256 = password_sha256;
    }

    public RegisterEntity(long id, String username, String email, String password_sha256){
        this.id = id;
        this.username = username;
        this.email = email;
        this.password_sha256 = password_sha256;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword_sha256(String password_sha256) {
        this.password_sha256 = password_sha256;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword_sha256() {
        return password_sha256;
    }
}
