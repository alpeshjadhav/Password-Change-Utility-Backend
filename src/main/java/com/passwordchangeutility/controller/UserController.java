package com.passwordchangeutility.controller;

import com.passwordchangeutility.model.User;
import com.passwordchangeutility.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Map<String, String> otpMap = new HashMap<>();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        try {
            User user = userService.getUserById(id);
            return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/generateOTP")
    public ResponseEntity<String> generateOTP(@RequestParam String username) {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);

        // Store OTP in map against user ID
        otpMap.put(username, String.valueOf(otp));

        // Return the OTP
        return ResponseEntity.ok (String.valueOf(otp));
    }

    @PostMapping("/validateOTP")
    public ResponseEntity<String> validateOTP(@RequestParam String username, @RequestParam String otp) {
        // Check if OTP exists for the given user ID
        if (!otpMap.containsKey(username)) {
            return ResponseEntity.badRequest().body("No OTP found for the provided user ID");
        }

        // Get the OTP from map
        String storedOTP = otpMap.get(username);

        // Check if provided OTP matches the stored OTP
        if (otp.equals(storedOTP)) {
            // Clear the OTP from the map after successful validation
            otpMap.remove(username);
            return ResponseEntity.ok("OTP validated successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }

    @PutMapping("/{id}/updatePassword")
    public ResponseEntity<String> updatePassword(@PathVariable String id, @RequestParam String newPassword) {
        try {
            userService.updatePassword(id, newPassword);
            return ResponseEntity.ok("Password updated successfully");
        } catch (UserPrincipalNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
