package index;
import simulator.SimulManager;
/**
 * @summary 프로그램 실행 class
 * @author hangeyol
 */
public class Index {
	public static void main(String[] args) {
		
		SimulManager simMan = SimulManager.getInstance();
		simMan.adminLogin();
//		simMan.productSimulation();
//		
		
//		Process p = new Process(1,10);
//		System.out.println(" 시뮬레이션 진행 중...\n");
//		while(!p.execute()) {}
//		System.out.println("<<시뮬레이션 완료>>");
//		p.resultHTML();
	}
}