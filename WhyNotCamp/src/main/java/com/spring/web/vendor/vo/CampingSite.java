package com.spring.web.vendor.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="snum")

public class CampingSite 
{
	private int snum;
	private String sname;
	private int cnum;
	private int bookable;
	private int max_ppl;
	private int price;
	private int vnum;
}