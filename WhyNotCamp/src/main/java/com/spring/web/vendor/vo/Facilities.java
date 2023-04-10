package com.spring.web.vendor.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"cnum"})
public class Facilities {

	public int cnum;
	public int pet;
	public int cook;
	public int bbq;
	public int fire;
	public int wifi;
	public int toilet;
	
}
