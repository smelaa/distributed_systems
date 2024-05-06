import ServantManagement.IBigDataObject;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.InitializationData;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.ServantLocator;

import java.util.HashMap;
import java.util.Map;

// In a nutshell, a servant locator is a local object that you implement and attach to an object adapter. 
// Once an adapter has a servant locator, it consults its active servant map (ASM) to locate a servant for an incoming request as usual. 
// If a servant for the request can be found in the ASM, the request is dispatched to that servant.
class OneToOneServantLocator implements ServantLocator {
	Map<Integer, SimpleObject> SimpleObjectsList = new HashMap<>();

	public ServantLocator.LocateResult locate(com.zeroc.Ice.Current current) {
		int id = Integer.parseInt(Util.identityToString(current.id).split("/")[1]);

		if (!SimpleObjectsList.containsKey(id)) {
			SimpleObjectsList.put(id, new SimpleObject(id));
		}

		return new LocateResult(SimpleObjectsList.get(id), null);
	}

	public void finished(com.zeroc.Ice.Current current, com.zeroc.Ice.Object servant, java.lang.Object cookie)
	{
	}

	public void deactivate(String category)
	{
	}
}

public class IceServer {
	public void t1(InitializationData initData) {
		try(Communicator communicator = Util.initialize(initData))
		{
			ObjectAdapter adapter = communicator.createObjectAdapter("Adapter");
			//ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Adapter", "tcp -h 127.0.0.2 -p 10000 -z -t 10000 : udp -h 127.0.0.2 -p 10000 -z");

			IBigDataObject bigDataObject = new BigDataObject(1);
			adapter.addDefaultServant(bigDataObject, "BigDataObject");
			adapter.addServantLocator(new OneToOneServantLocator(), "SimpleObject");

			adapter.activate();
			System.out.println("Entering event processing loop...");
			communicator.waitForShutdown();
		}

	}


	public static void main(String[] args) {
		InitializationData initData = null;
		initData = new InitializationData();
		initData.properties = Util.createProperties();
		initData.properties.load("config.server");
		IceServer app = new IceServer();
		app.t1(initData);
	}
}