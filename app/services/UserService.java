package services;

import com.avaje.ebean.Model;
import daos.UserDao;
import models.User;

import java.util.Date;

/**
 * Created by nookio on 15/5/13.
 */
public class UserService {

    private static Model.Finder<Integer, User> finder = new Model.Finder<Integer, User>(User.class);

    public static User getUserById(Integer id){
        return UserDao.findUserById(id);
    }

    public static void saveUser(User user){
        UserDao.save(user);
    }

    public static Integer updatePaymentInfoOfUser(Integer id, Short status, Short userType, Short payType, Date payedAt, Date expiredDate){
        User user  = UserDao.findUserById(id);
        user.payed = status;
        user.userType = userType;
        user.shopId = null;
        user.companyId = null;
        user.payedDate = payedAt;
        user.payType = payType;
        user.expiredDate = expiredDate;
        UserDao.save(user);
        return user.id;
    }

}
