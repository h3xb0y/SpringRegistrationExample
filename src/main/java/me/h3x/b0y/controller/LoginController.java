package me.h3x.b0y.controller;

import javax.validation.Valid;


import me.h3x.b0y.model.TableUser;
import me.h3x.b0y.model.User;
import me.h3x.b0y.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.sql.*;
import java.util.ArrayList;

@Controller
public class LoginController {
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.password}")
	private String password;
	@Value("${spring.queries.view-users}")
    private String query;

	@Autowired
	private UserService userService;

	@RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	
	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView registration(){
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult
					.rejectValue("email", "error.user","*There is already a user registered with the email provided");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User has been registered successfully" +
					"<i class=\"em em-white_check_mark\"></i>\n");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");
			
		}

		return modelAndView;
	}
	
	@RequestMapping(value="/admin/home", method = RequestMethod.GET)
	public ModelAndView home(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@RequestMapping(value="/admin/database", method = RequestMethod.GET)
	public ModelAndView database() throws SQLException {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("admin/database");
		Connection con = DriverManager.getConnection(url, username, password);
		Statement stmt = con.createStatement();

		ArrayList<TableUser> list = new ArrayList<TableUser>();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next())
		{
				TableUser user = new TableUser();
				user.setId(rs.getString("user_id"));
				user.setActive(rs.getString("active"));
				user.setEmail(rs.getString("email"));
				user.setLastName(rs.getString("last_name"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
				list.add(user);
		}
		modelAndView.addObject("users",list);

		return modelAndView;
	}


}
