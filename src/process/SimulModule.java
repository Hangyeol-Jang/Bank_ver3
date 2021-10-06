package process;

import java.util.ArrayList;

public class SimulModule {
	private int productID;
	private int facilityID;
	private int outputQuant;
	private int quota;
	private ArrayList<Integer> inputCode;
	private ArrayList<Integer> inputQuant;
	
	public SimulModule() {
	}
	public int getQuota() {
		return quota;
	}
	public void setQuota(int quota) {
		this.quota = quota;
	}
	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public int getFacilityID() {
		return facilityID;
	}

	public void setFacilityID(int facilityID) {
		this.facilityID = facilityID;
	}

	public int getOutputQuant() {
		return outputQuant;
	}

	public void setOutputQuant(int outputQuant) {
		this.outputQuant = outputQuant;
	}

	public ArrayList<Integer> getInputCode() {
		return inputCode;
	}

	public void setInputCode(ArrayList<Integer> inputCode) {
		this.inputCode = inputCode;
	}

	public ArrayList<Integer> getInputQuant() {
		return inputQuant;
	}

	public void setInputQuant(ArrayList<Integer> inputQuant) {
		this.inputQuant = inputQuant;
	}
	
}
