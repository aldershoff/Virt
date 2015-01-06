package libvirtAccessObject;

import java.util.UUID;






import org.libvirt.*;
import org.libvirt.LibvirtException;

import beans.VMBean;

public class VMmanagementTest {

	public VMmanagementTest() {
		System.setProperty("jna.library.path",
				"/home/virt");
	}

	public long getMemory(String OSname) throws LibvirtException {

		long maxMemory = 0;
		try {
			Connect conn = new Connect("qemu:///system", false);
			Domain x = conn.domainLookupByName(OSname);

			maxMemory = x.getMaxMemory();
		} catch (LibvirtException e) {
			e.printStackTrace();
		}
		return maxMemory;
	}

	// //Return VcpuInfo[] CPU, I've got to test this,
	// public VcpuInfo[] getCPU(String OSname) throws LibvirtException{
	// VcpuInfo[] CPU = null;
	// try {
	// Connect conn = new Connect("qemu:///system", false);
	// Domain x = conn.domainLookupByName(OSname);
	//
	// CPU = x.getVcpusInfo();
	// } catch (LibvirtException e) {
	// e.printStackTrace();
	// }
	// finally{
	// return CPU;
	// }
	// }

	public int editVM(UUID UUID, int oldMemory, int vCpu)
			throws LibvirtException {
		int success = -1;
		
		// setup libvirt connection, second parameter is a boolean for read-only
		Connect conn = new Connect("qemu:///system", false);
		if (conn.isConnected()) {
		try {
			Domain x = conn.domainLookupByUUID(UUID);
			x.setMemory(oldMemory);
			x.setVcpus(vCpu);
			success = 1;

		} catch (LibvirtException e) {
			e.printStackTrace();
		}
		}
		else{
			success = 0;
		}
		return success;

	}

	public int checkState(UUID UUID) throws LibvirtException {

		Connect conn = new Connect("qemu:///system", true);
		int state = 0;
		if (conn.isConnected()) {
			// 1 if running, 0 if inactive, -1 on error
			try {
				Domain x = conn.domainLookupByUUID(UUID);

				state = x.isActive();
			} catch (LibvirtException e) {

				e.getError();
				e.printStackTrace();
			}

		} else {
			state = -1;
		}
		return state;
	}

	public int startVM(UUID UUID) throws LibvirtException {
		int state = 0;
		
		Connect conn = new Connect("qemu:///system", false);
		if (conn.isConnected()) {
			// 1 if running, 0 if inactive, -1 on error
			try {
				Domain x = conn.domainLookupByUUID(UUID);

				if (state != 1) {
					x.create();
					state = 1;
				}
			} catch (LibvirtException e) {

				e.getError();
				e.printStackTrace();
			}

		} else {
			state = -1;
		}
		return state;
	}

	
	public int stopVM(UUID UUID) throws LibvirtException {
		Connect conn = new Connect("qemu:///system", false);
		int state = -1;

		try {
			Domain x = conn.domainLookupByUUID(UUID);
			state = x.isActive();
			int pers = x.isPersistent();
			if (state == 1 && pers == 1) {
				x.shutdown();
				state = 0;
			}
		} catch (LibvirtException e) {
			e.printStackTrace();
		}

		return state;
	}

	public VMBean getLiveData(UUID UUID, VMBean vmBean) throws LibvirtException {
		Connect conn = new Connect("qemu:///system", true);
		int state = 0;
		if (conn.isConnected()) {
			// 1 if running, 0 if inactive, -1 on error
			try {
				Domain x = conn.domainLookupByUUID(UUID);

				state = x.isActive();
				if(state == 1){
					vmBean.setVMName(x.getName());
					vmBean.setVMOS(x.getOSType());
					vmBean.setVMCPU(Integer.toString(x.getInfo().nrVirtCpu));
					vmBean.setVMMemory(Long.toString(x.getInfo().memory));
					
					// temp setter
					vmBean.setVMSLA(Long.toString(x.getMaxMemory()));
				}
				else{
					vmBean = null;
				}
			} catch (LibvirtException e) {

				e.getError();
				e.printStackTrace();
			}

		} else {
			vmBean = null;
		}
		return vmBean;
	}
	
	 public String[] getVolume(String key) throws LibvirtException{
		 String[] storage = new String[2];
		  Connect conn = new Connect("qemu:///system", false);
		  
		  try{
		   StorageVol x = conn.storageVolLookupByKey(key);
		   storage[0] = Long.toString(x.getInfo().capacity);
		   storage[1] = Long.toString(x.getInfo().allocation);
		 
		   }catch (LibvirtException e) {
		   e.printStackTrace();
		  }
		return storage;
		 }
}
