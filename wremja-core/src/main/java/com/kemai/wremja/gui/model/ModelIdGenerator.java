package com.kemai.wremja.gui.model;

import java.util.concurrent.atomic.AtomicLong;

import com.kemai.util.IdGenerator;
import com.kemai.wremja.model.ActivityRepository;

public class ModelIdGenerator implements IdGenerator {

	private final AtomicLong sequence;

	public ModelIdGenerator(ActivityRepository data) {
		this.sequence = new AtomicLong(data.getProjectIdSequence() + 1);
	}
	
	@Override
	public long nextId() {
		return sequence.getAndIncrement();
	}

}
