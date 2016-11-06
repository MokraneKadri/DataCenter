package fr.upmc.datacenter.controller.interfaces;

public interface RequestDispatcherManagementI {

	public void deployVM(int rd, String RequestDispatcherURIDVM);

	public void destroyVM(String uriComputerParent, String vm) ;

	public void initVM(int application, String uriComputerParent, String vm);

	public void unbindVM(String uriComputerParent, String vm) throws Exception;
}
