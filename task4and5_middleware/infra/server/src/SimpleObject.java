import ServantManagement.ISimpleObject;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Util;

public class SimpleObject implements ISimpleObject {
	private final long borntime;
	private final int id;

	public SimpleObject(int id) {
		System.out.println("SimpleObject servant created");
		borntime = System.currentTimeMillis();
		this.id = id;
	}

	@Override
	public long getBornTime(Current current) {
		System.out.println("SimpleObject" + id + "\tgetBornTime "+ Util.identityToString(current.id));
		return borntime;
	}
}
