package com.spring.web.login.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.Booking;

@Mapper
public interface LoginMapper {

	//id로 User객체 구하기 -지환-
	public User getUnumById(String id);
	
	//id로 Vnum구하기 -지환-
	public int getVnumById(String id);

	//adnum으로 nickname구하기 -지환-
	public String getNicknameByAdnum(int adnum);
	
	//3. 리뷰작성 할 booking 체크 - 지환
	public List<Booking> getCheckOut(int unum);	
	
	//4. booking체크 후 알람추가 기능 - 지환 
	public int addAlarmByReview(int unum);
}
