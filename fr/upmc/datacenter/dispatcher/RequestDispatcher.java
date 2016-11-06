package fr.upmc.datacenter.dispatcher;

import java.util.HashMap;
import java.util.Map;
import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.dispatcher.interfaces.RequestDispatcherI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

public class RequestDispatcher extends AbstractComponent
implements RequestDispatcherI,RequestSubmissionHandlerI,RequestNotificationHandlerI
{
	
	public static final String VM_MANAGEMENT="DispatcherVMManagementOut";
	
	public static final String REQ_SUB_IN="DispatcherRequestSubInURI";
	public static final String REQ_SUB_OUT="DispatcherRequestSubOutURI";
	
	public static final String REQ_NOT_OUT="DispatcherRequestNotOutURI";
	public static final String REQ_NOT_IN="DispatcherRequestNotInURI";
	
	protected String RDuri;
	protected int id;
	
	int lastVM;
	
	protected RequestSubmissionInboundPort	rsip;
	protected RequestNotificationOutboundPort rnop;
	
	protected Map<Integer,RequestSubmissionOutboundPort> rsop;
	protected Map<Integer,RequestNotificationInboundPort> rnip;
	protected Map<Integer,ApplicationVMManagementOutboundPort> avmmop;
	
	public RequestDispatcher(int id) throws Exception{
		/* Init Request Dispatcher */
		this.id=id;
		this.RDuri="Dispatcher"+id;
		lastVM=0;
		
		rsop=new HashMap<Integer,RequestSubmissionOutboundPort>();
		rnip=new HashMap<Integer,RequestNotificationInboundPort>();
		
		/*RD Ports connection with RG*/
		rsip = new RequestSubmissionInboundPort(REQ_SUB_IN+id, this);
		this.addPort(rsip);
		this.rsip.publishPort();
		rnop=new RequestNotificationOutboundPort(REQ_NOT_OUT+id, this);
		this.addPort(rnop);
		this.rnop.publishPort();
	}
	
	
	
	public void linkVM(int id, ApplicationVM virtualMachine) throws Exception {
		this.logMessage("VM"+id+" : Linking...");
		RequestSubmissionOutboundPort rsopvm = new RequestSubmissionOutboundPort(REQ_SUB_OUT + id, this);
		RequestNotificationInboundPort rnipvm = new RequestNotificationInboundPort(REQ_NOT_IN + id, this);

		this.addPort(rsopvm);
		this.addPort(rnipvm);

		rsopvm.publishPort();
		rnipvm.publishPort();
		
		this.rsop.put(id, rsopvm);
		this.rnip.put(id, rnipvm);
		this.logMessage("VM"+id+" : Linked !");
}
	
	/*TOREMOVE - Managed by the controller*/
	public void linkVM(int id,ApplicationVM vm,String vm_rsip,String vm_rnop)throws Exception{
		this.logMessage("VM"+id+" : Linking...");
		
		RequestSubmissionOutboundPort rsopvm=new RequestSubmissionOutboundPort(this);
		this.addPort(rsopvm);
		rsopvm.publishPort();
		
		RequestNotificationInboundPort rnipvm=new RequestNotificationInboundPort(this);
		this.addPort(rnipvm);
		rnipvm.publishPort();
		
		this.rsop.put(id,rsopvm);
		this.rnip.put(id,rnipvm);
		
		RequestSubmissionInboundPort rsip=(RequestSubmissionInboundPort) vm.findPortFromURI(vm_rsip);//new RequestSubmissionOutboundPort(REQ_SUB_OUT+id,this);
		RequestNotificationOutboundPort rnop=(RequestNotificationOutboundPort) vm.findPortFromURI(vm_rnop);//new RequestNotificationInboundPort(REQ_NOT_IN+id,this);
		
		
		rsopvm.doConnection(rsip.getPortURI(), RequestSubmissionConnector.class.getCanonicalName());
		rnop.doConnection(rnipvm.getPortURI(), RequestNotificationConnector.class.getCanonicalName());
		
		this.logMessage("VM"+id+" : Linked !");
	}
	
	public void linkRequestGenerator(RequestSubmissionOutboundPort rg_rsop,RequestNotificationInboundPort rg_rnip) throws Exception{
		this.logMessage("Linking RG to Dispatcher["+id+"] ...");
		this.logMessage(rg_rsop +" "+rg_rnip);
		rg_rsop.doConnection(rsip.getPortURI(), RequestSubmissionConnector.class.getCanonicalName());
		rnop.doConnection(rg_rnip.getPortURI(), RequestNotificationConnector.class.getCanonicalName());
		
		this.logMessage("RG linked to Dispatcher["+id+"] !"); //"+rg_rnip.getPortURI()+" | "+rg_rsop.getPortURI() );
	}
	
	public void	acceptRequestSubmission(final RequestI r)
	throws Exception
	{
		this.logMessage("Dispatcher["+id+"] : "+r.getRequestURI() +" => "+rsop.get(lastVM).getPortURI());
		rsop.get(lastVM).submitRequest(r);
		lastVM=(++lastVM)%rsop.keySet().size();
	}

	public void	acceptRequestSubmissionAndNotify(final RequestI r) throws Exception
	{
		this.logMessage("Dispatcher&N["+id+"] : "+r.getRequestURI() +" => "+"VM-"+lastVM);
		rsop.get(lastVM).submitRequestAndNotify(r);
		lastVM=(++lastVM)%rsop.keySet().size();
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		this.logMessage("Dispatcher&T["+id+"] : "+r.getRequestURI() +" => "+rnop.getPortURI());
		this.rnop.notifyRequestTermination(r);
		
	}

}
