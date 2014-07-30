/*
 * Author : Achintha Gunasekara
 */

package client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Random;

import messages.RPCMessage;
import serverTools.ClientManager;
import tools.CommanOps;
import tools.ConfigFileReader;

public class Client {
	
	private ClientManager cManager;
	private int tramId;
	private int routeId;
	private int requestId = 0;
	private long transactionId = 0;
	private int currentStop;
	private int previousStop;

	public static void main(String args[]) {
		
		try {

			Client c = new Client();
			c.run();
		}
		catch(Exception ex) {
			
			System.out.println("Unable to create a tram client. Error : " + ex.getMessage());
		}
	}
	
	public Client() throws Exception {
		
		tramId = Integer.parseInt(ConfigFileReader.getConfigFileReaderInstance().getPropertyVal("TRAM_ID"));
		routeId = Integer.parseInt(ConfigFileReader.getConfigFileReaderInstance().getPropertyVal("ROUTE_ID"));
	}
	
	public void run() {
		
		try {
			
			String RMI_HOST = ConfigFileReader.getConfigFileReaderInstance().getPropertyVal("RMI_HOST");
			int PORT = Integer.parseInt(ConfigFileReader.getConfigFileReaderInstance().getPropertyVal("PORT"));
			String STUB = ConfigFileReader.getConfigFileReaderInstance().getPropertyVal("STUB");
			
			cManager = (ClientManager)Naming.lookup("rmi://" + RMI_HOST + ":" + PORT + "/" + STUB);
			System.out.println("Registering the tram ID " + tramId);
			this.registerTram();
			this.runTram();
		}
		catch (Exception ex)
		{
			System.out.println("Unable to make a server connection. Error : " + ex.getMessage());
			System.exit(0);
		}
	}
	
	private void runTram() throws RemoteException, InterruptedException, Exception {
		
		while(true) {
			
			Random r = new Random();
			int nextRun = r.nextInt(1000) + 1000;
			System.out.println("Waiting for " + nextRun + " miliseconds before reaching next stop...");
			Thread.sleep(nextRun);
			
			//get the next stop and update tram location
			if(this.getNextStop()) {
				
				this.updateLocation();
			}
			else {
				
				System.out.println("Skiping the itration - invalid responce from the server!");
			}
		}
	}
	
	private boolean registerTram() throws Exception {
		
		RPCMessage sendMess = CommanOps.getRpcMessTemplate(incrementTransactionId(), requestId, ClientManager.REGISTER_TRAM_ID);
		sendMess.setCsv_data(tramId + "," + routeId);
		sendMess.setStatus((short) 0);
		
		requestId++;
			
		RPCMessage recMess = cManager.registerTram(sendMess);
			
		if(this.compareTwoMessages(sendMess, recMess, ClientManager.REGISTER_TRAM_ID)) {
			
			System.out.println("Tram ID " + tramId + " registered with the server. Tram is running on route " + routeId);
			
			int[] csvVals = CommanOps.processCsvData(recMess.getCsv_data());
			currentStop = csvVals[0];
			//when a tram starts for the fist time there is no previous stop
			previousStop = -1;
			
			return true;
		}
		else {
				
			throw new Exception("Reply message is invalid! Unable to register the tram " + tramId + " on route " + routeId);
		}
	}
	
	private boolean getNextStop() throws Exception {
		
		RPCMessage sendMess = CommanOps.getRpcMessTemplate(incrementTransactionId(), requestId, ClientManager.GET_NEXT_STOP_ID);
		sendMess.setCsv_data(routeId + "," + currentStop + "," + previousStop);
		sendMess.setStatus((short) 0);
		
		requestId++;
		
		RPCMessage recMess = cManager.retreceNextStop(sendMess);
		
		if(this.compareTwoMessages(sendMess, recMess, ClientManager.GET_NEXT_STOP_ID)) {
			
			System.out.println("Tram ID " + tramId + " is getting next stop...");
			
			int[] csvVals = CommanOps.processCsvData(recMess.getCsv_data());
			previousStop = currentStop;
			currentStop = csvVals[0];
			
			System.out.println("Tram ID : " + tramId + " on route ID : " + routeId + " || Next Stop : " + currentStop + " Previous Stop is : " + previousStop);
			
			return true;
		}
		else {
				
			throw new Exception("Reply message is invalid! Unable to get next stop for the tram " + tramId);
		}
	}
	
	private boolean updateLocation() throws Exception {
		
		RPCMessage sendMess = CommanOps.getRpcMessTemplate(incrementTransactionId(), requestId, ClientManager.UPDATE_LOCATION_ID);
		sendMess.setCsv_data(tramId + "," + currentStop + "," + previousStop);
		sendMess.setStatus((short) 0);
		
		requestId++;
		
		RPCMessage recMess = cManager.updateTramLocation(sendMess);
		
		if(this.compareTwoMessages(sendMess, recMess, ClientManager.UPDATE_LOCATION_ID)) {
			
			System.out.println("Tram ID " + tramId + " updating the location on the server...");
			
			return true;
		}
		else {
				
			throw new Exception("Reply message is invalid! Unable to update tram " + tramId + " location");
		}
	}
	
	private boolean compareTwoMessages(RPCMessage sendMess, RPCMessage recMess, short procedureId) {
		
		if(sendMess.getTransactionId() != recMess.getTransactionId()) {

			System.out.println("TransactionId doesn't match! Request TransactionId : " + sendMess.getTransactionId() + ", reply TransactionId " + recMess.getTransactionId());
			return false;
		}
		if(sendMess.getRPCId().compareTo(recMess.getRPCId()) != 0) {

			System.out.println("RPC ID doesn't match! Request RPCId : " + sendMess.getRPCId() + ", reply RPCId " + recMess.getRPCId());
			return false;
		}
		//make sure sent and received messages have the correct procedure ID
		if(sendMess.getProcedureId() != procedureId || recMess.getProcedureId() != procedureId) {
			
			System.out.println("ProcedureId doesn't match! Request ProcedureId : " + sendMess.getProcedureId() + ", reply ProcedureId " + recMess.getProcedureId() + ". Method procedure ID : " + procedureId);
			return false;
		}
		if(sendMess.getMessageType() == recMess.getMessageType()) {
			
			System.out.println("Message type are the same! Request MessageType : " + sendMess.getMessageType() + ", reply MessageType " + recMess.getMessageType());
			return false;
		}
		if(sendMess.getStatus() == recMess.getStatus()) {
			
			System.out.println("Status are the same - message may have not been processed!  Request Status : " + sendMess.getStatus() + ", Reply Status " + recMess.getStatus());
			return false;
		}
		
		return true;
	}
	
	private long incrementTransactionId() {
		
		return transactionId + tramId + 1;
	}
}