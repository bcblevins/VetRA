package com.bcb.vetra.controllers;

import com.bcb.vetra.daos.UserDao;
import com.bcb.vetra.models.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
/**
 * <strong>Controller for users.</strong>
 * <br><br>
 * This class is responsible for handling all HTTP requests related to users.
 */

@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {
    private UserDao userDao;
    public UserController(UserDao userDao) {this.userDao = userDao;}

    /**
     * Gets all users.
     *
     * @return A list of all users.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    @GetMapping(path = "/users")
    public List<User> getAll() {
        return userDao.getUsers();
    }

    /**
     * Gets a user by their username.
     *
     * @param username The username of the user.
     * @return The user with the given username.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    @GetMapping(path = "/users/{username}")
    public User get(@PathVariable String username) {
        return userDao.getUserByUsername(username);
    }

    /**
     * Creates a new user.
     *
     * @param user The user to create.
     * @return The created user.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/users")
    public User create(@Valid @RequestBody User user) {
        return userDao.createUser(user);
    }

    /**
     * Updates a user.
     *
     * @param user The user to update.
     * @param username The username of the user to update.
     * @return The updated user.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(path = "/users/{username}")
    public User update(@Valid @RequestBody User user, @PathVariable String username) {
        user.setUsername(username);
        return userDao.updateUser(user);
    }

    /**
     * Updates a specific user's password.
     *
     * @param password The new password.
     * @param username The username of the user.
     * @return The updated user.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(path = "users/{username}/password")
    public User updatePassword(@RequestBody String password, @PathVariable String username) {
        User user = userDao.getUserByUsername(username);
        user.setPassword(password);
        return userDao.updatePassword(user);
    }

    /**
     * Updates the currently logged in user's password.
     *
     * @param password
     * @param principal
     * @return
     */
    @PutMapping(path = "/password")
    public User updatePassword(@RequestBody String password, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        user.setPassword(password);
        return userDao.updatePassword(user);
    }

    /**
     * Deletes a user.
     *
     * @param username The username of the user to delete.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "users/{username}")
    public void delete(@PathVariable String username) {
        userDao.deleteUser(username);
    }

    //------------------------
    // Roles
    //------------------------

    /**
     * Gets all roles for a user.
     *
     * @param username The username of the user.
     * @return A list of all roles for the user.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    @GetMapping(path = "users/{username}/roles")
    public List<String> getRoles(@PathVariable String username) {
        return userDao.getRoles(username);
    }

    /**
     * Adds a role to a user.
     *
     * @param username The username of the user.
     * @param role The role to add.
     * @return A list of all roles for the user.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path = "users/{username}/roles")
    public List<String> addRole(@PathVariable String username, @RequestBody String role) {
        return userDao.addRole(username, role.toUpperCase());
    }

    /**
     * Deletes a role from a user.
     *
     * @param username The username of the user.
     * @param role The role to delete.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "users/{username}/roles/{role}")
    public void deleteRole(@PathVariable String username, @PathVariable String role) {
        userDao.deleteRole(username, role.toUpperCase());
    }

}
