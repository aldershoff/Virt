package infrastructure;


import org.libvirt.Connect;
import org.libvirt.ConnectAuth;
import org.libvirt.ConnectAuthDefault;
import org.libvirt.Domain;
import org.libvirt.DomainInfo;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;
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
	
	
	
}
