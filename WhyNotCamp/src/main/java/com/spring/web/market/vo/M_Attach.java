package com.spring.web.market.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class M_Attach {
	private int mnum;
	private int unum; // 데이터 쿼리 비교용 
	private int manum;
	private String fname;
}
