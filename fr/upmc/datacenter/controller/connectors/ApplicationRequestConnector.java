package fr.upmc.datacenter.controller.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.controller.interfaces.ApplicationRequestI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

public class ApplicationRequestConnector
extends		AbstractConnector
implements	ApplicationRequestI
{
	@Override
	public boolean acceptApplication(Integer application, String requestGeneratorURI) throws Exception {
		return ((ApplicationRequestI)this.offering).acceptApplication(application, requestGeneratorURI);
	}

	@Override
	public boolean acceptApplication(Integer application, String requestGeneratorURI,
			RequestSubmissionOutboundPort rg_rsop, RequestNotificationInboundPort rg_rnip) throws Exception {
		return ((ApplicationRequestI)this.offering).acceptApplication(application, requestGeneratorURI, rg_rsop, rg_rnip);
	}
}

