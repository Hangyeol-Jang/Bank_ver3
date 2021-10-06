package process;

import java.util.ArrayList;
import java.util.Map;

import dao.SimulDAO;

/**
 * @summary 입력 받은 코드의 제품을 생산하기 위한 모든 공정에 대한 정보를 저장, 시뮬레이션하는 모듈
 * @author hangeyol
 *
 */
public class Process {
	private ArrayList<SimulModule> simulList; // 시뮬에 필요한 상품 정보들을 담는 리스트
	private boolean isInit;	// execute에서 처음 한번은 DB를 초기화하기 위해 필요한 플래그
	private boolean complete;	// 할당량만큼 생산이 완료되었을 때, 더 이상 생산작업이 진행되지 않도록 막는데 사용
	private int simulCode;	// 시뮬레이션을 DB에 저장하기 위한 코드 
	private int timeStep;
	private int productCode;
	
	// 초기 설정
	public Process(int productCode, int quota) {
		isInit = true;
		complete = false;
		this.productCode = productCode;
		simulList = new ArrayList<SimulModule>();
		this.genSimulList(productCode, quota);
		this.getSimulCode(productCode, quota);
	}
	// HTML 파일로 결과 출력
	public void resultHTML() {
		WriteHTML result = new WriteHTML(timeStep, simulList, simulCode);
		String filePath = result.write();
		this.treePrinter("",false,true,productCode);
		System.out.println("<<결과파일 : 브라우저에서 실행해주세요>>");
		System.out.println(filePath);
	}
	private void treePrinter(String indent, boolean last, boolean first, int invenCode) {
		ArrayList<Integer> list = SimulDAO.getInstance().getChild(invenCode);
		if(last) {
			System.out.println(indent+"│");
			System.out.print(indent+"└────");
			indent +="     ";
		}else if(first){
			System.out.print("  ");
			indent +="  ";
		}else {
			System.out.println(indent+"│");
			System.out.print(indent+"├────");
			indent +="│    ";
		}
		
		System.out.println(			"┼────────┼");
		System.out.print(indent+	"│code."+invenCode);
		if((invenCode+"").length()==1) System.out.println("  │");
		else if((invenCode+"").length()==2) System.out.println(" │");
		else if((invenCode+"").length()==3) System.out.println(" │");
		System.out.println(indent+	"┼────────┼");
		for(int i=0;i<list.size();i++)
			this.treePrinter(indent, i==list.size()-1, false, list.get(i));
	}
	// input 재료들이 투입량만큼 재고를 확보하고 있는지 확인하는 메서드
	// 원재료는 확인하지 않는다(원재료 재고는 음수값을 허용한다).
	private boolean checkProcess(SimulModule bean) {
		ArrayList<Integer> inputCode = bean.getInputCode();
		ArrayList<Integer> inputQuant = bean.getInputQuant();
		Map<Integer, Integer> checkMap = SimulDAO.getInstance().getInventoryDict(inputCode);
		for(int i=0;i<inputCode.size();i++) {
			// 체크할 대상만 재고를 확인
			if(checkMap.containsKey(inputCode.get(i))) {
				// 필요한 투입량보다 재고가 부족하면 false(생산불가) 반환
				if(checkMap.get(inputCode.get(i))<inputQuant.get(i)) {
					return false;
				}
			}
		}
		return true;
	}
	// 해당 bean의 재고를 업데이트한다(대상 상품은 +, 재료는 -, 할당량은 -)
	private void exeBean(SimulModule bean) {
		SimulDAO dao = SimulDAO.getInstance();
		// 할당량을 생산량만큼 감소
		bean.setQuota(bean.getQuota()-bean.getOutputQuant());
		// 대상 상품의 DB 재고를 생산량만큼 증가
		dao.updateStock(bean.getProductID(), bean.getOutputQuant());
		// 투입 상품의 DB 재고를 투입량만큼 감소
		for(int i=0;i<bean.getInputCode().size();i++) {
			dao.updateStock(bean.getInputCode().get(i), -bean.getInputQuant().get(i));
		}
	}
	// 시뮬레이션을 1회 실행한다.
	public boolean execute() {
		// 해당 타입스텝에서 설비가 작동했는지 기록
		ArrayList<Integer> facilExe = new ArrayList<Integer>();
		// 시뮬레이션이 종료되면(모든 할당량만큼 실행이 되면) 더이상 진행 불가
		// simulCode가 제대로 생성되지 않을 때에도 실행을 하지 않는다.
		if(complete || simulCode ==0) return complete;
		
		if(isInit) {
			// 처음 실행했을 경우 timeStep=0, 모든 재고 0으로 설정
			// simul_facility와 simul_inventory에 기록
			timeStep = 0;
			isInit=false;
			SimulDAO.getInstance().resetStock(simulList);
		}else {
			timeStep++;
			// simulList에 하나씩 접근해서 재고DB수정
			for(SimulModule bean : simulList) {
				// 원재료가 아닐 경우, 해당 모듈의 할당량이 >0 일 경우
				// 각 모듈의 투입 재료의 재고가 있을 경우 생산 실행
				if(bean.getFacilityID()>0 && bean.getQuota()>0 && this.checkProcess(bean)) {
					facilExe.add(1);
					// 재고 update
					this.exeBean(bean);
				}else {
					facilExe.add(0);
				}}}
		// 시뮬 insert는 공통이다(처음이든, 중간이든)
		for(int i=0;i<simulList.size();i++) {
			// 재고를 기록
			SimulModule bean = simulList.get(i);
			SimulDAO.getInstance().insertSimulInventory(bean.getProductID(), simulCode, timeStep);
			// 설비 작동 여부를 기록(원재료는 기록하지 않는다)
			if(bean.getFacilityID()>0) {
				if(facilExe.size()>0) 
					SimulDAO.getInstance().insertSimulFacility(bean.getFacilityID(), simulCode, timeStep, facilExe.get(i));
				// 처음 실행할 때(step=0)는 설비를 전혀 작동하지 않으므로 모두 0으로 설정
				else
					SimulDAO.getInstance().insertSimulFacility(bean.getFacilityID(), simulCode, timeStep, 0);
			}
		}
		// 남은 할당량 확인, 더 이상 없으면 완료=true
		if(simulList.get(0).getQuota()<=0) complete=true;
		
		return complete;
	}
	
