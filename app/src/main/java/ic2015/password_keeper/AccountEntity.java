package ic2015.password_keeper;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;


@Entity
public class AccountEntity {

    @Id
    private long id;

    @Index
    private String title;

    private String user_name;

    @Index
    private String password;

    private String email;

    private String url_address;

    private String phone;

    private String question;

    private String answer;

    public ToOne<RegisterEntity> accountToRegister;

    public AccountEntity(){
    }

    public AccountEntity(String title, String user_name, String password) {
        this.title = title;
        this.user_name = user_name;
        this.password = password;
    }

    public AccountEntity(String title, String user_name, String password, String email,
                         String url_address, String phone, String question, String answer) {
        this.title = title;
        this.user_name = user_name;
        this.password = password;
        this.email = email;
        this.url_address = url_address;
        this.phone = phone;
        this.question = question;
        this.answer = answer;
    }

    public AccountEntity(long id, String title, String user_name, String password, String email,
                         String url_address, String phone, String question, String answer,
                         ToOne<RegisterEntity> accountToRegister) {
        this.id = id;
        this.title = title;
        this.user_name = user_name;
        this.password = password;
        this.email = email;
        this.url_address = url_address;
        this.phone = phone;
        this.question = question;
        this.answer = answer;
        this.accountToRegister = accountToRegister;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl_address() {
        return url_address;
    }

    public void setUrl_address(String url_address) {
        this.url_address = url_address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


    public ToOne<RegisterEntity> getAccountToRegister() {
        return accountToRegister;
    }

    public void setAccountToRegister(ToOne<RegisterEntity> accountToRegister) {
        this.accountToRegister = accountToRegister;
    }

}


