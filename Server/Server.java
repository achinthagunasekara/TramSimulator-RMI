/*
 *
 * @author Archie Gunasekara
 * @date 2014
 * 
 */

package server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessControlException;
import java.util.HashMap;

import messages.RPCMessage;
import messages.RPCMessage.MessageType;
import route.Route;
import serverTools.ClientManager;
import tools.CommanOps;
import tools.ConfigFileReader;
import tram.Tram;

public class Server extends UnicastRemoteObject implements ClientManager {

	private static final long serialVersionUID = 1L;
	private HashMap<Integer, Route> routes = new HashMap<Integer, Route>();
	private HashMap<Integer, Tram> trams = new HashMap<Integer, Tram>();
	
	private int MAX_NUM_OF_TRAMS_PER_ROUTE;
	
	protected Server() throws RemoteException {

	}
	
	public static void main(String args[]) throws IOException{
		
		try {
			
			//needs to be before setting the security manager
			String POLICY_FILE = ConfigFileReader.getConfigFileReaderInstance().getPropertyVal("POLICY_FILE");
			String RMI_HOST = ConfigFileReader.getConfigFileReaderInstance().getPropertyVal("RMI_HOST");
			int PORT = Integer.parseInt(ConfigFileReader.getConfigFileReaderInstance().getPropertyVal("PORT"));
			String STUB = ConfigFileReader.getConfigFileReaderInstance().getPropertyVal("STUB");
			
			System.setProperty("java.security.policy", POLICY_FILE);
			System.setSecurityManager(new SecurityManager());
			LocateRegistry.createRegistry(PORT);
			
			Server server = new Server();
			server.runSetup();
			
			Naming.rebind("rmi://" + RMI_HOST + ":" + PORT + "/" + STUB, server);
			System.out.println("Tram Server Has Started on " + RMI_HOST + ":" + PORT);	
		}
		catch (RemoteException re) {
			
			System.out.println("Error starting the server. Remote error : " + re.getMessage());
			System.exit(0);
		}
		catch (MalformedURLException me) {
			
			System.out.println("Error starting the server. Bad URL error : " + me.getMessage());
			System.exit(0);
		}
		catch (AccessControlException acEx) {
			
			System.out.println("Error starting the server. Access control error : " + acEx.getMessage());
			System.exit(0);
		}
		catch (Exception ex) {
			
			System.out.println("Error starting the server. General error : " + ex.getMessage());
			System.exit(0);
		}
	}
	
	private void runSetup() throws Exception {
		
		MAX_NUM_OF_TRAMS_PER_ROUTE = Integer.parseInt(ConfigFileReader.getConfigFileReaderInstance().getPropertyVal("MAX_NUM_OF_TRAMS_PER_ROUTE"));
		
		Route ROUTE_1 = new Route(1, new int[]{1, 2, 3, 4, 5});
		Route ROUTE_96 = new Route(96, new int[]{23, 24, 2, 34, 22});
		Route ROUTE_101 = new Route(101, new int[]{123, 11, 22, 34, 5, 4, 7});
		Route ROUTE_109 = new Route(109, new int[]{88, 87, 85, 80, 9, 7, 2, 1});
		Route ROUTE_112 = new Route(112, new int[]{110, 123, 11, 22, 34, 33, 29, 4});
		
		routes.put(1, ROUTE_1);
		routes.put(96, ROUTE_96);
		routes.put(101, ROUTE_101);
		routes.put(109, ROUTE_109);
		routes.put(112, ROUTE_112);
	}
	
