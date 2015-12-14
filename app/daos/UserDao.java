package daos;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import models.User;

/**
 * Created by nookio on 15/5/12.
 */
public class UserDao {

    private static Model.Finder<Integer, User> find = new Model.Finder<Integer, User>(User.class);

    public static User findUserById( Integer id){
        return  find.byId(id);
    };

    public static void save(User user){
            Ebean.save(user);
    }
}
