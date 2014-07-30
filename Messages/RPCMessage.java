/*
 * Author : Achintha Gunasekara
 */

package messages;

import java.io.Serializable;
import java.util.UUID;

public class RPCMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum MessageType {REQUEST, REPLY};
	private MessageType messageType;
	private long TransactionId; //transaction id
	private UUID RPCId; //Globally unique identifier
	private long RequestId; //Client request message counter 
	private short procedureId; //e.g.(1,2,3,4)
	private String csv_data; //data as comma separated values 
	private short status;

	public MessageType getMessageType() {

		return messageType;
	}

	public void setMessageType(MessageType messageType) {

		this.messageType = messageType;
	}

	public long getTransactionId() {

		return TransactionId;
	}

	public void setTransactionId(long transactionId) {

		TransactionId = transactionId;
	}

	public UUID getRPCId() {

		return RPCId;
	}

	public void setRPCId(UUID rPCId) {

		RPCId = rPCId;
	}

	public long getRequestId() {

		return RequestId;
	}

	public void setRequestId(long requestId) {

		RequestId = requestId;
	}

	public short getProcedureId() {

		return procedureId;
	}

	public void setProcedureId(short procedureId) {

		this.procedureId = procedureId;
	}

	public String getCsv_data() {

		return csv_data;
	}

	public void setCsv_data(String csv_data) {

		this.csv_data = csv_data;
	}

	public short getStatus() {

		return status;
	}

	public void setStatus(short status) {

		this.status = status;
	}
	
	public void print() {
		
		System.out.println("Message Type : " + messageType);
		System.out.println("Transaction Id : " + TransactionId);
		System.out.println("RPC Id : " + RPCId);
		System.out.println("Request Id : " + RequestId);
		System.out.println("procedure Id : " + procedureId);
		System.out.println("CSV data : " + csv_data);
		System.out.println("procedure Id : " + procedureId);
		System.out.println("Status : " + status);
	}
}
