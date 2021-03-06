package demo.simplerest.controllers;

import demo.simplerest.InvalidDataException;
import demo.simplerest.entities.User;
import demo.simplerest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping()
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable long id) {
        User user = userService.findById(id);
        return user == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public List<User> findByName(@PathVariable String name) {
        return userService.findByName(name);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<User> create(@RequestBody User user) throws InvalidDataException {
        return new ResponseEntity<>(userService.create(user), HttpStatus.OK);
    }

    @PutMapping
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<User> update(@RequestBody User user) throws InvalidDataException {
        if (user.getId() == null) {
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
        User userToUpdate = userService.findById(user.getId());
        if (userToUpdate != null) {
            return new ResponseEntity<>(userService.update(user), HttpStatus.OK);
        }
        return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
    }


    @DeleteMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<User> delete(@PathVariable("id") long id) {
        User userToDelete = userService.findById(id);
        if (userToDelete != null) {
            userService.remove(id);
        }
        return new ResponseEntity<>(userToDelete, userToDelete == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleResourceNotFoundException(InvalidDataException ex) {
        return ex.getMessage();
    }
}
