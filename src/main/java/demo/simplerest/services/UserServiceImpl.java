package demo.simplerest.services;

import demo.simplerest.InvalidDataException;
import demo.simplerest.entities.User;
import demo.simplerest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User create(User user) throws InvalidDataException {
        validateUser(user);
        user.setId(null);
        user.setPhone(formatPhone(user.getPhone()));
        return userRepository.save(user);
    }

    @Override
    public User update(User user) throws InvalidDataException {
        validateUser(user);
        user.setPhone(formatPhone(user.getPhone()));
        return userRepository.save(user);
    }

    @Override
    public void remove(Long userId) {
        userRepository.delete(userId);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findByName(String name) {
        return userRepository.findByNameStartingWithIgnoreCase(name);
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    private String formatPhone(String phone) {
        return phone == null ? null : phone.trim();
    }

    private void validateUser(User user) throws InvalidDataException {
        if (isFieldEmpty(user.getName())) {
            throw new InvalidDataException("User name is mandatory");
        }
        if (!isFieldEmpty(user.getPhone())) {
            User foundUser = findByPhone(user.getPhone().trim());
            if (foundUser != null && !foundUser.getId().equals(user.getId())) {
                throw new InvalidDataException("User with such phone already exists");
            }
        }
    }

    private boolean isFieldEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
