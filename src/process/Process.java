package process;

import java.util.ArrayList;
import java.util.Map;

import dao.SimulDAO;

/**
 * @summary �Է� ���� �ڵ��� ��ǰ�� �����ϱ� ���� ��� ������ ���� ������ ����, �ùķ��̼��ϴ� ���
 * @author hangeyol
 *
 */
public class Process {
	private ArrayList<SimulModule> simulList; // �ùĿ� �ʿ��� ��ǰ �������� ��� ����Ʈ
	private boolean isInit;	// execute���� ó�� �ѹ��� DB�� �ʱ�ȭ�ϱ� ���� �ʿ��� �÷���
	private boolean complete;	// �Ҵ緮��ŭ ������ �Ϸ�Ǿ��� ��, �� �̻� �����۾��� ������� �ʵ��� ���µ� ���
	private int simulCode;	// �ùķ��̼��� DB�� �����ϱ� ���� �ڵ� 
	private int timeStep;
	private int productCode;
	
	// �ʱ� ����
	public Process(int productCode, int quota) {
		isInit = true;
		complete = false;
		this.productCode = productCode;
		simulList = new ArrayList<SimulModule>();
		this.genSimulList(productCode, quota);
		this.getSimulCode(productCode, quota);
	}
	// HTML ���Ϸ� ��� ���
	public void resultHTML() {
		WriteHTML result = new WriteHTML(timeStep, simulList, simulCode);
		String filePath = result.write();
		this.treePrinter("",false,true,productCode);
		System.out.println("<<������� : ���������� �������ּ���>>");
		System.out.println(filePath);
	}
	private void treePrinter(String indent, boolean last, boolean first, int invenCode) {
		ArrayList<Integer> list = SimulDAO.getInstance().getChild(invenCode);
		if(last) {
			System.out.println(indent+"��");
			System.out.print(indent+"����������");
			indent +="     ";
		}else if(first){
			System.out.print("  ");
			indent +="  ";
		}else {
			System.out.println(indent+"��");
			System.out.print(indent+"����������");
			indent +="��    ";
		}
		
		System.out.println(			"��������������������");
		System.out.print(indent+	"��code."+invenCode);
		if((invenCode+"").length()==1) System.out.println("  ��");
		else if((invenCode+"").length()==2) System.out.println(" ��");
		else if((invenCode+"").length()==3) System.out.println(" ��");
		System.out.println(indent+	"��������������������");
		for(int i=0;i<list.size();i++)
			this.treePrinter(indent, i==list.size()-1, false, list.get(i));
	}
	// input ������ ���Է���ŭ ��� Ȯ���ϰ� �ִ��� Ȯ���ϴ� �޼���
	// ������ Ȯ������ �ʴ´�(����� ���� �������� ����Ѵ�).
	private boolean checkProcess(SimulModule bean) {
		ArrayList<Integer> inputCode = bean.getInputCode();
		ArrayList<Integer> inputQuant = bean.getInputQuant();
		Map<Integer, Integer> checkMap = SimulDAO.getInstance().getInventoryDict(inputCode);
		for(int i=0;i<inputCode.size();i++) {
			// üũ�� ��� ��� Ȯ��
			if(checkMap.containsKey(inputCode.get(i))) {
				// �ʿ��� ���Է����� ��� �����ϸ� false(����Ұ�) ��ȯ
				if(checkMap.get(inputCode.get(i))<inputQuant.get(i)) {
					return false;
				}
			}
		}
		return true;
	}
	// �ش� bean�� ��� ������Ʈ�Ѵ�(��� ��ǰ�� +, ���� -, �Ҵ緮�� -)
	private void exeBean(SimulModule bean) {
		SimulDAO dao = SimulDAO.getInstance();
		// �Ҵ緮�� ���귮��ŭ ����
		bean.setQuota(bean.getQuota()-bean.getOutputQuant());
		// ��� ��ǰ�� DB ��� ���귮��ŭ ����
		dao.updateStock(bean.getProductID(), bean.getOutputQuant());
		// ���� ��ǰ�� DB ��� ���Է���ŭ ����
		for(int i=0;i<bean.getInputCode().size();i++) {
			dao.updateStock(bean.getInputCode().get(i), -bean.getInputQuant().get(i));
		}
	}
	// �ùķ��̼��� 1ȸ �����Ѵ�.
	public boolean execute() {
		// �ش� Ÿ�Խ��ܿ��� ���� �۵��ߴ��� ���
		ArrayList<Integer> facilExe = new ArrayList<Integer>();
		// �ùķ��̼��� ����Ǹ�(��� �Ҵ緮��ŭ ������ �Ǹ�) ���̻� ���� �Ұ�
		// simulCode�� ����� �������� ���� ������ ������ ���� �ʴ´�.
		if(complete || simulCode ==0) return complete;
		
		if(isInit) {
			// ó�� �������� ��� timeStep=0, ��� ��� 0���� ����
			// simul_facility�� simul_inventory�� ���
			timeStep = 0;
			isInit=false;
			SimulDAO.getInstance().resetStock(simulList);
		}else {
			timeStep++;
			// simulList�� �ϳ��� �����ؼ� ���DB����
			for(SimulModule bean : simulList) {
				// ����ᰡ �ƴ� ���, �ش� ����� �Ҵ緮�� >0 �� ���
				// �� ����� ���� ����� ��� ���� ��� ���� ����
				if(bean.getFacilityID()>0 && bean.getQuota()>0 && this.checkProcess(bean)) {
					facilExe.add(1);
					// ��� update
					this.exeBean(bean);
				}else {
					facilExe.add(0);
				}}}
		// �ù� insert�� �����̴�(ó���̵�, �߰��̵�)
		for(int i=0;i<simulList.size();i++) {
			// ��� ���
			SimulModule bean = simulList.get(i);
			SimulDAO.getInstance().insertSimulInventory(bean.getProductID(), simulCode, timeStep);
			// ���� �۵� ���θ� ���(������ ������� �ʴ´�)
			if(bean.getFacilityID()>0) {
				if(facilExe.size()>0) 
					SimulDAO.getInstance().insertSimulFacility(bean.getFacilityID(), simulCode, timeStep, facilExe.get(i));
				// ó�� ������ ��(step=0)�� ���� ���� �۵����� �����Ƿ� ��� 0���� ����
				else
					SimulDAO.getInstance().insertSimulFacility(bean.getFacilityID(), simulCode, timeStep, 0);
			}
		}
		// ���� �Ҵ緮 Ȯ��, �� �̻� ������ �Ϸ�=true
		if(simulList.get(0).getQuota()<=0) complete=true;
		
		return complete;
	}
	
