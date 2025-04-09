package org.taskmanager;

import org.taskmanager.dao.UserDao;
import org.taskmanager.models.User;

public class Main {
    public static void main(String[] args) {

        UserDao userDao = new UserDao();
        //userDao.createNewUser(new User("test5", "1234", "debil"));
        userDao.updateUsername(2, "ololo");
        userDao.updateUserRole(2,"шнырь");
        userDao.updateUserPassword(2,"43221");
    }
}