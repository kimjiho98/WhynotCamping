package com.spring.web.admin.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.admin.vo.Admin;
import com.spring.web.user.vo.User;
import com.spring.web.vendor.vo.Booking;
import com.spring.web.vendor.vo.CampingMain;
import com.spring.web.vendor.vo.Vendor;

@Mapper
public interface AdminMapper {

	public Admin getPwdByAdnum(int adnum);
	
	public User getUserInfo(String uid);
	
	public List<Booking> findBooking(Map map);
	
	public Vendor getVendorInfo(String vid);
	
	public List<CampingMain> getCampByVid(Map map);
}
