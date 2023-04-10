package com.spring.web.vendor.vo;

import java.sql.Date;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of="bnum")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {

	private int bnum;
	private Date regDate;
	private Date checkin;
	private Date checkout;
	private int unum;
	private int ppl;
	private int ttlprice;
	private int snum;
	private String sname;
	private int cnum;
	private int confirm;
	private String name;
	private String phone;
	private String email;
}
