package com.spring.web.login.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.web.user.vo.User;
import com.spring.web.vendor.service.VendorService;
import com.spring.web.vendor.vo.Booking;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginService {
	
	@Autowired
	private LoginMapper mapper;
	
	
	//id로 User객체 구하기 -지환-
	public User getUnum(String id)
	{
		return mapper.getUnumById(id);
	}
	
	//id로 Vnum구하기 -지환-
	public int getVnum(String id)
	{
		int vnum = mapper.getVnumById(id);

		return vnum;
	}	
	
	public String getNicknameByadmin(int adnum)
	{
		String nickname = mapper.getNicknameByAdnum(adnum);		
		return nickname;
	}
	
	//. 리뷰체크 후 알람추가 - 지환 
	public void insertAlarmByReview(int unum)
	{
		log.info("userService1 : " + unum);
		List<Booking> list = mapper.getCheckOut(unum);
		
		log.info("userService2 : " + list);
		
		for(int i=0;i<list.size();i++)
		{
			Booking b = list.get(i);					
			if(b!=null)
			{
				mapper.addAlarmByReview(unum);
			}			
		}				
	}
			
	
	
	
}
