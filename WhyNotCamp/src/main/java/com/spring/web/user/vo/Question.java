package com.spring.web.user.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of="qnum")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Question {
	
	public int qnum;
	public int cnum;
	public int unum;
	public String question;
	public Date qRegDate;
	public String nickname;

}
