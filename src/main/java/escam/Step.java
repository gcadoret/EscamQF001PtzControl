package escam;

public enum Step {
	Step1(1), Step2(2), Step3(3), Step4(4), Step5(5), Step6(6), Step7(7), Step8(8);
	final int value;

	private Step(int v) {
		this.value = v;// TODO Auto-generated constructor stub
	}

	public int getValue() {
		return value;
	}
}
