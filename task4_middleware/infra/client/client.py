import sys
import Ice
sys.path.append("./client/generated")
import ServantManagement 

calc_options = {
    "sum": ServantManagement.CalculationType.sum,
    "sumdis": ServantManagement.CalculationType.sumdis,
}


def main():
    with Ice.initialize(sys.argv, "client/config.client") as communicator:
        endpoint = communicator.getProperties().getProperty("Endpoint")

        while True:
            try:
                line = input("===> ")
                if line == 'big_data':
                    line = input("ARG[sum/sumdis]> ")
                    base = communicator.stringToProxy("BigDataObject/1:" + endpoint)
                    proxy = ServantManagement.IBigDataObjectPrx.checkedCast(base)
                    if not proxy:
                        print("invalid proxy")
                        continue
                    if not line in calc_options.keys():
                        print("invalid option")
                        continue  
                    result = proxy.calculateOnBigData(calc_options[line])
                    print(f"RESULT = {result}")
                elif line == 'simple':
                    line = input("ARG[id:num]> ")
                    base = communicator.stringToProxy("SimpleObject/" + line + ":" + endpoint)
                    proxy = ServantManagement.ISimpleObjectPrx.checkedCast(base)
                    if not proxy:
                        print("invalid proxy")
                        continue
                    result = proxy.getBornTime()
                    print(f"RESULT = {result}")
                elif line == 'exit':
                    break
            except Ice.Exception as ex:
                print(ex)


if __name__ == "__main__":
    main()
