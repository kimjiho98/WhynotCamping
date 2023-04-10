package com.spring.web.admin.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.Vendor;

import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin")
@Controller
public class AdminController {


	@Autowired
	private AdminService svc;
	
	@Autowired
	private HttpSession session;
	
	
	@GetMapping("/main")
	private String adminMain()
	{
	
		return "thymeleaf/admin/main";
	}
	
	@GetMapping("/login")
	private String loginForm()
	{
		return "thymeleaf/admin/loginForm";
	}
	
	@GetMapping("/getUserInfo")
	private String getUser(String uid,Model m)
	{
		User user = svc.getUserDetail(uid);
		m.addAttribute("user",user);		
		return "thymeleaf/admin/getUserDetail";
	}
	
	@GetMapping("/getUserBook")
	private String getUserBook(String uid,Date regdate,Model m)
	{
		List<Booking> book = svc.getUBook(uid,regdate);
		m.addAttribute("book",book);
		return "thymeleaf/admin/getUserBooking";
	}
	
	@GetMapping("/getVendorInfo")
	private String getVendorInfo(String vid,Model m )
	{
		Vendor vendor = svc.findVendor(vid);		
		m.addAttribute("vendor",vendor);
		return "thymeleaf/admin/getVendorDetail";
	}
	
	@GetMapping("/getCampInfo")
	private String getCampInfo(String vid,String cname,Model m)
	{
		List<CampingMain> camp = svc.getCamp(vid,cname);
 		m.addAttribute("camp",camp);
		return "thymeleaf/admin/getCampDetail";
	}
	
	
	@GetMapping("/adminchat")
	public String admin()
	{
		return "thymeleaf/admin/admin";
	}	
	
	
}
