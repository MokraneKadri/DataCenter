package fr.upmc.datacenterclient.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.dispatcher.RequestDispatcher;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class TestRequestDispatcher extends AbstractCVM{

	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	// Predefined URI of the different ports visible at the component assembly
	// level.
	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;
	public static final String	ApplicationVMManagementInboundPortURI = "avm-ibp" ;
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	public static final String	ApplicationVMManagement2InboundPortURI = "avm2-ibp" ;
	public static final String	ApplicationVMManagement2OutboundPortURI = "avm2-obp" ;
	public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
	public static final String	RequestSubmissionOutboundPortURI = "rsobp" ;
	public static final String	RequestSubmissionInboundPortURI2 = "rsibp2" ;
	public static final String	RequestSubmissionOutboundPortURI2 = "rsobp2" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
	public static final String	RequestNotificationOutboundPortURI2 = "rnobp2" ;
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;

	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort				csPort ;
	/** Port connected to the computer component to receive the static
	 *  state data.															*/
	protected ComputerStaticStateDataOutboundPort		cssPort ;
	/** Port connected to the computer component to receive the dynamic
	 *  state data.															*/
	protected ComputerDynamicStateDataOutboundPort		cdsPort ;
	/** Port connected to the AVM component to allocate it cores.			*/
	protected ApplicationVMManagementOutboundPort		avmPort ;
	protected ApplicationVMManagementOutboundPort		avmPort2 ;
	/** Port of the request generator component sending requests to the
	 *  AVM component.														*/
	protected RequestSubmissionOutboundPort				rsobp ;
	/** Port of the request generator component used to receive end of
	 *  execution notifications from the AVM component.						*/
	protected RequestNotificationOutboundPort			nobp ;
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop ;

	// ------------------------------------------------------------------------
	// Component virtual machine constructors
	// ------------------------------------------------------------------------

	public	TestRequestDispatcher()
			throws Exception
	{
		super();
	}

	// ------------------------------------------------------------------------
	// Component virtual machine methods
	// ------------------------------------------------------------------------

	@Override
	public void	deploy() throws Exception
	{
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;

		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		String computerURI = "computer0" ;
		int numberOfProcessors = 2 ;
		int numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000) ;	// and at 3 GHz
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
		Computer c = new Computer(
				computerURI,
				admissibleFrequencies,
				processingPower,  
				1500,		// Test scenario 1, frequency = 1,5 GHz
				// 3000,	// Test scenario 2, frequency = 3 GHz
				1500,		// max frequency gap within a processor
				numberOfProcessors,
				numberOfCores,
				ComputerServicesInboundPortURI,
				ComputerStaticStateDataInboundPortURI,
				ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c) ;

		// Create a mock-up computer services port to later allocate its cores
		// to the application virtual machine.
		this.csPort = new ComputerServicesOutboundPort(
				ComputerServicesOutboundPortURI,
				new AbstractComponent() {}) ;
		this.csPort.publishPort() ;
		this.csPort.doConnection(
				ComputerServicesInboundPortURI,
				ComputerServicesConnector.class.getCanonicalName()) ;
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		ComputerMonitor cm =
				new ComputerMonitor(computerURI,
						true,
						ComputerStaticStateDataOutboundPortURI,
						ComputerDynamicStateDataOutboundPortURI) ;
		this.addDeployedComponent(cm) ;
		this.cssPort =
				(ComputerStaticStateDataOutboundPort)
				cm.findPortFromURI(ComputerStaticStateDataOutboundPortURI) ;
		this.cssPort.doConnection(
				ComputerStaticStateDataInboundPortURI,
				DataConnector.class.getCanonicalName()) ;

		this.cdsPort =
				(ComputerDynamicStateDataOutboundPort)
				cm.findPortFromURI(ComputerDynamicStateDataOutboundPortURI) ;
		this.cdsPort.
		doConnection(
				ComputerDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName()) ;
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create a RequestDispatcher
		// --------------------------------------------------------------------
		

		// --------------------------------------------------------------------
		// Create an Application VM component
		// --------------------------------------------------------------------
		ApplicationVM vm =
				new ApplicationVM("vm0",	// application vm component URI
						ApplicationVMManagementInboundPortURI,
						RequestSubmissionInboundPortURI,
						RequestNotificationOutboundPortURI) ;
		this.addDeployedComponent(vm) ;

		// Create a mock up port to manage the AVM component (allocate cores).
		this.avmPort = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagementOutboundPortURI,
				new AbstractComponent() {}) ;
		this.avmPort.publishPort() ;
		this.avmPort.
		doConnection(
				ApplicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;

		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		vm.toggleTracing() ;
		vm.toggleLogging() ;

		//---------------------------------------------------

		// --------------------------------------------------------------------
		// Create an Application VM component
		// --------------------------------------------------------------------
		ApplicationVM vm2 =
				new ApplicationVM("vm1",	// application vm component URI
						ApplicationVMManagement2InboundPortURI,
						RequestSubmissionInboundPortURI2,
						RequestNotificationOutboundPortURI2) ;
		this.addDeployedComponent(vm2) ;

		// Create a mock up port to manage the AVM component (allocate cores).
		this.avmPort2 = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagement2OutboundPortURI,
				new AbstractComponent() {}) ;
		this.avmPort2.publishPort() ;
		this.avmPort2.
		doConnection(
				ApplicationVMManagement2InboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;

		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		vm2.toggleTracing() ;
		vm2.toggleLogging() ;

		//---------------------------------------------------

		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------


		RequestGenerator rg =
				new RequestGenerator(
						"rg",			// generator component URI
						500.0,			// mean time between two requests
						6000000000L,	// mean number of instructions in requests
						RequestGeneratorManagementInboundPortURI,
						RequestSubmissionOutboundPortURI,
						RequestNotificationInboundPortURI) ;
		this.addDeployedComponent(rg) ;

		// Toggle on tracing and logging in the request generator to
		// follow the submission and end of execution notification of
		// individual requests.
		rg.toggleTracing() ;
		rg.toggleLogging() ;
		
		RequestDispatcher rq = new RequestDispatcher(0);
		rq.toggleTracing();
		rq.toggleLogging();
		rq.linkVM(0, vm,RequestSubmissionInboundPortURI,RequestNotificationOutboundPortURI);
		rq.linkVM(1, vm2,RequestSubmissionInboundPortURI2,RequestNotificationOutboundPortURI2);
		//RequestSubmissionInboundPortURI,
		//RequestNotificationOutboundPortURI
		
		rq.linkRequestGenerator(
				(RequestSubmissionOutboundPort) rg.findPortFromURI(RequestSubmissionOutboundPortURI) 
				, (RequestNotificationInboundPort) rg.findPortFromURI(RequestNotificationInboundPortURI));

		// Create a mock up port to manage to request generator component
		// (starting and stopping the generation).
		this.rgmop = new RequestGeneratorManagementOutboundPort(
				RequestGeneratorManagementOutboundPortURI,
				new AbstractComponent() {}) ;
		this.rgmop.publishPort() ;
		this.rgmop.doConnection(
				RequestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
		// --------------------------------------------------------------------

		// complete the deployment at the component virtual machine level.
		super.deploy();
	}


	public void	start() throws Exception
	{
		super.start() ;

		// Allocate the 4 cores of the computer to the application virtual
		// machine.
		AllocatedCore[] ac = this.csPort.allocateCores(2) ;
		this.avmPort.allocateCores(ac) ;
		
		AllocatedCore[] ac2 = this.csPort.allocateCores(2) ;
		this.avmPort2.allocateCores(ac2) ;


	}


	public void	shutdown() throws Exception
	{
		// disconnect all ports explicitly connected in the deploy phase.
		this.csPort.doDisconnection() ;
		this.avmPort.doDisconnection() ;
		this.rsobp.doDisconnection() ;
		this.nobp.doDisconnection() ;
		this.rgmop.doDisconnection() ;

		super.shutdown() ;
	}

	/**
	 * generate requests for 20 seconds and then stop generating.
	 *
	 * @throws Exception
	 */
	public void			testScenario() throws Exception
	{
		// start the request generation in the request generator.
		this.rgmop.startGeneration() ;
		// wait 20 seconds
		Thread.sleep(20000L) ;
		// then stop the generation.
		this.rgmop.stopGeneration() ;
	}

	/**
	 * execute the test application.
	 * 
	 * @param args	command line arguments, disregarded here.
	 */
	public static void	main(String[] args)
	{
		// Uncomment next line to execute components in debug mode.
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestRequestDispatcher trg = new TestRequestDispatcher() ;
			// Deploy the components
			trg.deploy() ;
			System.out.println("starting.......") ;
			// Start them.
			trg.start() ;
			// Execute the chosen request generation test scenario in a
			// separate thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						trg.testScenario() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(90000L) ;
			// Shut down the application.
			System.out.println("shutting down...") ;
			trg.shutdown() ;
			System.out.println("ending...") ;
			// Exit from Java.
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
