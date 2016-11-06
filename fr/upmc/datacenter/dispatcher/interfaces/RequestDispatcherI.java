package fr.upmc.datacenter.dispatcher.interfaces;

import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

public interface RequestDispatcherI {

	public void linkRequestGenerator(RequestSubmissionOutboundPort rg_rsop,RequestNotificationInboundPort rg_rnip) throws Exception;
	public void linkVM(int id,ApplicationVM vm,String vm_rsip,String vm_rnop)throws Exception;
}
