/*
 *
 * @author Archie Gunasekara
 * @date 2014
 * 
 */

package tools;

import java.util.UUID;

import messages.RPCMessage;

public class CommanOps {

	public static int[] processCsvData(String s) {
		
		String[] stringArr = s.split(",");
		int[] intArr = new int[stringArr.length];
		
		for(int i = 0; i < stringArr.length; i++) {
			
			intArr[i] = Integer.parseInt(stringArr[i]);
		}
		
		return intArr;
	}
	
	//generate globally unique identifier
	public static UUID generateUniqueId() {
		
		return UUID.randomUUID();
	}
	
	public static RPCMessage getRpcMessTemplate(long transactionId, int requestId, short setProcedureId) {
		
		RPCMessage message = new RPCMessage();
		message.setMessageType(RPCMessage.MessageType.REQUEST);
		message.setTransactionId(transactionId);
		message.setRPCId(generateUniqueId());
		message.setRequestId(requestId);
		message.setProcedureId(setProcedureId);
		message.setStatus((short) 0);
		
		return message;
	}
}
