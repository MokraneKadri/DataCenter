package fr.upmc.datacenter.controller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.controller.Controller;
import fr.upmc.datacenter.controller.interfaces.ApplicationRequestI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

public class ApplicationRequestOutboundPort extends AbstractOutboundPort implements ApplicationRequestI{
	public		ApplicationRequestOutboundPort(
			ComponentI owner
			) throws Exception
	{
		super(ApplicationRequestI.class, owner) ;

		assert	owner != null && owner instanceof Controller ;
	}

	public				ApplicationRequestOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, ApplicationRequestI.class, owner);

		assert	owner != null && owner instanceof Controller ;
	}

	@Override
	public boolean acceptApplication(Integer application, String requestGeneratorURI) throws Exception {
		return ((ApplicationRequestI)this.connector).acceptApplication(application, requestGeneratorURI) ;
	}

	@Override
	public boolean acceptApplication(Integer application, String requestGeneratorURI,
			RequestSubmissionOutboundPort rg_rsop, RequestNotificationInboundPort rg_rnip) throws Exception {
		return ((ApplicationRequestI)this.connector).acceptApplication(application, requestGeneratorURI, rg_rsop, rg_rnip);
	}
}
