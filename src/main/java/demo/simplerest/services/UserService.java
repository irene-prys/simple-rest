package demo.simplerest.services;

import demo.simplerest.entities.User;

import java.util.List;

public interface UserService {
    void remove(Long userId);

    User create(User user);// todo: add validation for phone

    User update(User user);

    List<User> findAll();

    User findById(long id);

    List<User> findByName(String name);
}
