package com.kemai.wremja.gui.model;

import java.util.concurrent.atomic.AtomicLong;

import com.kemai.util.IdGenerator;
import com.kemai.wremja.model.ActivityRepository;

/**
 * An {@link IdGenerator} which is based on an {@link ActivityRepository}
 * i.e. it guarantees to not return ids already taken in the given {@link ActivityRepository}.
 *
 * @author kutzi
 */
public class ModelIdGenerator implements IdGenerator {

	private final AtomicLong sequence;

	public ModelIdGenerator(ActivityRepository data) {
		this.sequence = new AtomicLong(data.getProjectIdSequence() + 1);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long nextId() {
		return sequence.getAndIncrement();
	}
}
