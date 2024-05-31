from concurrent import futures
from time import sleep
from queue import Queue, Empty
import threading

import grpc

import proto.cafeteria_pb2 as pb2
import proto.cafeteria_pb2_grpc as pb2_grpc

INTERVAL_S=10

weekdays = [
    pb2.MON,
    pb2.TUE,
    pb2.WED,
    pb2.THU,
    pb2.FRI,
]

cuisines = [
    pb2.POLISH,
    pb2.ITALIAN,
    pb2.INDIAN,
    pb2.MEXICAN,
]

class CafeteriaServicer(pb2_grpc.CafeteriaServiceServicer):
    def __init__(self, stop_event, config_filename="events_config"):
        self.menus=[]
        self.channels=dict()
        self.subscribers=dict()
        self.stop=stop_event
        with open(config_filename, 'r') as file:
            cnt=0
            for line in file:
                cnt+=1
                params=line.split(";")
                menu=pb2.Menu(
                    id=cnt,
                    cuisine=int(params[0]),
                    day=int(params[1]),
                    dish_name=params[2],
                    description=params[3],
                    ingredients=params[4].split(",")
                )
                self.menus.append(menu)


    def SubscribeCuisine(self, request, context):
        print(f"Cuisine subscription received {context.peer()}")
        cuisine=request.cuisine
        if not cuisine or not cuisine in cuisines:
            context.set_details("Invalid or missing arguments.")
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            return pb2.Menu()
        
        if context.peer() in self.channels.keys(): 
            channel=self.channels[context.peer()]
        else: 
            channel=Queue()
            self.channels[context.peer()]=channel

        context.add_callback(lambda: {print(f"Lost connection {context.peer()} - cuisine {cuisine}")})

        for menu in self.menus:
            if menu.cuisine==cuisine:
                if menu.id in self.subscribers:
                   self.subscribers[menu.id].add(context.peer())
                else:
                    self.subscribers[menu.id]={context.peer()}

        while True:
            if context.is_active() and not self.stop.is_set():
                while not channel.empty():
                    try:
                        to_yield = channel.get(timeout=30)
                        yield to_yield
                    except Empty as e:
                        continue
            else: break

    def SubscribeWeekday(self, request, context):
        print(f"Weekday subscription received {context.peer()}")
        weekday=request.weekday
        if not weekday or not weekday in weekdays:
            context.set_details("Invalid or missing arguments.")
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            return pb2.Menu()
        
        if context.peer() in self.channels.keys(): 
            channel=self.channels[context.peer()]
        else: 
            channel=Queue()
            self.channels[context.peer()]=channel

        context.add_callback(lambda: {print(f"Lost connection {context.peer()} - weekday {weekday}")})

        for menu in self.menus:
            if menu.day==weekday:
                if menu.id in self.subscribers:
                    self.subscribers[menu.id].add(context.peer())
                else:
                    self.subscribers[menu.id]={context.peer()}

        while True:
            if context.is_active() and not self.stop.is_set():
                while not channel.empty():
                    try:
                        to_yield = channel.get(timeout=30)
                        yield to_yield
                    except Empty as e:
                        continue
            else: break

def notify_subscribers(service, stop):
    print("Cafeteria is open.")
    while True:
        for menu in service.menus:
            print("Today's menu: ", menu.dish_name, "."*(25-len(menu.dish_name)),"weekday:", menu.day, " cuisine: ", menu.cuisine)
            if menu.id in service.subscribers:
                for subscriber_id in service.subscribers[menu.id]:
                    service.channels[subscriber_id].put(menu, block=False)
            for i in range(INTERVAL_S):
                if(stop.is_set()): 
                    print("Cafeteria is closed.")
                    return
                else: sleep(1)
    

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    grpc_stop_mark = threading.Event()
    grpc_stop_mark.clear()
    service=CafeteriaServicer(grpc_stop_mark)
    pb2_grpc.add_CafeteriaServiceServicer_to_server(service, server)
    server.add_insecure_port('[::]:50051')
    server.start()
    print("Server started at port 50051")
    notifying_stop_mark = threading.Event()
    notifying_thread = threading.Thread(target=notify_subscribers, args=(service, notifying_stop_mark))
    notifying_thread.start()
    try:
        #Block current thread until the server stops.
        server.wait_for_termination()
    except KeyboardInterrupt:
        server.stop(0)
        grpc_stop_mark.set()
        notifying_stop_mark.set()
        print("Server stopped.")

if __name__ == '__main__':
    serve()
