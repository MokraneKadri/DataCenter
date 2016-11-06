package fr.upmc.datacenter.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.controller.Factory.VMFactory;
import fr.upmc.datacenter.controller.interfaces.ApplicationRequestI;
import fr.upmc.datacenter.controller.interfaces.ControllerI;
import fr.upmc.datacenter.controller.interfaces.RequestDispatcherManagementI;
import fr.upmc.datacenter.controller.ports.ApplicationRequestInboundPort;
import fr.upmc.datacenter.dispatcher.RequestDispatcher;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementInboundPort;

public class Controller extends AbstractComponent
implements ControllerI,ApplicationRequestI,RequestDispatcherManagementI
{

	public static final String CONTROLLER_PREFIX = "CO_";

	private final int PARAMETER_INITIAL_NB_CORE=2;
	private final int PARAMETER_INITIAL_NB_VM=1;

	private int APP_ID=1;
	private int RD_ID=1;
	private int COMP_ID=1;

	String controllerURI;
	String computerURI;

	ApplicationRequestInboundPort applicationRequestInboundPort;

	Map<Integer,String> applicationURI;
	Map<Integer,String> requestDispatcherURI;
	Map<Integer,String> requestDispatcherManagementURIs;
	/* Liste des VM attribuer à un Request dispatcher */
	Map<Integer,HashMap<Integer,String>> requestDispatcherVMURIs;
	Map<Integer,String> computerURIs;
	Map<Integer,ComputerServicesOutboundPort> computerPorts;

	protected Map<Integer, ApplicationVMManagementOutboundPort> vmManagementOutBountPorts;
	protected Map<ApplicationVMManagementOutboundPort,String> vmManagementOBPwithVMUris=new HashMap<ApplicationVMManagementOutboundPort,String>();


	public Controller(String controllerURI,String applicationRequestInboundURI) throws Exception{
		/* TODO */

		this.controllerURI=controllerURI;
		/*Init Maps*/
		applicationURI=new HashMap<Integer,String>();
		requestDispatcherURI=new HashMap<Integer,String>();
		requestDispatcherManagementURIs=new HashMap<Integer,String>();
		requestDispatcherVMURIs=new HashMap<Integer,HashMap<Integer,String>>();
		computerURIs = new HashMap<Integer,String>();
		computerPorts = new HashMap<Integer,ComputerServicesOutboundPort>();

		vmManagementOutBountPorts=new HashMap<Integer, ApplicationVMManagementOutboundPort>();

		/* Init all ports */

		this.addOfferedInterface(ApplicationRequestI.class) ;
		this.applicationRequestInboundPort = new ApplicationRequestInboundPort(
				applicationRequestInboundURI, this) ;
		this.addPort(this.applicationRequestInboundPort) ;
		this.applicationRequestInboundPort.publishPort() ;

		//		this.addRequiredInterface(RequestSubmissionI.class) ;
		//		this.rsop = new RequestSubmissionOutboundPort(requestSubmissionOutboundPortURI, this) ;
		//		this.addPort(this.rsop) ;
		//		this.rsop.publishPort() ;
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

	@Override
	public boolean acceptApplication(Integer application, String requestGeneratorURI, RequestSubmissionOutboundPort rg_rsop,RequestNotificationInboundPort rg_rnip) throws Exception {
		this.logMessage("New Application : "+application+" from ["+requestGeneratorURI+"]");
		/*Creation of the RequestDispatcher*/
		RequestDispatcher rd=new RequestDispatcher(RD_ID);
		this.logMessage("Controller : RD["+RD_ID+"] created");
		rd.toggleLogging();
		rd.toggleTracing();

		/*Creation of the VMs*/
		Map<Integer, ApplicationVM> createdVMs = VMFactory.createVMs(PARAMETER_INITIAL_NB_VM, VMFactory.URI_PREFIX+"ApplicationVMManagementInboundPortURI_");

		this.addRequiredInterface(ApplicationVMManagementI.class);
		for(Entry<Integer, ApplicationVM> vm : createdVMs.entrySet()) {
			Integer key = vm.getKey();

			ApplicationVMManagementOutboundPort vmMPort = new ApplicationVMManagementOutboundPort(CONTROLLER_PREFIX+"ApplicationVMManagementOutboundPortURI_" + key,this);

			vmMPort.publishPort();

			vmMPort.doConnection(
					VMFactory.URI_PREFIX+"ApplicationVMManagementInboundPortURI_" + key,
					ApplicationVMManagementConnector.class.getCanonicalName());
			/*Useless for now, will be needed for the next step */
			this.vmManagementOBPwithVMUris.put(vmMPort,((ApplicationVM)vm.getValue()).findInboundPortURIsFromInterface(RequestSubmissionI.class)[0]);

			this.vmManagementOutBountPorts.put(key, vmMPort);
		}

		/*Allocation of the core for the VMs*/

		for(Entry<Integer, ApplicationVM> vm : createdVMs.entrySet()) {

			for(Entry<Integer, ComputerServicesOutboundPort> ports : computerPorts.entrySet()){
				AllocatedCore[] aC =ports.getValue().allocateCores(PARAMETER_INITIAL_NB_CORE);
				if(aC.length!=0){
					vm.getValue().allocateCores(aC);
					break;

				}else{
					/*TODO*
					 * We can't allocate any core to the application
					 * We refuse the submission
					 * -> Remove the allocation of the vms etc ...
					 */
					return false;
				}
			}
		}
		/*Link components*/
		/*Link all the VMs to the Request Dispatcher*/
		for(Entry<Integer, ApplicationVM> vm : createdVMs.entrySet()){
			rd.linkVM(vm.getKey(), vm.getValue());

			RequestSubmissionOutboundPort rdrsop = 
					(RequestSubmissionOutboundPort) rd.findPortFromURI(RequestDispatcher.REQ_SUB_OUT + vm.getKey());
			rdrsop.doConnection(VMFactory.INBOUND_URI_PREFIX + vm.getKey(), 
					RequestSubmissionConnector.class.getCanonicalName());

			RequestNotificationOutboundPort vmrnop =
					(RequestNotificationOutboundPort)
					vm.getValue().findPortFromURI(VMFactory.OUTBOUND_URI_PREFIX + vm.getKey());
			vmrnop.doConnection(RequestDispatcher.REQ_NOT_IN + vm.getKey(), 
					RequestNotificationConnector.class.getCanonicalName());
			/* Not needed now ...*/
//			ApplicationVMManagementOutboundPort avmmop = 
//					(ApplicationVMManagementOutboundPort)rd.findPortFromURI(RequestDispatcher.VM_MANAGEMENT+vm.getKey());
//
//			avmmop.doConnection(VMFactory.URI_PREFIX+vm.getKey(),
//					ApplicationVMManagementConnector.class.getCanonicalName());
		}
		
		/*Link the requestGenerator and the RequestDispatcher*/
		rd.linkRequestGenerator(rg_rsop, rg_rnip);

		

		RD_ID++;
		APP_ID++;
		return true;

	}


	@Override
	public boolean acceptApplication(Integer application, String requestGeneratorURI) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}



	public void linkComputer(Computer c) throws Exception {
		this.logMessage("Linking Computer to :"+this.controllerURI);
		computerPorts.put(COMP_ID++,(ComputerServicesOutboundPort)c.findPortFromURI(findOutboundPortURIsFromInterface(ComputerServicesI.class)[0]));

	}

	public void linkComputer(ComputerServicesOutboundPort c_out) throws Exception {
		this.logMessage("Linking Computer to :"+this.controllerURI);
		computerPorts.put(COMP_ID++,c_out);

	}


}
