/*
 *
 * @author Archie Gunasekara
 * @date 2014
 * 
 */

package tram;

public class Tram {

	private int tramId;
	private int routeId;
	private int currentStop;
	private int previousStop;
	
	public Tram(int tramId, int routeId) {
		
		this.tramId = tramId;
		this.routeId = routeId;
	}
	
	public int getTramId() {
		
		return tramId;
	}
	
	public int routeId() {
		
		return routeId;
	}
	
	public void setCurrentStop(int currentStop) {
		
		this.currentStop = currentStop;
	}
	
	public int getCurrentStop() {
		
		return currentStop;
	}

	public void setPreviousStop(int previousStop) {
		
		this.previousStop = previousStop;
	}
	
	public int getPreviousStop() {
		
		return previousStop;
	}
}
