package com.andrei.ppmtool.ppmtool.Repositories;

import com.andrei.ppmtool.ppmtool.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
