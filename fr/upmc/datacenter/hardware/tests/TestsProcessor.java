package fr.upmc.datacenter.hardware.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorIntrospectionConnector;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorServicesConnector;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorIntrospectionOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorStaticStateDataOutboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;
import fr.upmc.datacenter.software.interfaces.RequestI;

/**
 * The class <code>TestsProcessor</code> deploys a <code>Processor</code>
 * component connected to a <code>ProcessorMonitor</code> component and then
 * execute one of two test scenarii on the simulated processor.
 *
 * <p><strong>Description</strong></p>
 * 
 * The two scenarii create a processor with two cores having two levels of
 * admissible frequencies. They then execute two tasks, one on each core and
 * respectively raise or lower the frequency of the first core to test the
 * dynamic adaptation of the task duration. In parallel, the processor
 * monitor starts the notification of the dynamic state of the processor
 * by requesting 25 pushes at the rate of one each second.
 * 
 * One scenario is activated by uncommenting its lines and commenting the
 * other's ones.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : January 19, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class		TestsProcessor
extends		AbstractCVM
{
	public static final String	ProcessorServicesInboundPortURI = "ps-ibp" ;
	public static final String	ProcessorServicesOutboundPortURI = "ps-obp" ;
	public static final String	ProcessorServicesNotificationInboundPortURI = "psn-ibp" ;
	public static final String	ProcessorIntrospectionInboundPortURI = "pi-ibp" ;
	public static final String	ProcessorIntrospectionOutboundPortURI = "pi-obp" ;
	public static final String	ProcessorManagementInboundPortURI = "pm-ibp" ;
	public static final String	ProcessorManagementOutboundPortURI = "pm-obp" ;
	public static final String	ProcessorStaticStateDataInboundPortURI = "pss-dip" ;
	public static final String	ProcessorStaticStateDataOutboundPortURI = "pss-dop" ;
	public static final String	ProcessorDynamicStateDataInboundPortURI = "pds-dip" ;
	public static final String	ProcessorDynamicStateDataOutboundPortURI = "pds-dop" ;

	protected Processor								proc ;
	protected ProcessorServicesOutboundPort			psPort ;
	protected ProcessorIntrospectionOutboundPort	piPort ;
	protected ProcessorManagementOutboundPort		pmPort ;
	protected ProcessorStaticStateDataOutboundPort	pssPort ;
	protected ProcessorDynamicStateDataOutboundPort	pdsPort ;

	public			TestsProcessor()
	throws Exception
	{
		super();
	}

	@Override
	public void		deploy() throws Exception
	{
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;

		String processorURI = "processor0" ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;
		admissibleFrequencies.add(3000) ;
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;
		processingPower.put(3000, 3000000) ;
		this.proc = new Processor(processorURI,
								  admissibleFrequencies,
								  processingPower,
								  1500,		// Test scenario 1
								  // 3000,	// Test scenario 2
								  1500,
								  2,
								  ProcessorServicesInboundPortURI,
								  ProcessorIntrospectionInboundPortURI,
								  ProcessorManagementInboundPortURI,
								  ProcessorStaticStateDataInboundPortURI,
								  ProcessorDynamicStateDataInboundPortURI) ;
		this.proc.toggleTracing() ;
		this.proc.toggleLogging() ;
		this.addDeployedComponent(this.proc) ;

		ComponentI nullComponent = new AbstractComponent() {} ;
		this.psPort =
			new ProcessorServicesOutboundPort(ProcessorServicesOutboundPortURI,
											  nullComponent) ;
		this.psPort.publishPort() ;
		this.psPort.doConnection(
				ProcessorServicesInboundPortURI,
				ProcessorServicesConnector.class.getCanonicalName()) ;

		this.piPort =
			new ProcessorIntrospectionOutboundPort(
								ProcessorIntrospectionOutboundPortURI,
								nullComponent) ;
		this.piPort.publishPort() ;
		this.piPort.doConnection(
				ProcessorIntrospectionInboundPortURI,
				ProcessorIntrospectionConnector.class.getCanonicalName()) ;

		this.pmPort = new ProcessorManagementOutboundPort(
								ProcessorManagementOutboundPortURI,
								nullComponent) ;
		this.pmPort.publishPort() ;
		this.pmPort.doConnection(
				ProcessorManagementInboundPortURI,
				ProcessorManagementConnector.class.getCanonicalName()) ;

		ProcessorMonitor pm =
			new ProcessorMonitor(
					processorURI,
					false,
					ProcessorServicesNotificationInboundPortURI,
					ProcessorStaticStateDataOutboundPortURI,
					ProcessorDynamicStateDataOutboundPortURI) ;
		this.addDeployedComponent(pm) ;
		pm.toggleLogging() ;
		pm.toggleTracing() ;
		this.pssPort =
			(ProcessorStaticStateDataOutboundPort)
				pm.findPortFromURI(ProcessorStaticStateDataOutboundPortURI) ;
		this.pssPort.doConnection(
				ProcessorStaticStateDataInboundPortURI,
				DataConnector.class.getCanonicalName()) ;

		this.pdsPort =
			(ProcessorDynamicStateDataOutboundPort)
				pm.findPortFromURI(ProcessorDynamicStateDataOutboundPortURI) ;
		this.pdsPort.doConnection(
				ProcessorDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName());

		super.deploy() ;
	}

	@Override
	public void		start() throws Exception
	{
		super.start() ;

		System.out.println("0 isValidCoreNo: " + this.piPort.isValidCoreNo(0)) ;
		System.out.println("3000 isAdmissibleFrequency: " +
					this.piPort.isAdmissibleFrequency(3000)) ;
		System.out.println("3000 is CurrentlyPossibleFrequencyForCore 0: " +
					this.piPort.isCurrentlyPossibleFrequencyForCore(0, 3000)) ;

		this.psPort.executeTaskOnCoreAndNotify(
				new TaskI() {
					private static final long serialVersionUID = 1L;
					@Override
					public RequestI getRequest() {
						return new RequestI() {
							private static final long serialVersionUID = 1L;

							@Override
							public long getPredictedNumberOfInstructions() {
								return 30000000000L;
							}

							@Override
							public String getRequestURI() {
								return "r0" ;
							}
						};
					}

					@Override
					public String getTaskURI() {
						return "mytask-001";
					}
				},
				0,
				ProcessorServicesNotificationInboundPortURI) ;

		this.psPort.executeTaskOnCoreAndNotify(
				new TaskI() {
					private static final long serialVersionUID = 1L;
					@Override
					public RequestI getRequest() {
						return new RequestI() {
							private static final long serialVersionUID = 1L;

							@Override
							public long getPredictedNumberOfInstructions() {
								return 45000000000L ;
							}

							@Override
							public String getRequestURI() {
								return "r1" ;
							}
						};
					}
					@Override
					public String getTaskURI() {
						return "mytask-002";
					}
				},
				1,
				ProcessorServicesNotificationInboundPortURI) ;

		// Test scenario 1
		Thread.sleep(10000L) ;
		this.pmPort.setCoreFrequency(0, 3000) ;
		// Test scenario 2
		// Thread.sleep(5000L) ;
		// this.pmPort.setCoreFrequency(0, 1500) ;
	}

	@Override
	public void		shutdown() throws Exception
	{
		this.psPort.doDisconnection() ;
		this.piPort.doDisconnection() ;
		this.pmPort.doDisconnection() ;

		super.shutdown();
	}

	public static void	main(String[] args)
	{
		// AbstractCVM.toggleDebugMode() ;
		try {
			AbstractCVM c = new TestsProcessor() ;
			c.deploy() ;
			System.out.println("starting...") ;
			c.start() ;
			Thread.sleep(30000L) ;
			System.out.println("shutting down...") ;
			c.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
