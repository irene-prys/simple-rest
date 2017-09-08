package demo.simplerest.services;

import demo.simplerest.InvalidDataException;
import demo.simplerest.entities.User;

import java.util.List;

public interface UserService {
    void remove(Long userId);

    User create(User user) throws InvalidDataException;

    User update(User user) throws InvalidDataException;

    List<User> findAll();

    User findById(long id);

    List<User> findByName(String name);

    User findByPhone(String phone);
}
