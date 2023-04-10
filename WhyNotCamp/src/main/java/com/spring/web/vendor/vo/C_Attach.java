package com.spring.web.vendor.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="anum")
public class C_Attach 
{
	private int anum;
	private int cnum;
	private int vnum;
	private String fname;
}