	@Override
	public RPCMessage registerTram(RPCMessage message) throws RemoteException {
		
		System.out.println("Incoming tram registration message - RPC ID " + message.getRPCId());
		
		//if not valid this will throw an exception
		validateMessage(message, ClientManager.REGISTER_TRAM_ID);
		
		message.setMessageType(MessageType.REPLY);

		int[] csvData = CommanOps.processCsvData(message.getCsv_data());
			
		//get tram route
		Route r = routes.get(csvData[1]);

		if(r == null) {
			
			System.out.println("Tram Route " + csvData[1] + " does not exisit!");
			throw new RemoteException("Tram Route " + csvData[1] + " does not exisit!");
		}
		
		if(r.getNumberOfTramsOnRoute() >= MAX_NUM_OF_TRAMS_PER_ROUTE) {
			
			System.out.println("Only " + MAX_NUM_OF_TRAMS_PER_ROUTE + " trams allowed on one route! Unable to register the tram on route" + csvData[1] + "!");
			throw new RemoteException("Only " + MAX_NUM_OF_TRAMS_PER_ROUTE + " trams allowed on one route! Unable to register the tram on route" + csvData[1] + "!");
		}
		else {
			
			r.incrementNumberOfTramsOnRoute();
		}
		
		Tram t = new Tram(csvData[0], csvData[1]);
		trams.put(csvData[0], t);
		
		System.out.println("Registering Tram ID " + csvData[0] + " on route " + csvData[1]);
		System.out.println("Current # of trams on route " + r.getRouteId() + " is " + r.getNumberOfTramsOnRoute());
		
		//get first stop
		message.setCsv_data(r.getFirstStop() + "");
		message.setStatus((short) 1);
		return message;
	}

	@Override
	public RPCMessage retreceNextStop(RPCMessage message) throws RemoteException {
		
		System.out.println("Incoming get next stop message - RPC ID " + message.getRPCId());
		
		//if not valid this will throw an exception
		validateMessage(message, ClientManager.GET_NEXT_STOP_ID);
		
		message.setMessageType(MessageType.REPLY);
		
		int[] csvData = CommanOps.processCsvData(message.getCsv_data());

		Route r = routes.get(csvData[0]);
		
		if(r == null) {
		
			System.out.println("Unable to get next stop. Tram Route " + csvData[0] + " does not exisit!");
			throw new RemoteException("Tram Route " + csvData[0] + " does not exisit!");
		}
		
		int nextStop = r.getNextStop(csvData[1], csvData[2]);
			
		message.setCsv_data(nextStop + "");
		message.setStatus((short) 1);
		
		System.out.println("Location update sent for route ID " + csvData[0] + ". Next stop sent : " + nextStop);
		
		return message;
	}

	@Override
	public RPCMessage updateTramLocation(RPCMessage message) throws RemoteException {
		
		System.out.println("Incoming update location message - RPC ID " + message.getRPCId());
		
		//if not valid this will throw an exception
		validateMessage(message, ClientManager.UPDATE_LOCATION_ID);
		
		message.setMessageType(MessageType.REPLY);
		
		int[] csvData = CommanOps.processCsvData(message.getCsv_data());
		
		Tram t = trams.get(csvData[0]);
		
		if(t == null) {
		
			System.out.println("Unable to update location. Tram # " + csvData[0] + " is not registered on the system!");
			throw new RemoteException("Tram ID " + csvData[0] + " is not registered on the system!");
		}

		t.setCurrentStop(csvData[1]);
		t.setPreviousStop(csvData[2]);
			
		message.setCsv_data("");
		message.setStatus((short) 1);
		
		System.out.println("Locatiom updated for tram ID " + csvData[0]);
		
		return message;
	}
	
	private boolean validateMessage(RPCMessage message, short procedureId) throws RemoteException {
		
		if(message.getMessageType() != RPCMessage.MessageType.REQUEST) {
			
			throw new RemoteException("Bad Message received - " + message.getMessageType() + " is not a request. TransactionId : " + message.getTransactionId() + ", RPCId : " + message.getRPCId() + ", ProcedureId : " + message.getProcedureId());
		}
		if(message.getProcedureId() != procedureId) {
			
			throw new RemoteException("Procedure IDs do not match! Method procedure ID : " + procedureId + ", Message procedure ID : " + message.getProcedureId());
		}
		
		return true;
	}
}
