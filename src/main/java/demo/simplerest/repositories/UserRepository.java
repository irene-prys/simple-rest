package demo.simplerest.repositories;


import demo.simplerest.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findById(long id);

    List<User> findByNameStartingWithIgnoreCase(String name);

    User findByPhone(String phone);
}
