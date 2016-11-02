package fr.upmc.datacenter.dispatcher;

import java.util.HashMap;
import java.util.Map;
import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.dispatcher.interfaces.RequestDispatcherI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
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
	
	public static final String REQ_SUB_IN="DispatcherRequestSubInURI";
	public static final String REQ_SUB_OUT="DispatcherRequestSubOutURI";
	
	public static final String REQ_NOT_OUT="DispatcherRequestNotOutURI";
	public static final String REQ_NOT_IN="DispatcherRequestNotInURI";
	
	protected String uri;
	protected int id;
	
	int lastVM;
	
	protected RequestSubmissionInboundPort	rsip;
	protected RequestNotificationOutboundPort rnop;
	
	protected Map<Integer,RequestSubmissionOutboundPort> rsop;
	protected Map<Integer,RequestNotificationInboundPort> rnip;
	
	public RequestDispatcher(int id) throws Exception{
		/* Init Request Dispatcher */
		this.id=id;
		this.uri="Dispatcher"+id;
		lastVM=0;
		
		rsop=new HashMap<Integer,RequestSubmissionOutboundPort>();
		rnip=new HashMap<Integer,RequestNotificationInboundPort>();
		
		/*RD Ports*/
		rsip = new RequestSubmissionInboundPort(REQ_SUB_IN+id, this);
		this.addPort(rsip);
		this.rsip.publishPort();
		rnop=new RequestNotificationOutboundPort(REQ_NOT_OUT+id, this);
		this.addPort(rnop);
		this.rnop.publishPort();
	}
	
	
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
	
	public void linkRequestGenerator(RequestGenerator rg,RequestSubmissionOutboundPort rg_rsop,RequestNotificationInboundPort rg_rnip) throws Exception{
		rg_rsop.doConnection(rsip.getPortURI(), RequestSubmissionConnector.class.getCanonicalName());
		rnop.doConnection(rg_rnip.getPortURI(), RequestNotificationConnector.class.getCanonicalName());
		
		this.logMessage("Link RG to Dispatcher !"); //"+rg_rnip.getPortURI()+" | "+rg_rsop.getPortURI() );
	}
	
	public void	acceptRequestSubmission(final RequestI r)
	throws Exception
	{
		this.logMessage("Dispatcher : "+r.getRequestURI() +" => "+rsop.get(lastVM).getPortURI());
		rsop.get(lastVM).submitRequest(r);
		lastVM=(++lastVM)%rsop.keySet().size();
	}

	public void	acceptRequestSubmissionAndNotify(final RequestI r) throws Exception
	{
		this.logMessage("Dispatcher&N : "+r.getRequestURI() +"=> "+"VM-"+lastVM);
		rsop.get(lastVM).submitRequestAndNotify(r);
		lastVM=(++lastVM)%rsop.keySet().size();
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		this.logMessage("Dispatcher&T : "+r.getRequestURI() +"=> "+rnop.getPortURI());
		this.rnop.notifyRequestTermination(r);
		
	}

}
