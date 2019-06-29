package ib.project.service;

import java.util.List;


import ib.project.model.User;


public interface UserService {
	User findById(Long id);
    User findByUsername(String username);
    List<User> findAll ();
}