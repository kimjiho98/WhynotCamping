package com.spring.web.user.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.web.user.vo.Review;
import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.CampingMain;

import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/user")
@Controller
public class UserController {


    @Autowired
	private UserService svc;

    @Autowired
	private HttpSession session;
	
    @GetMapping("/main")//유저메인홈페이지 -지호
	public String userMain(Model m)
	{		
    	m.addAttribute("list",svc.taglist());	// main에 hashtag 보여주는 기능 - 소영
		return "thymeleaf/user/userMain";
	}
    
    @GetMapping("/register")  //유저 회원가입폼 -지호
    public String register()
    {
    	return "thymeleaf/user/userRegister";
    }
    @GetMapping("/loginpage")  //유저 로그인폼 -지호
    public String loginpage()
    {
    	return "thymeleaf/user/userLogin";
    }
    @PostMapping("/overlapcheck")//회원가입 아이디 중복체크 --지호
    @ResponseBody
    public Map<String,Boolean> overlapcheck(User user)
    {
    	Map<String,Boolean> map =new HashMap<>();
    	boolean checked=svc.overlapcheck(user);
    	map.put("checked", checked);
    	return map;
    }
    
    @PostMapping("/reg_emailcheck") //메일보내기 --지호
    @ResponseBody
	public Map<String,Object> reg_emailcheck(String email)
	{
		UUID randomUUID = UUID.randomUUID();
	    String mcode = randomUUID.toString().replaceAll("-", "");
		
	    session.setAttribute("mcode", mcode);	    
	    
		boolean isSent = svc.sendSimpleText(email,mcode);
	    String msg = isSent ? "인증 메일이 전송되었습니다." : "인증메일 전송에 실패하였습니다.";
	    Map<String,Object> map = new HashMap<>();
	      
	    map.put("isSent", isSent);
	    map.put("msg", msg);	      
	    
		return map;
	}
    @GetMapping("/onLinks/{code}")  // 매일 인증 -- 지호
    @ResponseBody
    public String codeCheck(@PathVariable("code") String code, Model m)
    {
      if(code.equals((String)session.getAttribute("mcode")))
      {
    	  return "이메일 인증이 완료되었씁니다";
      }     
      else return "이메일 인증 실패";          
    }

    @PostMapping("/register")//유저 로그인 기능-지호
    @ResponseBody
    public Map<String,Boolean> Register_Function(User user)
    {
    	Map<String,Boolean> map=new HashMap<>();
    	boolean checked=svc.register(user);    	
        map.put("checked",checked );
    	return map;
    }
    
    @PostMapping("/login")//유저 로그인 기능-지호
    @ResponseBody
    public Map<String,Boolean> Login_Function(@RequestParam("uuid")String uuid,@RequestParam("pwd")String pwd)                                                     
    {    	                            
    	log.info("adsaqweqwe");
    	Map<String,Boolean> map=new HashMap<>();
    	User user =svc.login(uuid, pwd);
        if(user!=null)
        {
        	map.put("logined",true);
        	session.setAttribute("unum", user.getUnum());
        	session.setAttribute("nickname", user.getNickname());
        	return map;
        }
        else  
        {
        	map.put("logined", false);
            return map;	
        }    	
    }
    @GetMapping("/logout")//유저 로그아웃 기능 -지호
    public String logout()
    {
    	session.invalidate();
    	return "thymeleaf/user/userMain";
    }
    
}
