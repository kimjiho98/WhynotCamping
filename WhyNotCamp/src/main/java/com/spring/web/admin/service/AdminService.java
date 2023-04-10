package com.spring.web.admin.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.web.admin.vo.Admin;
import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.Vendor;

import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminService {

	@Autowired
	private AdminMapper dao;

	/** 로그인 하는 함수다
	 * @param adnum 어드민 번호
	 * @param pwd 비밀번호
	 * @return 로그인 결과 리턴
	 * */
	public boolean login(int adnum, String pwd,HttpSession session) 
	{
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		log.info("하드코딩할 pwd"+ enc.encode(pwd));
		
		//1.CAMP_USER 테이블 USERNAME,PWD,ENABLED 하드코딩
		//2.AUTHORITES 테이블 USERNAME, ATHORITY 하드코딩
		
		Admin admin = dao.getPwdByAdnum(adnum);
		boolean suc = admin.getPwd().equals(pwd);
		if(suc)
		{
			session.setAttribute("adnum", adnum);
			session.setAttribute("nickname", admin.getNickname());
		}
		return suc;
	}
	
	public User getUserDetail(String uid)
	{
		User user = dao.getUserInfo(uid);
		
		return user;
	}	
	
	public List<Booking> getUBook(String uid,Date regdate)
	{
		Map<String,Object> map = new HashMap<>();
		map.put("uid", uid);
		map.put("regdate", regdate);
		
		return dao.findBooking(map);		
	}
	
	public Vendor findVendor(String vid)
	{		
		return dao.getVendorInfo(vid);
	}
	
	public List<CampingMain> getCamp(String vid,String cname)
	{
		
		Map<String,Object> map = new HashMap<>();
		map.put("vid", vid);
		map.put("cname", cname);
		return dao.getCampByVid(map);
	}

}
