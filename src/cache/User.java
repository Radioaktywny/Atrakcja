package cache;

import android.database.sqlite.SQLiteDatabase;

public class User 
{
    private final String password;
    private final String login;
    private boolean czyprofilowka=false;
            public User(String login, String password, String profilowka)
            {
                this.password=password;
                this.login=login;
                if(profilowka.equals("tak"))
                    czyprofilowka=true;
            }
            public String getPassword() 
            {
                return password;
            }
            public String getLogin() 
            {
                return login;
            }
            public Boolean czy_jest_profilowe()
            {
                return czyprofilowka;
            }
            
}
