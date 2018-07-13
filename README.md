# sessionbugdemo

This is a demo project to reproduce Spring Session issue https://github.com/spring-projects/spring-session/issues/1111

0. Start redis on localhost:6379
1. ./gradlew bootRun
2. Open ONE tab in the browser: http://localhost:8080/
3. Login with user:password
4. Socket is connected
5. Logout
6. Everything is fine:
```
2018-07-13 21:46:09.082  INFO 15812 --- [ioEventLoop-6-1] haizz.sessionbugdemo.MyWebSocketHandler  : Connected to socket 1078b809, web session = 2bbf1d30-a12b-4fbc-b4f4-36e5a8a9cb7e
2018-07-13 21:46:15.969  INFO 15812 --- [ctor-http-nio-3] haizz.sessionbugdemo.MyWebSocketHandler  : Socket 1078b809 pingFlux finally done
2018-07-13 21:46:19.110  INFO 15812 --- [ioEventLoop-6-1] haizz.sessionbugdemo.MyWebSocketHandler  : Web session 2bbf1d30-a12b-4fbc-b4f4-36e5a8a9cb7e for connection 1078b809 is null
2018-07-13 21:46:19.112  INFO 15812 --- [ioEventLoop-6-1] haizz.sessionbugdemo.MyWebSocketHandler  : Socket 1078b809 finally done
```
7. No spring:session:sessions:2bbf1d30-a12b-4fbc-b4f4-36e5a8a9cb7e in Redis
8. Open one tab in the browser: http://localhost:8080/
9. Login
10. Socket is connected
11. Open second tab in the browser: http://localhost:8080/
12. Socket is connected
13. Logout from the first tab
14. NullPointerException
15. Redis has spring:session:sessions:3ce2870c-53af-4666-95d6-a543c203b9f9 key with single lastAccessTime attribute.

