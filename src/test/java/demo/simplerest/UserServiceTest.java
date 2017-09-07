package demo.simplerest;

import demo.simplerest.entities.User;
import demo.simplerest.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void shouldCreateUser() {
        User user = newUser("Jane", "123");
        User savedUser = userService.save(user);
        assertNotNull(savedUser.getId());
    }

    @Test
    public void shouldCreateUserIgnoringPresetId() {
        User user = newUser("Helen", "3222");
        user.setId(100l);
        User savedUser = userService.save(user);
        assertNotNull(savedUser.getId());
        assertNotEquals(user.getId(), savedUser.getId());
    }

    @Test
    public void shouldCreateUserWithTheSameName() {
        User user1 = newUser("Thomas", "3456789");
        User user2 = newUser("Thomas", "456");

        User savedUser1 = userService.save(user1);
        User savedUser2 = userService.save(user2);
        assertNotNull(savedUser1.getId());
        assertNotNull(savedUser2.getId());
        assertNotEquals(savedUser1.getId(), savedUser2.getId());
    }

    @Test
    public void shouldUpdateUser() {
        String initialName = "Lily";
        User user = newUser(initialName, "345");
        User savedUser = userService.save(user);
        savedUser.setName("Lilly");
        User updatedUser = userService.save(user);

        assertNotNull(updatedUser.getId());
        assertNotNull(updatedUser.getName());
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertNotEquals(initialName, updatedUser.getName());
    }

    @Test(expected = DataAccessException.class)
    public void shouldNotAllowCreateUserWithDuplicatedPhones() {
        final String phone = "2233";
        User user1 = newUser("Ben Affleck", phone);
        User user2 = newUser("Jennifer Lopez", phone);

        userService.save(user1);
        userService.save(user2);
    }

    @Test
    public void shouldCreateUserWithoutPhone() {
        User user1 = newUser("Mary", null);
        assertNotNull(userService.save(user1).getId());
    }

    @Test
    public void shouldCreateSeveralUsersWithoutPhone() {
        User user1 = newUser("Benny", null);
        User user2 = newUser("Willy", null);

        User savedUser1 = userService.save(user1);
        User savedUser2 = userService.save(user2);

        assertNotNull(savedUser1.getId());
        assertNotNull(savedUser2.getId());
        assertNotEquals(savedUser1.getId(), savedUser2.getId());
    }

    @Test
    public void shouldFindAllUsers() {
        User savedUser1 = userService.save(newUser("User1", "12345"));
        User savedUser2 = userService.save(newUser("User2", "67890"));
        User savedUser3 = userService.save(newUser("User3", "01234"));

        List<User> users = userService.findAll();
        assertTrue(users.size() >= 3);
        long countOfJustSaved = users.stream().filter(item ->
                (item.getName().equals(savedUser1.getName()) && item.getId().equals(savedUser1.getId())) ||
                        (item.getName().equals(savedUser2.getName()) && item.getId().equals(savedUser2.getId())) ||
                        (item.getName().equals(savedUser3.getName()) && item.getId().equals(savedUser3.getId()))).count();
        assertTrue(countOfJustSaved == 3);
    }

    @Test
    public void shouldFindUserById() {
        User savedUser = userService.save(newUser("Mikky Rurke", "54412345"));
        User foundUser = userService.findById(savedUser.getId());
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(savedUser.getName(), foundUser.getName());
    }

    @Test
    public void shouldFindUserByName() {
        String name = "Angelina Jolie";
        User savedUser = userService.save(newUser(name, "54412341235"));
        List<User> foundUsers = userService.findByName(name);
        assertEquals(1, foundUsers.size());
        assertEquals(savedUser.getName(), foundUsers.get(0).getName());
    }

    @Test
    public void shouldFindUserByNameIgnoringCase() {
        String name = "Matthew Perry";
        User savedUser = userService.save(newUser(name, "222333222"));
        List<User> foundUsers = userService.findByName("maTtHew PeRRy");
        assertEquals(1, foundUsers.size());
        assertEquals(savedUser.getName(), foundUsers.get(0).getName());
    }

    @Test
    public void shouldFindUserNameByFirstFewLetters() {
        String name = "Courteney Cox";
        User savedUser = userService.save(newUser(name, "111133344466"));
        List<User> foundUsers = userService.findByName("court");
        assertEquals(1, foundUsers.size());
        assertEquals(savedUser.getName(), foundUsers.get(0).getName());
    }

    @Test
    public void shouldNotFindByNameIfItNotStartedWithLetters() {
        String name = "Jennifer Aniston";
        User savedUser = userService.save(newUser(name, "09876"));
        List<User> foundUsers = userService.findByName("nife");
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    public void shouldDeleteExistedUser() {
        String name = "Met Leblanc";
        User savedUser = userService.save(newUser(name, "0987654"));
        userService.remove(savedUser.getId());
        List<User> foundUsers = userService.findByName("nife");
        assertTrue(foundUsers.isEmpty());
    }

    @Test(expected = DataAccessException.class)
    public void shouldDeleteNotExistedUser() {
        userService.remove(150l);
    }

    private User newUser(String name, String phone) {
        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        return user;
    }
}
