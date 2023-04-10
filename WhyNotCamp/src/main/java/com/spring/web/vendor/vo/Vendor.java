package com.spring.web.vendor.vo;

import com.spring.web.user.vo.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of="vnum")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vendor {

	private int vnum;
	private String vid;
	private String pwd;
	private String license;
	private String phone;
	private String email;
	private String vname;
}
