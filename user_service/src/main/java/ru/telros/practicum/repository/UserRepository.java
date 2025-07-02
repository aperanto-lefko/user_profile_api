package ru.telros.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.telros.practicum.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
