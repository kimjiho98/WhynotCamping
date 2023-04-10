package com.spring.web.vendor.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.spring.web.user.vo.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of="cnum")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CampingMain {


	private int cnum;
	private int vnum;
	private String cname;
	private String address;
	private String phone;
	private String info;
	private int in_time;
	private int out_time;
	private double lat;
	private double lng;

	private List<CampingSite> siteList = new ArrayList<>();
	private List<C_Attach> attList = new ArrayList<>();
	private List<Review> revList = new ArrayList<>();
}
