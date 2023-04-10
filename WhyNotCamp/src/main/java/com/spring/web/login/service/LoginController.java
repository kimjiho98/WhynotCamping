package com.spring.web.login.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.web.user.vo.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/login")
@Controller
public class LoginController {
	
	@Autowired
	private LoginService svc;
	
	@Autowired
	private HttpSession session;
		
	
	@GetMapping("/login")
	public String login(HttpServletRequest request)
	{
		String old_url = request.getHeader("referer");
		System.out.println(old_url);
		return "thymeleaf/user/reLoginForm";
	}
	
	
	@GetMapping("/loginsuccess")
	@ResponseBody
	public Map<String,Object> success(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user,HttpSession session,Model m)
	{
		Map<String,Object> map = new HashMap<>();
		Collection<GrantedAuthority> col = user.getAuthorities();
		List<GrantedAuthority> list = new ArrayList(List.of(col.toArray()));
		String role = list.get(0).toString();
		switch (role) {
			case "ROLE_USER": {
				String uid = user.getUsername();
				User user2 = svc.getUnum(uid);
				svc.insertAlarmByReview(user2.getUnum());
				session.setAttribute("unum", user2.getUnum());
				session.setAttribute("nickname", user2.getNickname());
				map.put("confirm",true);
				log.info("여기 유저 로그인 성공시 옴");
				return map;
			}
			case "ROLE_VENDOR": {
				String vid =  user.getUsername();
				int vnum = svc.getVnum(vid);				
				session.setAttribute("vnum", vnum);
				session.setAttribute("vid", vid);
				map.put("confirm", true);
				return map;
			}
			case "ROLE_ADMIN": {
				String adnum =  user.getUsername();
				String nickname = svc.getNicknameByadmin(Integer.valueOf(adnum));
				session.setAttribute("nickname", nickname);
				map.put("confirm", true);
				return map;
			}
			
			
		}
		return map;
	}
}
