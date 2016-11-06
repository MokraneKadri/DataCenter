package fr.upmc.datacenterclient.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.controller.Controller;
import fr.upmc.datacenter.controller.connectors.ApplicationRequestConnector;
import fr.upmc.datacenter.controller.ports.ApplicationRequestInboundPort;
import fr.upmc.datacenter.controller.ports.ApplicationRequestOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class TestController extends AbstractCVM{
	public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
	public static final String	RequestSubmissionOutboundPortURI = "rsobp" ;

	public static final String	RequestSubmissionInboundPortURI2 = "rsibp2" ;
	public static final String	RequestSubmissionOutboundPortURI2 = "rsobp2" ;

	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	public static final String	RequestNotificationOutboundPortURI = "rnobp" ;

	public static final String	RequestNotificationInboundPortURI2 = "rnibp2" ;
	public static final String	RequestNotificationOutboundPortURI2 = "rnobp2" ;

	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;

	public static final String	RequestGeneratorManagementInboundPortURI2 = "rgmip2" ;
	public static final String	RequestGeneratorManagementOutboundPortURI2 = "rgmop2" ;

	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;
	private static final String ApplicationRequestOutboundPortURI = "ar-op";
	private static final String ApplicationRequestInboundPortURI = "ar-ip";

	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort				csPort ;
	/** Port connected to the computer component to receive the static
	 *  state data.															*/
	protected ComputerStaticStateDataOutboundPort		cssPort ;
	/** Port connected to the computer component to receive the dynamic
	 *  state data.															*/
	protected ComputerDynamicStateDataOutboundPort		cdsPort ;

	/** Port of the request generator component sending requests to the
	 *  AVM component.														*/
	protected RequestSubmissionOutboundPort				rsobp ;
	/** Port of the request generator component used to receive end of
	 *  execution notifications from the AVM component.						*/
	protected RequestNotificationOutboundPort			nobp ;
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop ;
	protected RequestGeneratorManagementOutboundPort	rgmop2 ;
	
	protected RequestSubmissionOutboundPort rg_rsop;
	protected RequestNotificationInboundPort rg_rnip;
	
	protected RequestSubmissionOutboundPort rg_rsop2;
	protected RequestNotificationInboundPort rg_rnip2;

	protected ApplicationRequestOutboundPort arop;


	public TestController() throws Exception {
		super();
	}

	@Override
	public void	deploy() throws Exception
	{
		/*TODO*/
		/* Create Processor / Computer */

		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 8 cores.
		// --------------------------------------------------------------------
		String computerURI = "computer0" ;
		int numberOfProcessors = 2 ;
		int numberOfCores = 8 ;
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

		/* Create The Controller */
		Controller controller= new Controller("controller1", ApplicationRequestInboundPortURI);

		this.arop = new ApplicationRequestOutboundPort(
				ApplicationRequestOutboundPortURI,
				new AbstractComponent() {}) ;
		this.arop.publishPort() ;
		this.arop.doConnection(
				ApplicationRequestInboundPortURI,
				ApplicationRequestConnector.class.getCanonicalName()) ;

		/* Link The controller and Computer */
		controller.linkComputer(csPort);
		controller.toggleLogging();
		controller.toggleTracing();

		/* Create Request Generators */
		/* RG 1 */
		RequestGenerator rg1 =
				new RequestGenerator(
						"rg1",			// generator component URI
						500.0,			// mean time between two requests
						6000000000L,	// mean number of instructions in requests
						RequestGeneratorManagementInboundPortURI,
						RequestSubmissionOutboundPortURI,
						RequestNotificationInboundPortURI) ;
		this.addDeployedComponent(rg1) ;
		
		
		this.rgmop = new RequestGeneratorManagementOutboundPort(
				RequestGeneratorManagementOutboundPortURI,
				new AbstractComponent() {}) ;
		this.rgmop.publishPort() ;
		this.rgmop.doConnection(
				RequestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
		
		RequestGenerator rg2 =
				new RequestGenerator(
						"rg2",			// generator component URI
						500.0,			// mean time between two requests
						6000000000L,	// mean number of instructions in requests
						RequestGeneratorManagementInboundPortURI2,
						RequestSubmissionOutboundPortURI2,
						RequestNotificationInboundPortURI2) ;
		this.addDeployedComponent(rg2) ;
		
		rg_rsop=(RequestSubmissionOutboundPort) rg1.findPortFromURI(RequestSubmissionOutboundPortURI);
		rg_rnip=(RequestNotificationInboundPort) rg1.findPortFromURI(RequestNotificationInboundPortURI);
		

		
		/*RG 2 */
		this.rgmop2 = new RequestGeneratorManagementOutboundPort(
				RequestGeneratorManagementOutboundPortURI2,
				new AbstractComponent() {}) ;
		this.rgmop2.publishPort() ;
		this.rgmop2.doConnection(
				RequestGeneratorManagementInboundPortURI2,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
		
		rg_rsop2=(RequestSubmissionOutboundPort) rg2.findPortFromURI(RequestSubmissionOutboundPortURI2);
		rg_rnip2=(RequestNotificationInboundPort) rg2.findPortFromURI(RequestNotificationInboundPortURI2);
		
		rg1.toggleLogging();
		rg1.toggleTracing();
		rg2.toggleLogging();
		rg2.toggleTracing();

		super.deploy();
	}

	public void	start() throws Exception
	{
		super.start() ;
	}

	public void	shutdown() throws Exception
	{
		// disconnect all ports explicitly connected in the deploy phase.
		//		this.csPort.doDisconnection() ;
		//		this.avmPort.doDisconnection() ;
		//		this.rsobp.doDisconnection() ;
		//		this.nobp.doDisconnection() ;
		//		this.rgmop.doDisconnection() ;

		super.shutdown() ;
	}



	public void			testScenario() throws Exception
	{
		/* TODO */
		/* Send New Application to the Controller*/

		Thread.sleep(2000L) ;
		if(arop.acceptApplication(1, "rg1",rg_rsop,rg_rnip)){
			Thread.sleep(2000L) ;
			rgmop.startGeneration();
			Thread.sleep(5000L);
		}
		if(arop.acceptApplication(2, "rg2",rg_rsop2,rg_rnip2)){
			Thread.sleep(2000L) ;
			rgmop2.startGeneration();
		}
		Thread.sleep(20000L);
		rgmop.stopGeneration();
		rgmop2.stopGeneration();
		
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
			final TestController tc = new TestController() ;
			// Deploy the components
			tc.deploy() ;
			System.out.println("starting.......") ;
			// Start them.
			tc.start() ;
			// Execute the chosen request generation test scenario in a
			// separate thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						tc.testScenario() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(90000L) ;
			// Shut down the application.
			System.out.println("shutting down...") ;
			tc.shutdown() ;
			System.out.println("ending...") ;
			// Exit from Java.
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}