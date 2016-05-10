import java.util.ArrayList;

import org.apache.log4j.Level;
import org.jetel.component.AbstractGenericTransform;
import org.jetel.data.DataField;
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
public class toWeightedGraph extends AbstractGenericTransform {

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
		//DataRecord outRecord = outRecords[0];

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
		
		ArrayList<Double> limits=new ArrayList<Double>();
		
		//filling the list of limits in my way
		double limit=0.2;
		while (limit<1){
			limits.add(limit);
			limit+=0.15;
		}
		
		
		
		DataRecord inRecord=inRecords[0];
	
		DataField[] fields=inRecord.getFields();
		
		
		
		
		
		
		int n=inRecord.getNumFields()-1;
		
		String[] secids=new String[n];
		String title="SECID";
		double[][] cor_matrix=new double[n][n];
		
		int j=0;
		while ((inRecord = readRecordFromPort(0)) != null){
			fields=inRecord.getFields();
			secids[j]=fields[0].getValue().toString();
			title+=";"+fields[0].getValue().toString();
			System.out.println(inRecord.toString());
			for (int i=0; i<n; i++){
				if (fields[i+1].getValue()!=null)
					cor_matrix[j][i]=Double.parseDouble(fields[i+1].getValue().toString());
			}
			j++;
		}
		System.out.println("READ FINISHED");
		
		
		
		
		DataRecord outRecord=outRecords[0];
		
		for (double lim:limits){
			outRecord.getField(0).setValue(title);
			writeRecordToPort(0, outRecord);
			for (int i=0; i<n-1;i++){
				String s=secids[i];
				//if (fields[i].getValue()!=null)
					//s=fields[i].getValue().toString();
				for (int k=0; k<n-1; k++)
					if (cor_matrix[i][k]>lim)
						s+=";1";
					else
						s+=";0";
				outRecord.getField(0).setValue(s);
				writeRecordToPort(0, outRecord);
				
			}
			outRecord.getField(0).setValue("LIMIT "+lim+" ENDS");
			writeRecordToPort(0, outRecord);		
		}
		//end filling
		
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
