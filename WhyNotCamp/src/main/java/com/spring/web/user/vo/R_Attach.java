package com.spring.web.user.vo;

import com.spring.web.vendor.vo.C_Attach;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude= {"unum","rnum","cnum","bnum","fname"})
public class R_Attach 
{
	private int ranum;
	private int unum;
	private int rnum;
	private int cnum;
	private int bnum;
	private String fname;
}