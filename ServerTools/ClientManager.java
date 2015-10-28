/*
 *
 * @author Archie Gunasekara
 * @date 2014
 * 
 */

package serverTools;

import java.rmi.Remote;
import java.rmi.RemoteException;

import messages.RPCMessage;

public interface ClientManager extends Remote {
	
	public final short REGISTER_TRAM_ID = 1;
	public final short GET_NEXT_STOP_ID = 2;
	public final short UPDATE_LOCATION_ID = 3;
	
	RPCMessage registerTram(RPCMessage message) throws RemoteException;	
	RPCMessage retreceNextStop(RPCMessage message) throws RemoteException;
	RPCMessage updateTramLocation(RPCMessage message) throws RemoteException;
}
