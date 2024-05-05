package src.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import src.client.gen.EventServiceGrpc;
import src.client.gen.EventServiceGrpc.EventServiceStub;
import src.client.gen.EventOuterClass.Event;
import src.client.gen.EventOuterClass.SubscriptionResponse;
import src.client.gen.EventOuterClass.ConnectRequest;
import src.client.gen.EventOuterClass.CuisineSubscriptionRequest;
import src.client.gen.EventOuterClass.WeekdaySubscriptionRequest;

public class Client {
    private final ManagedChannel channel;
    private final EventServiceStub stub;
    private final int client_id;

    public Client(int client_id) {
        this.client_id = client_id;

        String serverAddress = "localhost";
        int serverPort = 50051;

        channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort)
                .usePlaintext()
                .build();

        stub = EventServiceGrpc.newStub(channel);
    }

    private void printEvent(Event event){
        synchronized (System.out) {
            System.err.println(event.getDishName());
            System.err.println(event.getDescription());
        }
    }

    private void printEventList(List<Event> events){
        synchronized (System.out) {
            events.stream().forEach(event -> {
                System.err.println(event.getDishName());
                System.err.println(event.getDescription());
                System.err.println();
            });
        }
    }

    public void connect(){
        ConnectRequest connectRequest = ConnectRequest.newBuilder()
            .setClientId(client_id)
            .build();

        stub.clientConnect(connectRequest, new StreamObserver<Event>() {
            @Override
            public void onNext(Event event) {
                printEvent(event);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("RPC ERROR");
            }

            @Override
            public void onCompleted() {
                System.out.println("Subscription finished.");
            }
        });
    }

    private void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private void listen() throws InterruptedException {
        String line = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        do {
            try {
                System.out.println("W - subscribe to weekday\nC - subscribe to cuisine\nX-exit");
                System.out.print("==> ");
                System.out.flush();
                line = in.readLine();
                switch (line) {
                    case "W": {
                        WeekdaySubscriptionRequest request = WeekdaySubscriptionRequest.newBuilder()
                                .setClientId(client_id).setWeekdayValue(1)
                                .build();
                        stub.clientSubscribeWeekday(request, new StreamObserver<SubscriptionResponse>() {
                            @Override
                            public void onNext(SubscriptionResponse response) {
                                System.err.println("Subscribed to a new weekday. Upcoming events: ");
                                printEventList(response.getEventsList());
                            }
                
                            @Override
                            public void onError(Throwable t) {
                                System.out.println("RPC ERROR");
                            }
                
                            @Override
                            public void onCompleted() {
                            }
                        });
                        break;
                    }
                    case "C": {
                        CuisineSubscriptionRequest request = CuisineSubscriptionRequest.newBuilder()
                                .setClientId(client_id).setCuisineValue(1)
                                .build();
                        stub.clientSubscribeCuisine(request, new StreamObserver<SubscriptionResponse>() {
                            @Override
                            public void onNext(SubscriptionResponse response) {
                                System.err.println("Subscribed to a new cuisine. Upcoming events: ");
                                printEventList(response.getEventsList());
                            }
                
                            @Override
                            public void onError(Throwable t) {
                                System.out.println("RPC ERROR");
                            }
                
                            @Override
                            public void onCompleted() {
                            }
                        });
                        break;
                    }
                    case "X":
                    case "":
                        break;
                    default:
                        System.out.println("???");
                        break;
                }
            } catch (java.io.IOException ex) {
                System.err.println(ex);
            }
        } while (line!=null && !line.equals("X"));

        shutdown();
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(Integer.valueOf(args[0]));
        client.listen();
    }
}
