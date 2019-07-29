package it.eng.care.databaser.applyer;

import org.springframework.stereotype.Component;

import it.fmoon.fxapp.mvc.ActivityDef;

@Component
public class ApplyerActivityDef extends ActivityDef<ApplyerActivity> {

	public ApplyerActivityDef() {
		super(ApplyerActivity.class);
	}

	@Override
	public String getName() {
		return "ApplyerActivity";
	}

}
