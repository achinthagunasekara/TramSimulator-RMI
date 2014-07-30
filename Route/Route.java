/*
 * Author : Achintha Gunasekara
 */

package route;

public class Route {
	
	private int routeId;
	private int[] stops;
	private int numberOfTramsOnRoute = 0;
	
	public Route(int routeId, int[] stops) {
		
		this.routeId = routeId;
		this.stops = stops;
	}
	
	public int getRouteId() {
		
		return routeId;
	}
	
	public int getNextStop(int currentStop, int previousStop) {

		for(int i = 0; i < stops.length; i++) {
			
			//first time getting the stop
			if(currentStop == stops[0]) {
				
				return stops[1];
			}

			if(currentStop == stops[i]) {
				
				if(currentStop == stops[stops.length -1]) {
					
					return stops[stops.length-2];
				}
				else if(previousStop == stops[i - 1]) {
					
					return stops[i+1];
				}
				else if(previousStop == stops[i+1]) {
					
					return stops[i-1];
				}
				else {
					
					return -1;
				}
			}
		}
		
		return -1;
	}
	
	public int getFirstStop() {
		
		return stops[0];
	}

	public int getNumberOfTramsOnRoute() {

		return numberOfTramsOnRoute;
	}

	public void incrementNumberOfTramsOnRoute() {

		numberOfTramsOnRoute++;
	}
}