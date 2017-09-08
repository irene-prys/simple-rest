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
    public void shouldCreateUser() throws InvalidDataException {
        User user = newUser("Jane", "123");
        User savedUser = userService.create(user);
        assertNotNull(savedUser.getId());
    }

    @Test
    public void shouldCreateUserIgnoringPresetId() throws InvalidDataException {
        Long presetId = 100l;
        User user = newUser("Helen", "3222");
        user.setId(presetId);
        User savedUser = userService.create(user);
        assertNotNull(savedUser.getId());
        assertNotEquals(presetId, savedUser.getId());
    }

    @Test
    public void shouldCreateNewUserIfIdOfExistUserPassed() throws InvalidDataException {
        User user1 = newUser("Helen", "3224672");
        User savedUser = userService.create(user1);

        User user2 = newUser("Anna", "32253242");
        user2.setId(savedUser.getId());
        User savedUser2 = userService.create(user2);

        assertNotNull(savedUser.getId());
        assertNotEquals(savedUser2.getId(), savedUser.getId());
    }

    @Test
    public void shouldCreateUserWithTheSameName() throws InvalidDataException {
        User user1 = newUser("Thomas", "3456789");
        User user2 = newUser("Thomas", "456");

        User savedUser1 = userService.create(user1);
        User savedUser2 = userService.create(user2);
        assertNotNull(savedUser1.getId());
        assertNotNull(savedUser2.getId());
        assertNotEquals(savedUser1.getId(), savedUser2.getId());
    }

    @Test
    public void shouldUpdateUser() throws InvalidDataException {
        String initialName = "Lily";
        User user = newUser(initialName, "54321");
        User savedUser = userService.create(user);
        savedUser.setName("Lilly");
        User updatedUser = userService.update(user);

        assertNotNull(updatedUser.getId());
        assertNotNull(updatedUser.getName());
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertNotEquals(initialName, updatedUser.getName());
    }

    @Test(expected = InvalidDataException.class)
    public void shouldNotAllowCreateUserWithDuplicatedPhones() throws InvalidDataException {
        final String phone = "2233";
        User user1 = newUser("Ben Affleck", phone);
        User user2 = newUser("Jennifer Lopez", phone);

        userService.create(user1);
        userService.create(user2);
    }

    @Test
    public void shouldCreateUserWithoutPhone() throws InvalidDataException {
        User user1 = newUser("Mary", null);
        assertNotNull(userService.create(user1).getId());
    }

    @Test(expected = InvalidDataException.class)
    public void shouldThrowExceptionIfUserWithSuchPhoneExists() throws InvalidDataException {
        User user1 = newUser("John Doe", "1234567890");
        User user2 = newUser("Jane Doe", "1234567890");
        userService.create(user1);
        userService.create(user2);
    }

    @Test
    public void shouldCreateSeveralUsersWithoutPhone() throws InvalidDataException {
        User user1 = newUser("Benny", null);
        User user2 = newUser("Willy", null);

        User savedUser1 = userService.create(user1);
        User savedUser2 = userService.create(user2);

        assertNotNull(savedUser1.getId());
        assertNotNull(savedUser2.getId());
        assertNotEquals(savedUser1.getId(), savedUser2.getId());
    }

    @Test
    public void shouldFindAllUsers() throws InvalidDataException {
        User savedUser1 = userService.create(newUser("User1", "12345"));
        User savedUser2 = userService.create(newUser("User2", "67890"));
        User savedUser3 = userService.create(newUser("User3", "01234"));

        List<User> users = userService.findAll();
        assertTrue(users.size() >= 3);
        long countOfJustSaved = users.stream().filter(item ->
                (item.getName().equals(savedUser1.getName()) && item.getId().equals(savedUser1.getId())) ||
                        (item.getName().equals(savedUser2.getName()) && item.getId().equals(savedUser2.getId())) ||
                        (item.getName().equals(savedUser3.getName()) && item.getId().equals(savedUser3.getId()))).count();
        assertTrue(countOfJustSaved == 3);
    }

    @Test
    public void shouldFindUserById() throws InvalidDataException {
        User savedUser = userService.create(newUser("Mikky Rurke", "54412345"));
        User foundUser = userService.findById(savedUser.getId());
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(savedUser.getName(), foundUser.getName());
    }

    @Test
    public void shouldFindUserByName() throws InvalidDataException {
        String name = "Angelina Jolie";
        User savedUser = userService.create(newUser(name, "54412341235"));
        List<User> foundUsers = userService.findByName(name);
        assertEquals(1, foundUsers.size());
        assertEquals(savedUser.getName(), foundUsers.get(0).getName());
    }

    @Test
    public void shouldFindUserByNameIgnoringCase() throws InvalidDataException {
        String name = "Matthew Perry";
        User savedUser = userService.create(newUser(name, "222333222"));
        List<User> foundUsers = userService.findByName("maTtHew PeRRy");
        assertEquals(1, foundUsers.size());
        assertEquals(savedUser.getName(), foundUsers.get(0).getName());
    }

    @Test
    public void shouldFindUserNameByFirstFewLetters() throws InvalidDataException {
        String name = "Courteney Cox";
        User savedUser = userService.create(newUser(name, "111133344466"));
        List<User> foundUsers = userService.findByName("court");
        assertEquals(1, foundUsers.size());
        assertEquals(savedUser.getName(), foundUsers.get(0).getName());
    }

    @Test
    public void shouldNotFindByNameIfItNotStartedWithLetters() throws InvalidDataException {
        String name = "Jennifer Aniston";
        User savedUser = userService.create(newUser(name, "09876"));
        List<User> foundUsers = userService.findByName("nife");
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    public void shouldDeleteExistedUser() throws InvalidDataException {
        String name = "Met Leblanc";
        User savedUser = userService.create(newUser(name, "0987654"));
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
