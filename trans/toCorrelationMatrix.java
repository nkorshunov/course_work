import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.jetel.component.AbstractGenericTransform;
import org.jetel.data.DataRecord;
import org.jetel.exception.ComponentNotReadyException;
import org.jetel.exception.ConfigurationStatus;
import org.jetel.exception.ConfigurationStatus.Priority;
import org.jetel.exception.ConfigurationStatus.Severity;
import org.jetel.metadata.DataRecordMetadata;

/**
 * This is an example custom transformer. It shows how you can
 *  read records, process their values and write records.
 */
public class toCorrelationMatrix extends AbstractGenericTransform {

	@Override
	public void execute() {
		/** This code is an example. Replace it with your custom code. */

		/** This is how you can read component properties. */
		//String customValue = getProperties().getStringProperty("myCustomPropertyName");

		/** You can log messages. */
		//getLogger().log(Level.DEBUG, "Custom property resolved to: " + customValue);

		/** Record prepared for reading from input port 0 */
		//DataRecord inRecord = inRecords[0];

		/** Record prepared for writing to output port 0 */
		DataRecord outRecord = outRecords[0];
		
		/** Read all records from input port 0 */
		/*
		 
		while ((inRecord = readRecordFromPort(0)) != null) {
			// Get value from input record
			Integer valueFromRecord = (Integer) inRecord.getField("myIntegerField").getValue();

			// Save value multiplied by 2 to output record
			outRecord.getField("myIntegerField").setValue(2 * valueFromRecord);

			// Write output record to output port 0
			writeRecordToPort(0, outRecord);
		}
		*/
		
		
		HashMap<String, Double> avg_yields_map=new HashMap<String, Double> ();
		
		DataRecord share_yield=inRecords[1];
		while ((share_yield = readRecordFromPort(1)) != null){
			avg_yields_map.put(share_yield.getField("SECID").getValue().toString(), 
					Double.parseDouble(share_yield.getField("AVG_YIELD").getValue().toString()));
		}
		
		double[][] cor_matrix=new double[avg_yields_map.size()][avg_yields_map.size()];
		
		ArrayList<ShareX> list=new ArrayList<ShareX>();
		DataRecord inRecord=inRecords[0];
		int i=0;
		boolean fr=true;
		String curShare = null;
		String SECID = null;
		inRecord = readRecordFromPort(0);
		while (inRecord != null){
			if (fr){
				curShare=inRecord.getField(0).getValue().toString();
				SECID=new String(curShare);
				fr=false;
			}
			
			list.add(new ShareX(SECID));
			while ((inRecord!=null)&&(curShare.equals(SECID))){
				list.get(i).dates.add(inRecord.getField(1).getValue().toString());
				list.get(i).yields.add(Double.parseDouble(inRecord.getField(2).getValue().toString()));
				
				inRecord=readRecordFromPort(0);
				if (inRecord!=null)
					curShare=inRecord.getField(0).getValue().toString();
			}
			
			SECID=new String(curShare);
			i++;
		}
		
		for (int j=0; j<list.size(); j++){
			for (int k=0; k<list.size(); k++){
				if (k!=j){
					
					cor_matrix[j][k]=ShareX.getCorrelation(list.get(j), list.get(k), avg_yields_map);
					//System.out.println(cor_matrix[j][k]);
				}
				else
					cor_matrix[j][k]=-2;
			}
		}
		
		String s="SECIDS;";
		
		for (int j=0; j<list.size(); j++){
			s+=list.get(j).SECID+";";
		}
		outRecord.getField(0).setValue(s);
		writeRecordToPort(0, outRecord);
		
		
		for (int j=0; j<list.size();j++){
			s=list.get(j).SECID+";";
			for (int k=0; k<list.size(); k++){
				s+=cor_matrix[j][k]+";";
			}
			outRecord.getField(0).setValue(s);
			writeRecordToPort(0, outRecord);
		}
		outRecord.getField(0).setValue("Done");
		
		
		
		
		
		
		
		
	}

	@Override
	public ConfigurationStatus checkConfig(ConfigurationStatus status) {
		super.checkConfig(status);

		/** This way you can check connected edges and their metadata. */
		/*
		if (getComponent().getInPorts().size() < 1 || getComponent().getOutPorts().size() < 1) {
			status.add("Both input and output port must be connected!", Severity.ERROR, getComponent(), Priority.NORMAL);
			return status;
		}

		DataRecordMetadata inMetadata = getComponent().getInputPort(0).getMetadata();
		DataRecordMetadata outMetadata = getComponent().getOutputPort(0).getMetadata();
		if (inMetadata == null || outMetadata == null) {
			status.add("Metadata on input or output port not specified!", Severity.ERROR, getComponent(), Priority.NORMAL);
			return status;
		}

		if (inMetadata.getFieldPosition("myIntegerField") == -1) {
			status.add("Incompatible input metadata!", Severity.ERROR, getComponent(), Priority.NORMAL);
		}
		if (outMetadata.getFieldPosition("myIntegerField") == -1) {
			status.add("Incompatible output metadata!", Severity.ERROR, getComponent(), Priority.NORMAL);
		}
		*/
		return status;
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void preExecute() throws ComponentNotReadyException {
		super.preExecute();
	}

	@Override
	public void postExecute() throws ComponentNotReadyException {
		super.postExecute();
	}
}


class ShareX{
	
	String SECID;
	ArrayList<String> dates=new ArrayList<String>();
	ArrayList<Double> yields=new ArrayList<Double>();
	
	public ShareX(String s){
		SECID=s;
	}
	
	public static double getCorrelation(ShareX a, ShareX b, HashMap<String, Double> map){
		double numin = 0;
		double denom;
		int i=0;
		double a_avg=map.get(a.SECID);
		double b_avg=map.get(b.SECID);
		for(String date:a.dates){
			if(b.dates.contains(date)){
				numin+=(a.yields.get(a.dates.indexOf(date))-a_avg)*(b.yields.get(b.dates.indexOf(date))-b_avg);
			}
			else{
				numin+=(a.yields.get(a.dates.indexOf(date))-a_avg)*(0-b_avg);
			}
		}
		
		for(String date:b.dates){
			if(!a.dates.contains(date)){
				numin+=(0-a_avg)*(b.yields.get(b.dates.indexOf(date))-b_avg);
			}
		}
		
		double a_denom=0;
		double b_denom=0;
		for(String date:a.dates){
			if(b.dates.contains(date)){
				a_denom+=(a.yields.get(a.dates.indexOf(date))-a_avg)*(a.yields.get(a.dates.indexOf(date))-a_avg);
				b_denom+=(b.yields.get(b.dates.indexOf(date))-b_avg)*(b.yields.get(b.dates.indexOf(date))-b_avg);
			}
			else{
				a_denom+=(a.yields.get(a.dates.indexOf(date))-a_avg)*(a.yields.get(a.dates.indexOf(date))-a_avg);
				b_denom+=(0-b_avg)*(0-b_avg);
			}
		}
		
		
		for(String date:b.dates){
			if(!a.dates.contains(date)){
				a_denom+=(0-a_avg)*(0-a_avg);
				b_denom+=(b.yields.get(b.dates.indexOf(date))-b_avg)*(b.yields.get(b.dates.indexOf(date))-b_avg);
			}
		}
		
		denom=Math.sqrt(a_denom*b_denom);
		
		
		
		return numin/denom;
		
	}
}