	// �ù� �ʱ� ���� + ���� ����
	private void getSimulCode(int productCode, int quota) {
		simulCode = SimulDAO.getInstance().getMaxSimulCode();
		if(simulCode!=0) SimulDAO.getInstance().insertSimulInfo(simulCode, productCode, quota);
		else complete = true; // �ùķ��̼��� ������� �ʵ��� �����Ѵ�.
	}
	// ��ǰ�� ����Ʈ�� �ִ´�(��ǰ�� ���� ��� ���� �ʴ´�)
	private void genSimulList(int productCode, int quota) {
		// ��� ����
		SimulModule bean = new SimulModule();
		
		// 1.���̵� ����
		bean.setProductID(productCode);

		// ���� �ڵ带 �����´�. ����� ��� 0, ��ǰ ��ü�� ���� ��� -1
		int facilityCode = SimulDAO.getInstance().getFromInventory(productCode);
		if(facilityCode == -1) return;
		// 2.���� ����
		bean.setFacilityID(facilityCode);
		if(facilityCode==0) {
			// ����� ���
			this.simulList.add(bean);
			return;
		}
		// 3.���귮 ����
		int outputQuant = SimulDAO.getInstance().getFromFacility(facilityCode); 
		if(outputQuant < 1) bean.setOutputQuant(0);
		else				bean.setOutputQuant(outputQuant);
		// 4.���� ����� �ڵ� + ����
		ArrayList<Integer> inputCode = new ArrayList<Integer>();
		ArrayList<Integer> inputQuant = new ArrayList<Integer>();
		SimulDAO.getInstance().getFromInput(facilityCode, inputCode, inputQuant);
		bean.setInputCode(inputCode);
		bean.setInputQuant(inputQuant);
		// 5.���귮 �Ҵ�
		if(quota<0) quota = 0;
		bean.setQuota(quota);
		int cycle;
		if(outputQuant>0) cycle = (int)Math.ceil(quota / (double)outputQuant );
		else cycle = 0;
		
		// ����� ����Ʈ�� ����
		this.simulList.add(bean);
		for(int i=0; i<inputCode.size();i++) {
			this.genSimulList(inputCode.get(i),inputQuant.get(i)*cycle);
		}
	}
}