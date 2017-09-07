package demo.simplerest.controllers;

import demo.simplerest.entities.User;
import demo.simplerest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ResourceNotFoundException;
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
    public void create(@RequestBody User user) {// todo: handle wrong data (don't have user or phone duplicated)
        userService.save(user);
    }

    @PutMapping
    @ResponseStatus(value = HttpStatus.OK)// todo: handle situation when user doesn't exist
    public void update(@RequestBody User user) {// todo: handle wrong data (don't have user or phone duplicated)
        userService.update(user);
    }


    @DeleteMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable("id") long id) {
        userService.remove(id);// todo: need to return HttpStatus.NOT_FOUND if user not found
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleResourceNotFoundException(ResourceNotFoundException ex)// todo: implement it
    {
        return ex.getMessage();
    }
}
