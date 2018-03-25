package me.h3x.b0y.service;


import me.h3x.b0y.model.User;

public interface UserService {
	User findUserByEmail(String email);
	void saveUser(User user);
}
