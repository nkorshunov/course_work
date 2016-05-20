import java.util.ArrayList;

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
public class MyCustomTransformer extends AbstractGenericTransform {

	@Override
	public void execute() {
		/** This code is an example. Replace it with your custom code. */
		DataRecord in= inRecords[1];
		ArrayList<String> allowed_trades=new ArrayList<String>();
		while ((in = readRecordFromPort(1)) != null) {
			allowed_trades.add(in.getField(0).getValue().toString());
		
		}
		
		/** Record prepared for reading from input port 0 */
		DataRecord inRecord = inRecords[0];

		/** Record prepared for writing to output port 0 */
		DataRecord outRecord = outRecords[0];

		/** Read all records from input port 0 */
		inRecord = readRecordFromPort(0);
		String SECID=null;
		boolean flag=true;
		String curString=null;
		int i=0;
		String date=null;
		while ((inRecord = readRecordFromPort(0)) != null) {
			
			// Get value from input record
			System.out.println("1");
		
				SECID=  inRecord.getField(0).getValue().toString();
				System.out.println("2");
				curString=new String(SECID);
			
			Share prev_share=new Share(SECID,0);
			System.out.println(prev_share);;
			String prev_price=inRecord.getField("Close").getValue().toString();
			String price =new String(prev_price);
			date=inRecord.getField(1).getValue().toString();
			if (allowed_trades.contains(SECID) ){
			while (curString.equals(SECID) && (inRecord!=null)){
				
				Share temp=new Share(SECID,  (Double.parseDouble(price)-Double.parseDouble(prev_price))/Double.parseDouble(prev_price));
				outRecord.getField(0).setValue(temp.toString()+";"+date);
				
				
				writeRecordToPort(0, outRecord);
				if ((inRecord = readRecordFromPort(0)) != null){
				
				
					SECID=  inRecord.getField(0).getValue().toString();
					price=  inRecord.getField("Close").getValue().toString();
					date=inRecord.getField(1).getValue().toString();}
				
			}
			
			flag=false;
			curString=SECID;
			
			outRecord.getField(0).setValue(new Share(SECID,  (Double.parseDouble(price)-Double.parseDouble(prev_price))/Double.parseDouble(prev_price)).toString()+";"+date);
			if (allowed_trades.contains(SECID) )
				writeRecordToPort(0, outRecord);
			}
			
			// Save value multiplied by 2 to output record
			

			// Write output record to output port 0

		}
		
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

class Share{
	String SECID;
	double yield;
	public Share (String s, double y){
		SECID=s;
		yield=y;
	}
	
	public String toString(){
		return SECID+";"+yield;
		
	}
}
