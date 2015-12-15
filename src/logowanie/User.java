package logowanie;

public class User 
{
    private final String password;
    private final String login;
            public User(String password, String login)
            {
                this.password=password;
                this.login=login;
            }
            public String getPassword() 
            {
                return password;
            }
            public String getLogin() 
            {
                return login;
            }
            
            
}