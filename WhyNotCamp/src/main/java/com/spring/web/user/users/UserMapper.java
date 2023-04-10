package com.spring.web.user.users;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.user.vo.Review;
import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.Vendor;



@Mapper
public interface UserMapper {
	
	//1. 회원가입기능 --지호
	public int register(User user);
	
	//2. 로그인기능 --지호
	public User login(String uuid);
	//3.회원가입 아이디 중복체크 --지호
	public User overlapcheck(String uuid);
	// Camp_User에 암호화된 정보 저장 - 지환-
	public int addCampUser(User user);
	// 벤더 권한 부여 -지환- 
	public int addAuthorities(User user);
	
}
