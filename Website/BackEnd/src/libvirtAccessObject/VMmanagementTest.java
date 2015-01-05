package libvirtAccessObject;


import java.util.UUID;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.VcpuInfo;


public class VMmanagementTest {
	
	public long getMemory(String OSname) throws LibvirtException{
		
		long maxMemory = 0;
		try{
			Connect conn = new Connect("qemu:///system", false);
			Domain x = conn.domainLookupByName(OSname);
			
			maxMemory = x.getMaxMemory();
		}
		catch(LibvirtException e){
			e.printStackTrace();
		}
		return maxMemory;
	}
	//Return VcpuInfo[] CPU, I've got to test this,
	public VcpuInfo[] getCPU(String OSname) throws LibvirtException{
		VcpuInfo[] CPU = null;
		try {
			Connect conn = new Connect("qemu:///system", false);
			Domain x = conn.domainLookupByName(OSname);
			
			CPU = x.getVcpusInfo();
		} catch (LibvirtException e) {
			e.printStackTrace();
		}
		finally{
			return CPU;
		}
	}
	
	public void changeVM(UUID UUID, int oldMemory, int vCpu) throws LibvirtException{
			
			//setup libvirt connection, second parameter is a boolean for read-only 
			Connect conn = new Connect("qemu:///system", false);
			
			try{
				Domain x = conn.domainLookupByUUID(UUID);
				x.setMaxMemory(Long.valueOf(oldMemory));
				x.setVcpus(vCpu);
			}catch (LibvirtException e) {
				e.printStackTrace();
	}
			
			
	}
	@SuppressWarnings("finally")
	public int checkState(UUID UUID) throws LibvirtException{
		
		Connect conn = new Connect("qemu:///system", false);
		int state = 0;
		//1 if running, 0 if inactive, -1 on error
		try{
			Domain x = conn.domainLookupByUUID(UUID);
			state = x.isActive();
		}catch (LibvirtException e) {
			e.printStackTrace();
		}finally{
			return state;
		}
	}
	
	public void startDomain(UUID UUID) throws LibvirtException{
		
		Connect conn = new Connect("qemu:///system", false);
		
		try{
			Domain x = conn.domainLookupByUUID(UUID);
			int state = x.isActive();
			if(state != 1){
				x.create();
			}
		}catch (LibvirtException e) {
			e.printStackTrace();
		}
	}
}
