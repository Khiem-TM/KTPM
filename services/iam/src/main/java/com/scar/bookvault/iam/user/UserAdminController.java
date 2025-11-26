package com.scar.bookvault.iam.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iam/v1/users")
public class UserAdminController {
    private final UserRepository repository;

    public UserAdminController(UserRepository repository) { this.repository = repository; }

    @GetMapping
    public Page<User> list(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) { return repository.save(user); }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User incoming) {
        User u = repository.findById(id).orElseThrow();
        u.setEmail(incoming.getEmail());
        u.setRole(incoming.getRole());
        return repository.save(u);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}

