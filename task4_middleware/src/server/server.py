import grpc
import time
from gen.protos import event_pb2, event_pb2_grpc
from concurrent import futures
from queue import Queue

class EventService(event_pb2_grpc.EventServiceServicer):
    def __init__(self, events_list):
        self.client_ids = set()
        self.events = events_list
        self.event_subscribers = {}  #{event1: {client_id1, client_id2}}
        self.client_notification_channels = {} #{client_id: Queue()}

    def Notify(self, request, context):
        client_id = request.client_id
        if client_id not in self.client_notification_channels:
            self.client_notification_channels[client_id] = Queue()
        queue = self.client_notification_channels[client_id]

        try:
            while context.is_active():
                try:
                    message = queue.get(timeout=30)
                    yield event_pb2.NotificationResponse(message=message)
                except Empty:
                    continue
        except Exception as e:
            print(f"Error while sending notifications to client {client_id}: {str(e)}")

    def SubscribeWeekday(self, request, context):
        pass

    def SubscribeCuisine(self, request, context):
        pass

def notify_subscribers():
    pass

def read_events(config_filename):
    events=[]
    cuisine_val = [cuisine for cuisine in event_pb2.Cuisine.values() if cuisine != 0]
    weekday_val = [weekday for weekday in event_pb2.Weekday.values() if weekday != 0]
    with open(config_filename, 'r') as file:
        cnt=0
        for line in file:
            cnt+=1
            params=line.split(";")
            event=event_pb2.Event(
                event_id=cnt,
                cuisine=cuisine_val[int(params[0])],
                weekday=weekday_val[int(params[1])],
                dish_name=params[2],
                description=params[3],
                ingredients=params[4].split(",")
            )
            events.append(event)
    return events

def serve():
    events=read_events("events_config")
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    event_pb2_grpc.add_EventServiceServicer_to_server(EventService(events), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    print("Server started at port 50051")
    try:
        #Block current thread until the server stops.
        server.wait_for_termination()
    except KeyboardInterrupt:
        server.stop(0)
    #Zastopuj wątek wysyłający notifications

if __name__ == '__main__':
    serve()