import ServantManagement.CalculationType;
import ServantManagement.IBigDataObject;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Util;

import java.util.Random;
import java.util.stream.IntStream;

public class BigDataObject implements IBigDataObject {
	private int[] data;
	private final int id;

	public BigDataObject(int id) {
		System.out.println("BigDataObject servant created");
		data = IntStream.generate(() -> new Random().nextInt(1000)).limit(200).toArray();
		this.id = id;
	}

	@Override
	public int calculateOnBigData(CalculationType calculationType, Current current) {
		System.out.println("BigDataObject" + id + "\tcalculateOnBigData "+ Util.identityToString(current.id));
		if(calculationType.value()==0){
			return IntStream.of(data).sum(); 
		}
		return IntStream.of(data).distinct().sum();
	}
}
