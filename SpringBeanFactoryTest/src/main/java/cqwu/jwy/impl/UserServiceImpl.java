package cqwu.jwy.impl;

import cqwu.jwy.UserDao;
import cqwu.jwy.UserService;

public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @Override
    public String info() {
        return "UserService";
    }

    /**
     * UserDao的Setter，由BeanFactory调用（DI）
     * @param userDao UserDao
     */
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
        System.out.println("BeanFactory调用了该方法并set了UserDao:" + userDao);
    }
}
