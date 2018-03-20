package net.stellers.sakai.resttest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SakaiTime {
	
	private String display;
	private Long time;
	
}
