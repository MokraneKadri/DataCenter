package fr.upmc.datacenter.controller.Factory;

import java.util.HashMap;
import java.util.Map;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;

public class VMFactory{
	public static final String URI_PREFIX = "vm_";
	public static final String INBOUND_URI_PREFIX = URI_PREFIX+"RequestSubmissionInboundPortURI_";
	public static final String OUTBOUND_URI_PREFIX = URI_PREFIX+"RequestNotificationOutboundPortURI_";

	protected static int counter = 0;

	public static Map<Integer, ApplicationVM> createVMs(int number, String inboundPortURI) throws Exception {
		Map<Integer, ApplicationVM> virtualMs = new HashMap<>();
		for(int i = 0; i < number; i++) {

			ApplicationVM virtualM = new ApplicationVM(
					URI_PREFIX + counter,
					inboundPortURI + counter,
					INBOUND_URI_PREFIX + counter,
					OUTBOUND_URI_PREFIX + counter);

			virtualM.toggleTracing() ;
			virtualM.toggleLogging() ;

			AbstractCVM.theCVM.addDeployedComponent(virtualM);

			virtualMs.put(counter++, virtualM);
		}
		return virtualMs;
	}	
}

