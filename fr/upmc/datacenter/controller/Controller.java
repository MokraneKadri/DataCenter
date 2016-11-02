package fr.upmc.datacenter.controller;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.controller.interfaces.ControllerI;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;

public class Controller extends AbstractComponent
implements ControllerI
{
	List<Computer> computers;

	public Controller(){
		computers=new ArrayList<Computer>();
	}
	
	public void addComputer(Computer c){
		computers.add(c);
	}
	
	public void createVM(){
		
	}


}
