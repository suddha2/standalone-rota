package com.midco.rota;

public class IdealShiftCount {
	private final int idealCount;

	public IdealShiftCount(int idealCount) {
		this.idealCount = idealCount;
	}

	public int getIdealCount() {
		return idealCount;
	}

	@Override
	public String toString() {
		return "IdealShiftCount(" + idealCount + ")";
	}
}
