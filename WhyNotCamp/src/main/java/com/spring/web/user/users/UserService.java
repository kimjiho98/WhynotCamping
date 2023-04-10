package com.spring.web.user.users;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.web.user.vo.Question;
import com.spring.web.user.vo.Review;
import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.Answer;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.CampingSite;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class UserService {	
	
	//inject - 지환
	@Autowired
	private UserMapper mapper;	

	//inject - 지환
	@Autowired
	private JavaMailSender sender;
	
	//1.유저 회원가입 -지호 
	@Transactional
	public Boolean register(User user)
	{
	     int row= mapper.register(user);
	     
	     BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
	     user.setPwd(enc.encode(user.getPwd()));
	     
	     mapper.addCampUser(user);  // camp_user에 유저 추가
	     mapper.addAuthorities(user);   // 권한 추가
	     
	     boolean success=false;
	     if(row>0)
	     {
	    	 success =true;
	     }
	     else {success=false;}
		return success;
	}
	
	//2.유저 로그인기능 -지호 
		public User login(String uuid ,String pwd)
		{
		     User user= mapper.login(uuid);
		     log.info("입력한 pwd={}",pwd);
		     log.info(user.getPwd());
		     boolean success;
		  
		    return user;
			
		}
		
	//3. 메인 해쉬태그 리스트 출력 - 소영
		public List<String> taglist()
		{
			String[] tags = {"강화도","영종도","글램핑","강릉","태안","카라반","인천","영주","계곡","서울","노지캠핑","강원도","경기도"};
			Set<String>set = new HashSet<String>();
	        int length = tags.length;
			
	        while(set.size() != 4){
	        	int rdm = (int)(Math.random()* length);
	            set.add(tags[rdm]);
	        }
	        
	        // 정렬
	        List<String> list = new ArrayList<String>(set);
	        Collections.sort(list);
	         
			return list;
		}
		//4.회원가입 아이디 중복체크 -지호
		public boolean overlapcheck(User user)
		{
			String uuid =user.getUuid();
			User users = mapper.overlapcheck(uuid);
			if(users==null)
			{
				return true;
			}
			return false;
		}
		//5.회원가입 이메일 인증 -지호
		 public boolean sendSimpleText(String email,String code)
		 {
			 MimeMessage mimeMessage = sender.createMimeMessage();
		      try 
		      {
		         InternetAddress[] addressTo = new InternetAddress[1];
		         addressTo[0] = new InternetAddress(email);
		         mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);
		         mimeMessage.setSubject("캠핑어때 이메일 인증을 해주세요");      
		         mimeMessage.setContent( "<a href='http://localhost/user/onLinks/" +code+ "'>링크를 클릭하면 이메일 인증이 완료됩니다.</a>","text/html;charset=utf-8");
		         sender.send(mimeMessage);
		         return true;
		      }
		      catch (MessagingException e) 
		      {
		         log.error("에러={}", e);
		      }
		     return false;
		 }
		
		
}
