import grpc
import threading
from gen.protos import event_pb2, event_pb2_grpc
from concurrent import futures
from queue import Queue, Empty
from time import sleep

INTERVAL_S=10

class EventService(event_pb2_grpc.EventServiceServicer):
    def __init__(self, events_list):
        self.client_ids = set()
        self.events = events_list
        self.event_subscribers = {}  #{event1_id: {client_id1, client_id2}}
        self.client_notification_channels = {} #{client_id: Queue()}

    def ClientConnect(self, request, context):
        client_id = request.client_id
        if client_id  in self.client_notification_channels.keys():
            context.set_code(grpc.StatusCode.ALREADY_EXISTS)
            context.set_details(f"Client ID {client_id} is not available.")
            raise ValueError(f"Client ID {client_id} is not available.")
        else:
            self.client_notification_channels[client_id] = Queue()
            queue = self.client_notification_channels[client_id]

            try:
                while context.is_active():
                    try:
                        event = queue.get(timeout=30)
                        yield event
                    except Empty:
                        continue
            except Exception as e:
                print(f"Error while sending notifications to client {client_id}: {str(e)}")

    def ClientSubscribeWeekday(self, request, context):
        with self.events_lock:
            client_id = request.client_id
            weekday = request.weekday
            newly_subscribed_events = []
            for event in self.events:
                if weekday == event.day and not client_id in self.event_subscribers[event.id]:
                    newly_subscribed_events.append(event)
                    self.event_subscribers[event.id].append(client_id)

            return event_pb2.SubscriptionResponse(client_id=client_id, events_list=newly_subscribed_events)
    
    def ClientSubscribeCuisine(self, request, context):
        with self.events_lock:
            client_id = request.client_id
            cuisine = request.cuisine
            newly_subscribed_events = []
            for event in self.events:
                if cuisine == event.cuisine and not client_id in self.event_subscribers[event.id]:
                    newly_subscribed_events.append(event)
                    self.event_subscribers[event.id].append(client_id)

            return event_pb2.SubscriptionResponse(client_id=client_id, events_list=newly_subscribed_events)
    

def notify_subscribers(server):
    while True:
        for event in server.events:
            for subscriber_id in server.event_subscribers[event.id]:
                server.client_notification_channels[subscriber_id].put(event, block=False)
            sleep(INTERVAL_S)


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
                id=cnt,
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
    notifying_thread = threading.Thread(target=notify_subscribers, args=(server, ))
    notifying_thread.start()
    print("Server started at port 50051")
    try:
        #Block current thread until the server stops.
        server.wait_for_termination()
    except KeyboardInterrupt:
        server.stop(0)
    notifying_thread.join()

if __name__ == '__main__':
    serve()