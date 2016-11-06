package fr.upmc.datacenter.controller.interfaces;

import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

public interface ApplicationRequestI {
	public boolean acceptApplication(Integer application,
			String requestGeneratorURI) throws Exception;

	boolean acceptApplication(Integer application, String requestGeneratorURI, RequestSubmissionOutboundPort rg_rsop,
			RequestNotificationInboundPort rg_rnip) throws Exception;

}