	// 시뮬 초기 설정 + 정보 저장
	private void getSimulCode(int productCode, int quota) {
		simulCode = SimulDAO.getInstance().getMaxSimulCode();
		if(simulCode!=0) SimulDAO.getInstance().insertSimulInfo(simulCode, productCode, quota);
		else complete = true; // 시뮬레이션이 실행되지 않도록 설정한다.
	}
	// 상품을 리스트에 넣는다(상품이 없을 경우 넣지 않는다)
	private void genSimulList(int productCode, int quota) {
		// 모듈 생성
		SimulModule bean = new SimulModule();
		
		// 1.아이디 저장
		bean.setProductID(productCode);

		// 설비 코드를 가져온다. 재료일 경우 0, 상품 자체가 없을 경우 -1
		int facilityCode = SimulDAO.getInstance().getFromInventory(productCode);
		if(facilityCode == -1) return;
		// 2.설비 저장
		bean.setFacilityID(facilityCode);
		if(facilityCode==0) {
			// 재료일 경우
			this.simulList.add(bean);
			return;
		}
		// 3.생산량 저장
		int outputQuant = SimulDAO.getInstance().getFromFacility(facilityCode); 
		if(outputQuant < 1) bean.setOutputQuant(0);
		else				bean.setOutputQuant(outputQuant);
		// 4.투입 재료의 코드 + 수량
		ArrayList<Integer> inputCode = new ArrayList<Integer>();
		ArrayList<Integer> inputQuant = new ArrayList<Integer>();
		SimulDAO.getInstance().getFromInput(facilityCode, inputCode, inputQuant);
		bean.setInputCode(inputCode);
		bean.setInputQuant(inputQuant);
		// 5.생산량 할당
		if(quota<0) quota = 0;
		bean.setQuota(quota);
		int cycle;
		if(outputQuant>0) cycle = (int)Math.ceil(quota / (double)outputQuant );
		else cycle = 0;
		
		// 모듈을 리스트에 저장
		this.simulList.add(bean);
		for(int i=0; i<inputCode.size();i++) {
			this.genSimulList(inputCode.get(i),inputQuant.get(i)*cycle);
		}
	}
}