package com.spring.web.vendor.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Answer {

	private int asnum;
	private int qnum;
	private int cnum;
	private String answer;
	private Date aRegDate;
	
}
