package lld.practice.mobility.uber.service;

import lld.practice.mobility.uber.entity.user.User;

public interface UserManagementService<T extends User> {
    T createUser(T user);
    T updateUser(T user);
    boolean deleteUser(String id);
    T getUser(String id);
}
