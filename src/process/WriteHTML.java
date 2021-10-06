package process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import dao.HtmlDAO;

public class WriteHTML {
	private int timeStep;
	private ArrayList<SimulModule> simulList;
	private int simulCode;
	public WriteHTML(int timeStep, ArrayList<SimulModule> simulList, int simulCode) {
		this.timeStep = timeStep;
		this.simulList = simulList;
		this.simulCode = simulCode;
	}
	public String write() {
		String name = "result.html";
		OutputStream os = null;
		OutputStreamWriter osw = null;
		PrintWriter pw = null;
		String path = new File(name).getAbsolutePath();
		try {
			os = new FileOutputStream(name);
			osw = new OutputStreamWriter(os,"UTF-8");
			pw = new PrintWriter(osw);
			 
			this.htmlBody(pw);
			this.htmlScript(pw);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {if(pw!=null)pw.close();}catch(Exception e) {}
			try {if(osw!=null)osw.close();}catch(Exception e) {}
			try {if(os!=null)os.close();}catch(Exception e) {}
		}
		return path;
	}
	private void htmlBody(PrintWriter pw) {
		pw.println("<!DOCTYPE html><html><head><meta charset=\"utf-8\">");
		pw.println("<title>Simulation Visualization</title>");
		pw.println("<script src='https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.5.1/chart.js'></script>");
		pw.println("</head><body><canvas id=\"stockChart\" width=\"210\" height=\"100\"></canvas>");
		pw.println("<canvas id=\"facilityChart\" width=\"210\" height=\"100\"></canvas></body></html>");
		pw.println("");
	}
	private void htmlScript(PrintWriter pw) {
		pw.println("<script>");
		pw.println("var ctx1 = document.getElementById('stockChart');");
		pw.println("var stockChart = new Chart(ctx1, {type: 'line',data: {");
		
		String label = "0";
		for(int i=1;i<=timeStep;i++)
			label+= ","+i;
		
		pw.println("labels: ["+label+"],");
		pw.println("datasets: [");
		
		for(int i=0;i<simulList.size();i++)
			this.getDataSet(pw, simulList.get(i), i);
		
		pw.println("]},});</script>");
		
		pw.println("");
		pw.println("<script type=\"text/javascript\">");
		pw.println("var config={type: 'scatter',data: {");
		pw.println("datasets: [");
		
		pw.println("{label:'',");
		pw.println("data:[");
		for(int i=0;i<simulList.size()+2;i++) {
			pw.println("{x:"+0+",y:"+i+"},");
		}
		pw.println("],");
		
		pw.println("borderColor:['rgba(255,255,255,0)',],");
		pw.println("backgroundColor:['rgba(255,255,255,0)',],},");
		
		pw.println("{label: 'Facility Operation',");
		pw.println("backgroundColor:['rgba(44,44,44,1)',],");
		pw.println("borderColor:['rgba(44,44,44,44,0)',],");
		pw.println("pointRadius: document.getElementById(\"facilityChart\").offsetWidth*4.2 / "+timeStep+",");
		pw.println("borderWidth: 1,");
		pw.println("pointStyle: 'rect',data: [");
		for(int i=0;i<simulList.size();i++) {
			HtmlDAO.getInstance().genDataSet(i+1, simulList.get(i).getFacilityID(),
											simulCode, pw);
		}
		pw.println("]}]},options: {scales: {y:{");
		pw.println("ticks:{callback: function(label, index,labels){return yLabels[label];}}}");
		pw.println("}}}");
		
		String labels = "";
		for(int i=0;i<simulList.size();i++) {
			if(simulList.get(i).getFacilityID()>0)
				labels+= (i+1)+":'Facility No."+simulList.get(i).getFacilityID()+"',";
			else
				labels+= (i+1)+":'',";
		}
		pw.println("var yLabels= {0:'',"+labels+(simulList.size()+1)+":''}");
		
		pw.println("var ctx2 = document.getElementById('facilityChart')");
		pw.println("var scatterChart = new Chart(ctx2, config)");
		pw.println("</script>");
		pw.println("");
	}
	private void getDataSet(PrintWriter pw, SimulModule bean, int index) {
		pw.println("{");
		pw.println("label: 'Stoke NO."+bean.getProductID()+"',");
		pw.println("data: ["+HtmlDAO.getInstance().getStockHistory(bean.getProductID(), simulCode)+"],");
		pw.println("borderWidth: 1,fill: true,");
		
		String RGB = this.colorSetting(index, simulList.size());
		pw.println("borderColor:['rgba("+RGB+",0.5)',],");
		pw.println("backgroundColor:['rgba("+RGB+",0.02)',],");
		pw.println("},");
	}
	// 인덱스와 전체 길이를 가지고 RGB를 만드는 메서드
	private String colorSetting(int x, int length) {
		int n = (length+1)/2;
		int unit = 255/n;
		int r = n-x>0 ? n-x : 0;
		int g = x<n ? x : 2*n-1 - x;
		int b = x-(n-1)>0 ? x-(n-1) : 0;
		String color = r*unit+","+g*unit+","+b*unit;
		return color;
	}
}
