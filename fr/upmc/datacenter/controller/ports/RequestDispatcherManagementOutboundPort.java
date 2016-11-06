package fr.upmc.datacenter.controller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.controller.Controller;
import fr.upmc.datacenter.controller.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementOutboundPort
extends		AbstractOutboundPort
implements	RequestDispatcherManagementI{


	public		RequestDispatcherManagementOutboundPort(
			ComponentI owner
			) throws Exception
	{
		super(RequestDispatcherManagementI.class, owner) ;

		assert	owner != null && owner instanceof Controller ;
	}

	public				RequestDispatcherManagementOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, RequestDispatcherManagementI.class, owner);

		assert	owner != null && owner instanceof Controller ;
	}

	@Override
	public void deployVM(int rd, String RequestDispatcherURIDVM) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyVM(String uriComputerParent, String vm) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initVM(int application, String uriComputerParent, String vm) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbindVM(String uriComputerParent, String vm) throws Exception {
		// TODO Auto-generated method stub

	}
}