Logs:
```
2018-07-13 21:50:00.369  INFO 15812 --- [ctor-http-nio-4] haizz.sessionbugdemo.MyWebSocketHandler  : Connected to socket dd09bc3, web session = 3ce2870c-53af-4666-95d6-a543c203b9f9
2018-07-13 21:50:04.146  INFO 15812 --- [ctor-http-nio-5] haizz.sessionbugdemo.MyWebSocketHandler  : Connected to socket 5b94d99f, web session = 3ce2870c-53af-4666-95d6-a543c203b9f9
2018-07-13 21:50:07.857  INFO 15812 --- [ctor-http-nio-4] haizz.sessionbugdemo.MyWebSocketHandler  : Socket dd09bc3 pingFlux finally done
2018-07-13 21:50:14.154  INFO 15812 --- [ioEventLoop-6-1] haizz.sessionbugdemo.MyWebSocketHandler  : Web session 3ce2870c-53af-4666-95d6-a543c203b9f9 for connection 5b94d99f is null
2018-07-13 21:50:14.154  INFO 15812 --- [ctor-http-nio-5] haizz.sessionbugdemo.MyWebSocketHandler  : Socket 5b94d99f pingFlux finally done
2018-07-13 21:50:14.157  INFO 15812 --- [ioEventLoop-6-1] haizz.sessionbugdemo.MyWebSocketHandler  : Socket 5b94d99f finally done
2018-07-13 21:50:20.381 ERROR 15812 --- [ioEventLoop-6-1] haizz.sessionbugdemo.MyWebSocketHandler  : SessionCheck dd09bc3 error, sessionid = 3ce2870c-53af-4666-95d6-a543c203b9f9

java.lang.NullPointerException: null
	at org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository$SessionMapper.apply(ReactiveRedisOperationsSessionRepository.java:358) ~[spring-session-data-redis-2.0.5.BUILD-SNAPSHOT.jar:2.0.5.BUILD-SNAPSHOT]
	at org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository$SessionMapper.apply(ReactiveRedisOperationsSessionRepository.java:344) ~[spring-session-data-redis-2.0.5.BUILD-SNAPSHOT.jar:2.0.5.BUILD-SNAPSHOT]
	at reactor.core.publisher.FluxMapFuseable$MapFuseableConditionalSubscriber.onNext(FluxMapFuseable.java:254) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxFilterFuseable$FilterFuseableConditionalSubscriber.onNext(FluxFilterFuseable.java:285) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.Operators$MonoSubscriber.complete(Operators.java:1083) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoCollect$CollectSubscriber.onComplete(MonoCollect.java:142) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxDoFinally$DoFinallySubscriber.onComplete(FluxDoFinally.java:138) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxMap$MapSubscriber.onComplete(FluxMap.java:130) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxFlatMap$FlatMapMain.checkTerminated(FluxFlatMap.java:773) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxFlatMap$FlatMapMain.drainLoop(FluxFlatMap.java:543) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxFlatMap$FlatMapMain.innerComplete(FluxFlatMap.java:836) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxFlatMap$FlatMapInner.onComplete(FluxFlatMap.java:930) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoFlatMapMany$FlatMapManyInner.onComplete(MonoFlatMapMany.java:248) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxIterable$IterableSubscription.slowPath(FluxIterable.java:266) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxIterable$IterableSubscription.request(FluxIterable.java:202) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoFlatMapMany$FlatMapManyMain.onSubscribeInner(MonoFlatMapMany.java:140) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoFlatMapMany$FlatMapManyInner.onSubscribe(MonoFlatMapMany.java:233) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxIterable.subscribe(FluxIterable.java:140) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxStream.subscribe(FluxStream.java:68) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.Flux.subscribe(Flux.java:6877) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoFlatMapMany$FlatMapManyMain.onNext(MonoFlatMapMany.java:184) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoNext$NextSubscriber.onNext(MonoNext.java:76) [reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at io.lettuce.core.RedisPublisher$RedisSubscription.readAndPublish(RedisPublisher.java:390) [lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$State$3.read(RedisPublisher.java:552) [lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$State$3.onDataAvailable(RedisPublisher.java:527) [lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$RedisSubscription.onDataAvailable(RedisPublisher.java:284) [lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$RedisSubscription.onNext(RedisPublisher.java:270) [lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$SubscriptionCommand.complete(RedisPublisher.java:724) [lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.protocol.CommandHandler.complete(CommandHandler.java:598) [lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.protocol.CommandHandler.decode(CommandHandler.java:556) [lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.protocol.CommandHandler.channelRead(CommandHandler.java:508) [lettuce-core-5.0.4.RELEASE.jar:na]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.ChannelInboundHandlerAdapter.channelRead(ChannelInboundHandlerAdapter.java:86) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.ChannelInboundHandlerAdapter.channelRead(ChannelInboundHandlerAdapter.java:86) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1434) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:965) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:163) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:647) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:582) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:499) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:461) [netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884) [netty-common-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) [netty-common-4.1.25.Final.jar:4.1.25.Final]
	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_172-ea]

2018-07-13 21:50:20.391 ERROR 15812 --- [ioEventLoop-6-1] .a.w.r.e.DefaultErrorWebExceptionHandler : Failed to handle request [GET http://localhost:8080/websocket]

java.lang.NullPointerException: null
	at org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository$SessionMapper.apply(ReactiveRedisOperationsSessionRepository.java:358) ~[spring-session-data-redis-2.0.5.BUILD-SNAPSHOT.jar:2.0.5.BUILD-SNAPSHOT]
	at org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository$SessionMapper.apply(ReactiveRedisOperationsSessionRepository.java:344) ~[spring-session-data-redis-2.0.5.BUILD-SNAPSHOT.jar:2.0.5.BUILD-SNAPSHOT]
	at reactor.core.publisher.FluxMapFuseable$MapFuseableConditionalSubscriber.onNext(FluxMapFuseable.java:254) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxFilterFuseable$FilterFuseableConditionalSubscriber.onNext(FluxFilterFuseable.java:285) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.Operators$MonoSubscriber.complete(Operators.java:1083) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoCollect$CollectSubscriber.onComplete(MonoCollect.java:142) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxDoFinally$DoFinallySubscriber.onComplete(FluxDoFinally.java:138) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxMap$MapSubscriber.onComplete(FluxMap.java:130) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxFlatMap$FlatMapMain.checkTerminated(FluxFlatMap.java:773) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxFlatMap$FlatMapMain.drainLoop(FluxFlatMap.java:543) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxFlatMap$FlatMapMain.innerComplete(FluxFlatMap.java:836) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxFlatMap$FlatMapInner.onComplete(FluxFlatMap.java:930) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoFlatMapMany$FlatMapManyInner.onComplete(MonoFlatMapMany.java:248) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxIterable$IterableSubscription.slowPath(FluxIterable.java:266) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxIterable$IterableSubscription.request(FluxIterable.java:202) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoFlatMapMany$FlatMapManyMain.onSubscribeInner(MonoFlatMapMany.java:140) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoFlatMapMany$FlatMapManyInner.onSubscribe(MonoFlatMapMany.java:233) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxIterable.subscribe(FluxIterable.java:140) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxStream.subscribe(FluxStream.java:68) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.Flux.subscribe(Flux.java:6877) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoFlatMapMany$FlatMapManyMain.onNext(MonoFlatMapMany.java:184) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoNext$NextSubscriber.onNext(MonoNext.java:76) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at io.lettuce.core.RedisPublisher$RedisSubscription.readAndPublish(RedisPublisher.java:390) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$State$3.read(RedisPublisher.java:552) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$State$3.onDataAvailable(RedisPublisher.java:527) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$RedisSubscription.onDataAvailable(RedisPublisher.java:284) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$RedisSubscription.onNext(RedisPublisher.java:270) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$SubscriptionCommand.complete(RedisPublisher.java:724) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.protocol.CommandHandler.complete(CommandHandler.java:598) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.protocol.CommandHandler.decode(CommandHandler.java:556) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.protocol.CommandHandler.channelRead(CommandHandler.java:508) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.ChannelInboundHandlerAdapter.channelRead(ChannelInboundHandlerAdapter.java:86) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.ChannelInboundHandlerAdapter.channelRead(ChannelInboundHandlerAdapter.java:86) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1434) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:965) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:163) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:647) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:582) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:499) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:461) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884) ~[netty-common-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.1.25.Final.jar:4.1.25.Final]
	at java.lang.Thread.run(Thread.java:748) ~[na:1.8.0_172-ea]

2018-07-13 21:50:20.443  INFO 15812 --- [ioEventLoop-6-1] haizz.sessionbugdemo.MyWebSocketHandler  : Socket dd09bc3 finally done
2018-07-13 21:50:20.444 ERROR 15812 --- [ioEventLoop-6-1] o.s.w.s.adapter.HttpWebHandlerAdapter    : Failed to handle request [GET http://localhost:8080/websocket]

java.lang.IllegalStateException: Status and headers already sent
	at reactor.ipc.netty.http.server.HttpServerOperations.status(HttpServerOperations.java:346) ~[reactor-netty-0.7.8.RELEASE.jar:0.7.8.RELEASE]
	at org.springframework.http.server.reactive.ReactorServerHttpResponse.applyStatusCode(ReactorServerHttpResponse.java:67) ~[spring-web-5.0.7.RELEASE.jar:5.0.7.RELEASE]
	at org.springframework.http.server.reactive.AbstractServerHttpResponse.lambda$null$4(AbstractServerHttpResponse.java:214) ~[spring-web-5.0.7.RELEASE.jar:5.0.7.RELEASE]
	at reactor.core.publisher.MonoRunnable.subscribe(MonoRunnable.java:42) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.Mono.subscribe(Mono.java:3080) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxConcatIterable$ConcatIterableSubscriber.onComplete(FluxConcatIterable.java:147) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoWhen$WhenCoordinator.signal(MonoWhen.java:208) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoWhen$WhenInner.onComplete(MonoWhen.java:285) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoIgnoreElements$IgnoreElementsSubscriber.onComplete(MonoIgnoreElements.java:80) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoWhen$WhenCoordinator.signal(MonoWhen.java:208) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoWhen$WhenInner.onComplete(MonoWhen.java:285) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoNext$NextSubscriber.onComplete(MonoNext.java:96) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoNext$NextSubscriber.onNext(MonoNext.java:77) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxDoFinally$DoFinallySubscriber.onNext(FluxDoFinally.java:123) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxMap$MapSubscriber.onNext(FluxMap.java:108) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoNext$NextSubscriber.onNext(MonoNext.java:76) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxOnErrorResume$ResumeSubscriber.onNext(FluxOnErrorResume.java:73) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoFlatMapMany$FlatMapManyInner.onNext(MonoFlatMapMany.java:238) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxMap$MapSubscriber.onNext(FluxMap.java:108) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoNext$NextSubscriber.onNext(MonoNext.java:76) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at io.lettuce.core.RedisPublisher$RedisSubscription.readAndPublish(RedisPublisher.java:390) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$State$3.read(RedisPublisher.java:552) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$State$3.onDataAvailable(RedisPublisher.java:527) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$RedisSubscription.onDataAvailable(RedisPublisher.java:284) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$RedisSubscription.onNext(RedisPublisher.java:270) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.RedisPublisher$SubscriptionCommand.complete(RedisPublisher.java:724) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.protocol.CommandHandler.complete(CommandHandler.java:598) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.protocol.CommandHandler.decode(CommandHandler.java:556) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.lettuce.core.protocol.CommandHandler.channelRead(CommandHandler.java:508) ~[lettuce-core-5.0.4.RELEASE.jar:na]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.ChannelInboundHandlerAdapter.channelRead(ChannelInboundHandlerAdapter.java:86) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.ChannelInboundHandlerAdapter.channelRead(ChannelInboundHandlerAdapter.java:86) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1434) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:965) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:163) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:647) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:582) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:499) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:461) ~[netty-transport-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884) ~[netty-common-4.1.25.Final.jar:4.1.25.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.1.25.Final.jar:4.1.25.Final]
	at java.lang.Thread.run(Thread.java:748) ~[na:1.8.0_172-ea]
```
