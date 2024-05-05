package src.client.gen;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.62.2)", comments = "Source: protos/event.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class EventServiceGrpc {

  private EventServiceGrpc() {
  }

  public static final java.lang.String SERVICE_NAME = "EventService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<EventOuterClass.ConnectRequest, EventOuterClass.Event> getClientConnectMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/'
      + "ClientConnect", requestType = EventOuterClass.ConnectRequest.class, responseType = EventOuterClass.Event.class, methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<EventOuterClass.ConnectRequest, EventOuterClass.Event> getClientConnectMethod() {
    io.grpc.MethodDescriptor<EventOuterClass.ConnectRequest, EventOuterClass.Event> getClientConnectMethod;
    if ((getClientConnectMethod = EventServiceGrpc.getClientConnectMethod) == null) {
      synchronized (EventServiceGrpc.class) {
        if ((getClientConnectMethod = EventServiceGrpc.getClientConnectMethod) == null) {
          EventServiceGrpc.getClientConnectMethod = getClientConnectMethod = io.grpc.MethodDescriptor.<EventOuterClass.ConnectRequest, EventOuterClass.Event>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ClientConnect"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EventOuterClass.ConnectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EventOuterClass.Event.getDefaultInstance()))
              .setSchemaDescriptor(new EventServiceMethodDescriptorSupplier("ClientConnect"))
              .build();
        }
      }
    }
    return getClientConnectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<EventOuterClass.WeekdaySubscriptionRequest, EventOuterClass.SubscriptionResponse> getClientSubscribeWeekdayMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/'
      + "ClientSubscribeWeekday", requestType = EventOuterClass.WeekdaySubscriptionRequest.class, responseType = EventOuterClass.SubscriptionResponse.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<EventOuterClass.WeekdaySubscriptionRequest, EventOuterClass.SubscriptionResponse> getClientSubscribeWeekdayMethod() {
    io.grpc.MethodDescriptor<EventOuterClass.WeekdaySubscriptionRequest, EventOuterClass.SubscriptionResponse> getClientSubscribeWeekdayMethod;
    if ((getClientSubscribeWeekdayMethod = EventServiceGrpc.getClientSubscribeWeekdayMethod) == null) {
      synchronized (EventServiceGrpc.class) {
        if ((getClientSubscribeWeekdayMethod = EventServiceGrpc.getClientSubscribeWeekdayMethod) == null) {
          EventServiceGrpc.getClientSubscribeWeekdayMethod = getClientSubscribeWeekdayMethod = io.grpc.MethodDescriptor.<EventOuterClass.WeekdaySubscriptionRequest, EventOuterClass.SubscriptionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ClientSubscribeWeekday"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EventOuterClass.WeekdaySubscriptionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EventOuterClass.SubscriptionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EventServiceMethodDescriptorSupplier("ClientSubscribeWeekday"))
              .build();
        }
      }
    }
    return getClientSubscribeWeekdayMethod;
  }

  private static volatile io.grpc.MethodDescriptor<EventOuterClass.CuisineSubscriptionRequest, EventOuterClass.SubscriptionResponse> getClientSubscribeCuisineMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/'
      + "ClientSubscribeCuisine", requestType = EventOuterClass.CuisineSubscriptionRequest.class, responseType = EventOuterClass.SubscriptionResponse.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<EventOuterClass.CuisineSubscriptionRequest, EventOuterClass.SubscriptionResponse> getClientSubscribeCuisineMethod() {
    io.grpc.MethodDescriptor<EventOuterClass.CuisineSubscriptionRequest, EventOuterClass.SubscriptionResponse> getClientSubscribeCuisineMethod;
    if ((getClientSubscribeCuisineMethod = EventServiceGrpc.getClientSubscribeCuisineMethod) == null) {
      synchronized (EventServiceGrpc.class) {
        if ((getClientSubscribeCuisineMethod = EventServiceGrpc.getClientSubscribeCuisineMethod) == null) {
          EventServiceGrpc.getClientSubscribeCuisineMethod = getClientSubscribeCuisineMethod = io.grpc.MethodDescriptor.<EventOuterClass.CuisineSubscriptionRequest, EventOuterClass.SubscriptionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ClientSubscribeCuisine"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EventOuterClass.CuisineSubscriptionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EventOuterClass.SubscriptionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EventServiceMethodDescriptorSupplier("ClientSubscribeCuisine"))
              .build();
        }
      }
    }
    return getClientSubscribeCuisineMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static EventServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EventServiceStub> factory = new io.grpc.stub.AbstractStub.StubFactory<EventServiceStub>() {
      @java.lang.Override
      public EventServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
        return new EventServiceStub(channel, callOptions);
      }
    };
    return EventServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output
   * calls on the service
   */
  public static EventServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EventServiceBlockingStub> factory = new io.grpc.stub.AbstractStub.StubFactory<EventServiceBlockingStub>() {
      @java.lang.Override
      public EventServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
        return new EventServiceBlockingStub(channel, callOptions);
      }
    };
    return EventServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the
   * service
   */
  public static EventServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EventServiceFutureStub> factory = new io.grpc.stub.AbstractStub.StubFactory<EventServiceFutureStub>() {
      @java.lang.Override
      public EventServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
        return new EventServiceFutureStub(channel, callOptions);
      }
    };
    return EventServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void clientConnect(EventOuterClass.ConnectRequest request,
        io.grpc.stub.StreamObserver<EventOuterClass.Event> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getClientConnectMethod(), responseObserver);
    }

    /**
     */
    default void clientSubscribeWeekday(EventOuterClass.WeekdaySubscriptionRequest request,
        io.grpc.stub.StreamObserver<EventOuterClass.SubscriptionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getClientSubscribeWeekdayMethod(), responseObserver);
    }

    /**
     */
    default void clientSubscribeCuisine(EventOuterClass.CuisineSubscriptionRequest request,
        io.grpc.stub.StreamObserver<EventOuterClass.SubscriptionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getClientSubscribeCuisineMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service EventService.
   */
  public static abstract class EventServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return EventServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service EventService.
   */
  public static final class EventServiceStub
      extends io.grpc.stub.AbstractAsyncStub<EventServiceStub> {
    private EventServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EventServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EventServiceStub(channel, callOptions);
    }

    /**
     */
    public void clientConnect(EventOuterClass.ConnectRequest request,
        io.grpc.stub.StreamObserver<EventOuterClass.Event> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getClientConnectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void clientSubscribeWeekday(EventOuterClass.WeekdaySubscriptionRequest request,
        io.grpc.stub.StreamObserver<EventOuterClass.SubscriptionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getClientSubscribeWeekdayMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void clientSubscribeCuisine(EventOuterClass.CuisineSubscriptionRequest request,
        io.grpc.stub.StreamObserver<EventOuterClass.SubscriptionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getClientSubscribeCuisineMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service EventService.
   */
  public static final class EventServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<EventServiceBlockingStub> {
    private EventServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EventServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EventServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<EventOuterClass.Event> clientConnect(
        EventOuterClass.ConnectRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getClientConnectMethod(), getCallOptions(), request);
    }

    /**
     */
    public EventOuterClass.SubscriptionResponse clientSubscribeWeekday(
        EventOuterClass.WeekdaySubscriptionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getClientSubscribeWeekdayMethod(), getCallOptions(), request);
    }

    /**
     */
    public EventOuterClass.SubscriptionResponse clientSubscribeCuisine(
        EventOuterClass.CuisineSubscriptionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getClientSubscribeCuisineMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service
   * EventService.
   */
  public static final class EventServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<EventServiceFutureStub> {
    private EventServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EventServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EventServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<EventOuterClass.SubscriptionResponse> clientSubscribeWeekday(
        EventOuterClass.WeekdaySubscriptionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getClientSubscribeWeekdayMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<EventOuterClass.SubscriptionResponse> clientSubscribeCuisine(
        EventOuterClass.CuisineSubscriptionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getClientSubscribeCuisineMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CLIENT_CONNECT = 0;
  private static final int METHODID_CLIENT_SUBSCRIBE_WEEKDAY = 1;
  private static final int METHODID_CLIENT_SUBSCRIBE_CUISINE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CLIENT_CONNECT:
          serviceImpl.clientConnect((EventOuterClass.ConnectRequest) request,
              (io.grpc.stub.StreamObserver<EventOuterClass.Event>) responseObserver);
          break;
        case METHODID_CLIENT_SUBSCRIBE_WEEKDAY:
          serviceImpl.clientSubscribeWeekday((EventOuterClass.WeekdaySubscriptionRequest) request,
              (io.grpc.stub.StreamObserver<EventOuterClass.SubscriptionResponse>) responseObserver);
          break;
        case METHODID_CLIENT_SUBSCRIBE_CUISINE:
          serviceImpl.clientSubscribeCuisine((EventOuterClass.CuisineSubscriptionRequest) request,
              (io.grpc.stub.StreamObserver<EventOuterClass.SubscriptionResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
            getClientConnectMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
                new MethodHandlers<EventOuterClass.ConnectRequest, EventOuterClass.Event>(
                    service, METHODID_CLIENT_CONNECT)))
        .addMethod(
            getClientSubscribeWeekdayMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<EventOuterClass.WeekdaySubscriptionRequest, EventOuterClass.SubscriptionResponse>(
                    service, METHODID_CLIENT_SUBSCRIBE_WEEKDAY)))
        .addMethod(
            getClientSubscribeCuisineMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<EventOuterClass.CuisineSubscriptionRequest, EventOuterClass.SubscriptionResponse>(
                    service, METHODID_CLIENT_SUBSCRIBE_CUISINE)))
        .build();
  }

  private static abstract class EventServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    EventServiceBaseDescriptorSupplier() {
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return EventOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("EventService");
    }
  }

  private static final class EventServiceFileDescriptorSupplier
      extends EventServiceBaseDescriptorSupplier {
    EventServiceFileDescriptorSupplier() {
    }
  }

  private static final class EventServiceMethodDescriptorSupplier
      extends EventServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    EventServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (EventServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new EventServiceFileDescriptorSupplier())
              .addMethod(getClientConnectMethod())
              .addMethod(getClientSubscribeWeekdayMethod())
              .addMethod(getClientSubscribeCuisineMethod())
              .build();
        }
      }
    }
    return result;
  }
}
